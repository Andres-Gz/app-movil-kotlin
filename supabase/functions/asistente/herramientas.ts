// Las herramientas de Finanzia IA: lo que Claude puede pedir mientras arma una respuesta.
//
// Las de consulta leen los datos del usuario con las funciones SQL de la app, usando su token: la IA
// solo ve lo que el usuario vería. Las de proponer no guardan nada: dejan lista una acción que la app
// muestra con los botones Confirmar y Cancelar, y que solo se ejecuta si el usuario confirma
// (confirmar_accion_asistente en asistente.sql).

import type Anthropic from "npm:@anthropic-ai/sdk@1.3.0";
import type { SupabaseClient } from "npm:@supabase/supabase-js@2.118.0";

/** Lo que la IA propone guardar. Va a `mensajes.accion` y la app lo muestra para confirmarlo. */
export type Accion =
  | { tipo: "gasto"; monto: number; categoria: string; descripcion: string }
  | { tipo: "ingreso"; monto: number; frecuencia: string; descripcion: string }
  | { tipo: "aporte"; monto: number; id_meta: number; nombre_meta: string };

/** Lo que comparten las herramientas mientras Claude arma una respuesta. */
export interface Contexto {
  supabase: SupabaseClient;
  /** La acción propuesta en esta respuesta: una como máximo. */
  accion: Accion | null;
}

/** Error de una función SQL, con su código de Postgres (22023: dato no válido, P0002: no existe…). */
export class ErrorBaseDatos extends Error {
  constructor(readonly codigo: string, mensaje: string) {
    super(mensaje);
  }
}

/** Un dato que Claude mandó mal: se le cuenta para que lo corrija. */
class ErrorHerramienta extends Error {}

// Las mismas claves que aceptan registrar_gasto (gastos.sql) y registrar_ingreso (ingresos.sql).
const CATEGORIAS_GASTO = ["gastos_fijos", "comida", "diversion", "transporte"];
const FRECUENCIAS_INGRESO = ["mensual", "quincenal", "semanal", "diario"];
const LARGO_MAXIMO_DESCRIPCION = 120;
// Hasta cientos de miles de millones de pesos, como en la app.
const MONTO_MAXIMO = 999_999_999_999;
const PATRON_FECHA = /^\d{4}-\d{2}-\d{2}$/;
// Códigos de Postgres que vienen de un dato mal enviado, no de una falla.
const CODIGOS_DATO_INVALIDO = ["22023", "22007", "22008", "P0002"];

const sinParametros = () => ({ type: "object" as const, properties: {}, required: [], additionalProperties: false });

export const HERRAMIENTAS: Anthropic.Beta.BetaTool[] = [
  {
    name: "consultar_resumen",
    description:
      "Resumen de las finanzas del usuario: su nombre, el saldo inicial de sus cuentas, el total de ingresos y de " +
      "gastos de siempre, cuántos movimientos tiene, sus 4 últimos movimientos y su meta más reciente con lo ahorrado.",
    strict: true,
    input_schema: sinParametros(),
  },
  {
    name: "consultar_movimientos",
    description:
      "Movimientos del usuario entre dos fechas (ambas incluidas, en hora de Colombia), con el total de ingresos, " +
      "el total de gastos y los gastos por categoría. Lista hasta 100 movimientos, los más recientes; los totales cuentan todos.",
    strict: true,
    input_schema: {
      type: "object",
      properties: {
        desde: { type: "string", format: "date", description: "Primer día, AAAA-MM-DD." },
        hasta: { type: "string", format: "date", description: "Último día, AAAA-MM-DD." },
      },
      required: ["desde", "hasta"],
      additionalProperties: false,
    },
  },
  {
    name: "consultar_metas",
    description:
      "Las metas de ahorro del usuario: id_meta, nombre, objetivo, lo ahorrado, fecha límite, categoría y regla de " +
      "aporte (monto y frecuencia), más las fechas de sus aportes del último año.",
    strict: true,
    input_schema: sinParametros(),
  },
  {
    name: "proponer_gasto",
    description:
      "Prepara un gasto para que el usuario lo confirme en la app. No lo guarda: la app muestra la propuesta con " +
      "los botones Confirmar y Cancelar.",
    strict: true,
    input_schema: {
      type: "object",
      properties: {
        monto: { type: "integer", description: "Pesos colombianos, mayor que cero." },
        categoria: {
          type: "string",
          enum: CATEGORIAS_GASTO,
          description: "gastos_fijos (arriendo, servicios, planes), comida, diversion (salidas, ocio) o transporte.",
        },
        descripcion: { type: "string", description: "Corta, por ejemplo \"Almuerzo\"." },
      },
      required: ["monto", "categoria", "descripcion"],
      additionalProperties: false,
    },
  },
  {
    name: "proponer_ingreso",
    description:
      "Prepara un ingreso para que el usuario lo confirme en la app. No lo guarda: la app muestra la propuesta con " +
      "los botones Confirmar y Cancelar.",
    strict: true,
    input_schema: {
      type: "object",
      properties: {
        monto: { type: "integer", description: "Pesos colombianos, mayor que cero." },
        frecuencia: {
          type: "string",
          enum: FRECUENCIAS_INGRESO,
          description: "Cada cuánto recibe este ingreso. Si es algo de una sola vez, usa mensual.",
        },
        descripcion: { type: "string", description: "Corta, por ejemplo \"Salario\" o \"Venta de bicicleta\"." },
      },
      required: ["monto", "frecuencia", "descripcion"],
      additionalProperties: false,
    },
  },
  {
    name: "proponer_aporte",
    description:
      "Prepara un aporte a una meta de ahorro para que el usuario lo confirme en la app. No lo guarda. " +
      "Usa el id_meta que devuelve consultar_metas.",
    strict: true,
    input_schema: {
      type: "object",
      properties: {
        id_meta: { type: "integer", description: "El id_meta de la meta, según consultar_metas." },
        monto: { type: "integer", description: "Pesos colombianos, mayor que cero." },
      },
      required: ["id_meta", "monto"],
      additionalProperties: false,
    },
  },
];

/**
 * Ejecuta las herramientas que pidió Claude y devuelve sus resultados, todos juntos, como los espera la API.
 * Van una por una para que dos propuestas en la misma respuesta no se pisen.
 */
export async function usarHerramientas(
  usos: Anthropic.Beta.BetaToolUseBlock[],
  contexto: Contexto,
): Promise<Anthropic.Beta.BetaToolResultBlockParam[]> {
  const resultados: Anthropic.Beta.BetaToolResultBlockParam[] = [];
  for (const uso of usos) {
    try {
      const resultado = await ejecutar(uso.name, uso.input as Record<string, unknown>, contexto);
      resultados.push({ type: "tool_result", tool_use_id: uso.id, content: JSON.stringify(resultado) });
    } catch (error) {
      resultados.push({ type: "tool_result", tool_use_id: uso.id, content: explicarError(uso.name, error), is_error: true });
    }
  }
  return resultados;
}

/** Llama una función SQL con el token del usuario. */
export async function llamarFuncion(
  supabase: SupabaseClient,
  funcion: string,
  parametros?: Record<string, unknown>,
): Promise<unknown> {
  const { data, error } = await supabase.rpc(funcion, parametros);
  if (error) throw new ErrorBaseDatos(error.code, `${funcion}: ${error.message}`);
  return data;
}

async function ejecutar(nombre: string, datos: Record<string, unknown>, contexto: Contexto): Promise<unknown> {
  switch (nombre) {
    case "consultar_resumen":
      return await llamarFuncion(contexto.supabase, "resumen_inicio");
    case "consultar_movimientos":
      return await llamarFuncion(contexto.supabase, "movimientos_asistente", {
        p_desde: fecha(datos.desde),
        p_hasta: fecha(datos.hasta),
      });
    case "consultar_metas":
      return await llamarFuncion(contexto.supabase, "resumen_metas");
    case "proponer_gasto":
      return proponer(contexto, {
        tipo: "gasto",
        monto: monto(datos.monto),
        categoria: opcion(datos.categoria, CATEGORIAS_GASTO),
        descripcion: descripcion(datos.descripcion),
      });
    case "proponer_ingreso":
      return proponer(contexto, {
        tipo: "ingreso",
        monto: monto(datos.monto),
        frecuencia: opcion(datos.frecuencia, FRECUENCIAS_INGRESO),
        descripcion: descripcion(datos.descripcion),
      });
    case "proponer_aporte":
      revisarSinPropuesta(contexto);
      return proponer(contexto, await aporte(contexto.supabase, datos));
    default:
      throw new ErrorHerramienta(`No existe la herramienta ${nombre}.`);
  }
}

function proponer(contexto: Contexto, accion: Accion) {
  revisarSinPropuesta(contexto);
  contexto.accion = accion;
  return {
    estado: "propuesta_lista",
    nota: "La app le muestra al usuario esta propuesta con los botones Confirmar y Cancelar. Todavía no está guardada.",
    propuesta: accion,
  };
}

function revisarSinPropuesta(contexto: Contexto) {
  if (contexto.accion !== null) {
    throw new ErrorHerramienta("Ya propusiste una acción en esta respuesta; propón una sola por respuesta.");
  }
}

/** El aporte con el nombre real de la meta, que es el que ve el usuario al confirmar. */
async function aporte(supabase: SupabaseClient, datos: Record<string, unknown>): Promise<Accion> {
  const idMeta = datos.id_meta;
  const montoAporte = monto(datos.monto);
  const resumen = await llamarFuncion(supabase, "resumen_metas") as { metas: { id_meta: number; nombre: string }[] };
  const meta = resumen.metas.find((m) => m.id_meta === idMeta);
  if (!meta) throw new ErrorHerramienta("Esa meta no existe. Usa consultar_metas para ver las metas del usuario y sus id_meta.");
  return { tipo: "aporte", monto: montoAporte, id_meta: meta.id_meta, nombre_meta: meta.nombre };
}

function monto(valor: unknown): number {
  if (typeof valor !== "number" || !Number.isInteger(valor) || valor <= 0 || valor > MONTO_MAXIMO) {
    throw new ErrorHerramienta("El monto debe ser un número entero de pesos, mayor que cero.");
  }
  return valor;
}

function opcion(valor: unknown, opciones: string[]): string {
  if (typeof valor !== "string" || !opciones.includes(valor)) {
    throw new ErrorHerramienta(`Usa una de estas opciones: ${opciones.join(", ")}.`);
  }
  return valor;
}

function descripcion(valor: unknown): string {
  const texto = typeof valor === "string" ? valor.trim() : "";
  if (!texto) throw new ErrorHerramienta("Falta la descripción.");
  return texto.slice(0, LARGO_MAXIMO_DESCRIPCION);
}

function fecha(valor: unknown): string {
  if (typeof valor !== "string" || !PATRON_FECHA.test(valor) || Number.isNaN(Date.parse(valor))) {
    throw new ErrorHerramienta("Las fechas van en formato AAAA-MM-DD.");
  }
  return valor;
}

/** Lo que se le cuenta a Claude cuando una herramienta falla: el motivo si fue un dato mal enviado. */
function explicarError(herramienta: string, error: unknown): string {
  if (error instanceof ErrorHerramienta) return error.message;
  if (error instanceof ErrorBaseDatos && CODIGOS_DATO_INVALIDO.includes(error.codigo)) return error.message;
  console.error(`La herramienta ${herramienta} falló:`, error instanceof Error ? error.message : error);
  return "No se pudo consultar en este momento. Cuéntale al usuario que hubo un problema y que intente más tarde.";
}
