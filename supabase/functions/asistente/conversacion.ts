// La conversación con Claude: las instrucciones, la memoria y el ciclo de herramientas.

import type Anthropic from "npm:@anthropic-ai/sdk@1.3.0";
import { type Accion, type Contexto, HERRAMIENTAS, usarHerramientas } from "./herramientas.ts";

const MODELO = "claude-opus-5-5";
// Cuánto piensa Claude antes de responder (low, medium, high…): más esfuerzo, más demora y más costo.
const ESFUERZO = "medium";
const MAXIMO_TOKENS = 16000;
// Cuántas veces se le pregunta a Claude por cada mensaje: cada vuelta con herramientas suma una.
const MAXIMO_VUELTAS = 8;
// Cuántos mensajes anteriores recuerda (20 preguntas con sus respuestas).
const MENSAJES_DE_MEMORIA = 40;
const ZONA_HORARIA = "America/Bogota";
// Si los filtros de seguridad de Claude rechazan una pregunta, la API la reintenta sola con el modelo
// de respaldo que Anthropic recomienda para ese caso ("default").
const BETA_RESPALDO = "server-side-fallback-2026-07-01";

const RESPUESTA_RECHAZO =
  "# No puedo ayudarte con eso\nEsa pregunta no la puedo responder. Si quieres, revisamos tu presupuesto, tus gastos o tus metas.";

/** Un mensaje guardado, como lo devuelve mensajes_asistente (asistente.sql). */
export interface MensajeGuardado {
  id_mensaje: number;
  rol: string;
  contenido: string;
  fecha: string;
  accion: Accion | null;
  estado_accion: string | null;
}

export interface Respuesta {
  texto: string;
  /** Lo que la IA propuso guardar en esta respuesta, si propuso algo. */
  accion: Accion | null;
}

/**
 * Le pregunta a Claude. Si pide una herramienta, se ejecuta y se le devuelve el resultado, hasta que
 * responda con texto. Así puede leer los datos del usuario antes de contestar.
 */
export async function responder(
  cliente: Anthropic,
  contexto: Contexto,
  nombre: string,
  historial: MensajeGuardado[],
  pregunta: string,
  ahora: Date = new Date(),
): Promise<Respuesta> {
  const sistema = instrucciones(nombre, ahora);
  const mensajes: Anthropic.Beta.BetaMessageParam[] = [...memoria(historial), { role: "user", content: pregunta }];

  for (let vuelta = 0; vuelta < MAXIMO_VUELTAS; vuelta++) {
    const respuesta = await cliente.beta.messages.create({
      model: MODELO,
      max_tokens: MAXIMO_TOKENS,
      output_config: { effort: ESFUERZO },
      betas: [BETA_RESPALDO],
      fallbacks: "default",
      // Guarda en caché lo ya enviado (instrucciones, herramientas, historial): la siguiente vuelta sale más barata.
      cache_control: { type: "ephemeral" },
      system: sistema,
      tools: HERRAMIENTAS,
      messages: mensajes,
    });

    if (respuesta.stop_reason === "refusal") return { texto: RESPUESTA_RECHAZO, accion: null };

    const usos = respuesta.content.filter((bloque): bloque is Anthropic.Beta.BetaToolUseBlock => bloque.type === "tool_use");
    if (respuesta.stop_reason !== "tool_use" || usos.length === 0) {
      const texto = respuesta.content
        .flatMap((bloque) => (bloque.type === "text" ? [bloque.text] : []))
        .join("")
        .trim();
      if (!texto) throw new Error(`Claude no devolvió texto (stop_reason: ${respuesta.stop_reason})`);
      return { texto, accion: contexto.accion };
    }

    // La respuesta va completa y sin cambios, y después los resultados de todas las herramientas juntos.
    mensajes.push({ role: "assistant", content: respuesta.content });
    mensajes.push({ role: "user", content: await usarHerramientas(usos, contexto) });
  }
  throw new Error("Claude pidió demasiadas herramientas sin responder");
}

/**
 * La memoria del chat. Claude no recuerda nada entre una pregunta y otra: en cada una se le vuelven a
 * mandar los últimos mensajes guardados. Si una respuesta propuso una acción, se le agrega al final qué
 * decidió el usuario, para que sepa si quedó guardada.
 */
export function memoria(historial: MensajeGuardado[]): Anthropic.Beta.BetaMessageParam[] {
  const recientes = historial.slice(-MENSAJES_DE_MEMORIA);
  // Lo que se le manda a Claude tiene que empezar con un mensaje del usuario.
  const inicio = recientes.findIndex((mensaje) => mensaje.rol === "user");
  if (inicio < 0) return [];
  return recientes.slice(inicio).map((mensaje): Anthropic.Beta.BetaMessageParam =>
    mensaje.rol === "assistant"
      ? {
        role: "assistant",
        content: mensaje.accion ? `${mensaje.contenido}\n\n${describirAccion(mensaje.accion, mensaje.estado_accion)}` : mensaje.contenido,
      }
      : { role: "user", content: mensaje.contenido }
  );
}

export function describirAccion(accion: Accion, estado: string | null): string {
  const monto = pesos(accion.monto);
  const propuesta = accion.tipo === "gasto"
    ? `gasto de ${monto} en ${accion.categoria} ("${accion.descripcion}")`
    : accion.tipo === "ingreso"
    ? `ingreso ${accion.frecuencia} de ${monto} ("${accion.descripcion}")`
    : `aporte de ${monto} a la meta "${accion.nombre_meta}"`;
  const decision = estado === "confirmada"
    ? "el usuario la confirmó y quedó guardada"
    : estado === "cancelada"
    ? "el usuario la canceló"
    : "el usuario todavía no la ha confirmado";
  return `[Acción propuesta: ${propuesta}. Estado: ${decision}.]`;
}

function pesos(valor: number): string {
  return `$${new Intl.NumberFormat("es-CO", { maximumFractionDigits: 0 }).format(valor)}`;
}

/** Las instrucciones de Finanzia IA. Cambian una vez al día (por la fecha), así se aprovecha la caché. */
export function instrucciones(nombre: string, ahora: Date): string {
  const primerNombre = nombre.trim().split(/\s+/)[0] || "el usuario";
  const hoy = new Intl.DateTimeFormat("es-CO", { dateStyle: "full", timeZone: ZONA_HORARIA }).format(ahora);
  // en-CA escribe la fecha como AAAA-MM-DD, el formato de las herramientas.
  const hoyIso = new Intl.DateTimeFormat("en-CA", { timeZone: ZONA_HORARIA }).format(ahora);
  return `Eres Finanzia IA, el copiloto financiero de la app Finanzia. Hablas con ${primerNombre}, en español de Colombia, con un tono cercano y claro, como un amigo que sabe de finanzas.

Hoy es ${hoy} (${hoyIso}), hora de Colombia. Los montos son pesos colombianos: escríbelos con punto de miles y sin decimales, por ejemplo $20.000.

Sus datos
Con las herramientas lees los datos reales de ${primerNombre}: consultar_resumen (balance, totales y últimos movimientos), consultar_movimientos (lo de un periodo, con gastos por categoría) y consultar_metas (sus metas de ahorro). Consulta antes de dar cifras sobre su dinero y no inventes datos; si algo no está registrado en la app, díselo. El balance es el saldo inicial de las cuentas más los ingresos menos los gastos.

Registrar
Si pide registrar un gasto, un ingreso o un aporte a una meta, usa proponer_gasto, proponer_ingreso o proponer_aporte. Proponer no guarda nada: la app le muestra una tarjeta con los botones Confirmar y Cancelar, y solo se guarda si confirma. Cuéntale en una frase qué preparaste y que lo confirme en la tarjeta. Si falta un dato que no puedes deducir (el monto, o a qué meta va el aporte), pregúntalo antes de proponer. Propón una sola acción por respuesta. En tus mensajes anteriores, lo que propusiste aparece al final como "[Acción propuesta: …]" con lo que decidió el usuario; no escribas tú esa marca.

Formato
Es un chat en un teléfono: responde corto, en uno a tres párrafos breves. La primera línea es siempre un título de dos a cuatro palabras que empieza con "# " (por ejemplo "# Diagnóstico presupuestario"); después va el texto. Resalta las cifras clave con **negrita**. Para listas, usa líneas que empiecen con "• ". No uses tablas ni otros encabezados.

Límites
Ayudas con presupuesto, gastos, ahorro, metas y hábitos. No recomiendas inversiones concretas (acciones, criptomonedas o fondos específicos): si te lo piden, explica que no eres un asesor financiero certificado y ofrece ayuda con su presupuesto.`;
}
