// Edge Function "asistente": el chat de Finanzia con Claude.
//
// La app manda {"id_conversacion": 12, "mensaje": "..."} (id_conversacion vacío para un chat nuevo) con
// el token de la sesión, y recibe la pregunta y la respuesta ya guardadas:
//   1. Comprueba la sesión. Sin una sesión válida no se llama a la IA (cada llamada cuesta).
//   2. Lee los mensajes anteriores de la conversación: son la memoria del chat.
//   3. Le pregunta a Claude (conversacion.ts), que usa las herramientas de herramientas.ts para leer los
//      datos del usuario o proponer un gasto, un ingreso o un aporte.
//   4. Guarda la pregunta y la respuesta, con la acción propuesta si hay, y las devuelve.
// Todas las consultas a la base van con el token del usuario, así que la IA solo ve sus datos.
// La llave de Anthropic es el secreto ANTHROPIC_API_KEY de Supabase: nunca llega a la app.

import Anthropic from "npm:@anthropic-ai/sdk@1.3.0";
import { createClient, type SupabaseClient } from "npm:@supabase/supabase-js@2.118.0";
import { type MensajeGuardado, responder } from "./conversacion.ts";
import { ErrorBaseDatos, llamarFuncion } from "./herramientas.ts";

const LARGO_MAXIMO_PREGUNTA = 2000;
// Código de Postgres cuando la conversación no existe o es de otra persona.
const NO_ENCONTRADO = "P0002";

Deno.serve(async (peticion) => {
  if (peticion.method !== "POST") return responderJson(405, { error: "metodo_no_permitido" });

  const llave = Deno.env.get("ANTHROPIC_API_KEY");
  if (!llave) {
    console.error("Falta el secreto ANTHROPIC_API_KEY (Edge Functions → Secrets en Supabase)");
    return responderJson(500, { error: "ia_sin_configurar" });
  }

  const autorizacion = peticion.headers.get("Authorization") ?? "";
  const supabase = createClient(Deno.env.get("SUPABASE_URL") ?? "", clavePublica(peticion), {
    global: { headers: { Authorization: autorizacion } },
    auth: { persistSession: false, autoRefreshToken: false },
  });
  const { data: sesion } = await supabase.auth.getUser(autorizacion.replace(/^Bearer\s+/i, ""));
  if (!sesion.user) return responderJson(401, { error: "sin_sesion" });

  const cuerpo = await peticion.json().catch(() => null);
  const pregunta = typeof cuerpo?.mensaje === "string" ? cuerpo.mensaje.trim() : "";
  const conversacion = Number.isInteger(cuerpo?.id_conversacion) ? cuerpo.id_conversacion as number : null;
  if (!pregunta || pregunta.length > LARGO_MAXIMO_PREGUNTA) return responderJson(400, { error: "pregunta_no_valida" });

  try {
    const historial = conversacion === null
      ? []
      : await llamarFuncion(supabase, "mensajes_asistente", { p_conversacion: conversacion }) as MensajeGuardado[];
    const respuesta = await responder(
      new Anthropic({ apiKey: llave }),
      { supabase, accion: null },
      await nombreDelUsuario(supabase),
      historial,
      pregunta,
    );
    const guardado = await llamarFuncion(supabase, "guardar_intercambio_asistente", {
      p_conversacion: conversacion,
      p_pregunta: pregunta,
      p_respuesta: respuesta.texto,
      p_accion: respuesta.accion,
    });
    return responderJson(200, guardado);
  } catch (error) {
    return responderError(error);
  }
});

/** La clave publicable con que llamó la app; si no la mandó, la del proyecto. */
function clavePublica(peticion: Request): string {
  return peticion.headers.get("apikey") ?? Deno.env.get("SUPABASE_ANON_KEY") ?? "";
}

/** El nombre del perfil: la regla RLS de `usuarios` solo deja leer la fila propia. */
async function nombreDelUsuario(supabase: SupabaseClient): Promise<string> {
  const { data } = await supabase.from("usuarios").select("nombre").maybeSingle();
  return typeof data?.nombre === "string" ? data.nombre : "";
}

function responderJson(estado: number, cuerpo: unknown): Response {
  return new Response(JSON.stringify(cuerpo), { status: estado, headers: { "Content-Type": "application/json" } });
}

/** Traduce cada falla a un código que la app entiende. El detalle queda solo en los registros de Supabase. */
function responderError(error: unknown): Response {
  console.error("El asistente falló:", error instanceof Error ? `${error.name}: ${error.message}` : error);
  if (error instanceof ErrorBaseDatos && error.codigo === NO_ENCONTRADO) {
    return responderJson(404, { error: "conversacion_no_encontrada" });
  }
  if (error instanceof Anthropic.RateLimitError) return responderJson(429, { error: "ia_ocupada" });
  if (error instanceof Anthropic.AuthenticationError || error instanceof Anthropic.PermissionDeniedError) {
    return responderJson(500, { error: "ia_sin_configurar" });
  }
  if (error instanceof Anthropic.APIConnectionError) return responderJson(503, { error: "ia_sin_conexion" });
  if (error instanceof Anthropic.APIError) return responderJson(502, { error: "ia_no_disponible" });
  return responderJson(500, { error: "error_interno" });
}
