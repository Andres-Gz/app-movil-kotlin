// Pruebas de la memoria y del ciclo de herramientas, con un Claude falso (no llaman a ninguna API).
// Se corren con: deno test supabase/functions/asistente

import { assert, assertEquals, assertStringIncludes } from "jsr:@std/assert@1.0.14";
import type Anthropic from "npm:@anthropic-ai/sdk@1.3.0";
import type { SupabaseClient } from "npm:@supabase/supabase-js@2.118.0";
import { describirAccion, instrucciones, type MensajeGuardado, memoria, responder } from "./conversacion.ts";
import type { Contexto } from "./herramientas.ts";

function mensaje(id: number, rol: string, contenido: string, extra: Partial<MensajeGuardado> = {}): MensajeGuardado {
  return { id_mensaje: id, rol, contenido, fecha: "2026-10-27T14:00:00+00:00", accion: null, estado_accion: null, ...extra };
}

/** Un Claude falso que contesta, en orden, las respuestas dadas y guarda lo que recibió. */
function claudeFalso(respuestas: Partial<Anthropic.Beta.BetaMessage>[]) {
  const pedidos: Anthropic.Beta.MessageCreateParamsNonStreaming[] = [];
  const cliente = {
    beta: {
      messages: {
        create: (pedido: Anthropic.Beta.MessageCreateParamsNonStreaming) => {
          // Copia los mensajes: el ciclo sigue agregando a la misma lista.
          pedidos.push({ ...pedido, messages: [...pedido.messages] });
          return Promise.resolve(respuestas[pedidos.length - 1]);
        },
      },
    },
  } as unknown as Anthropic;
  return { cliente, pedidos };
}

const contextoSinBase = (): Contexto => ({ supabase: {} as SupabaseClient, accion: null });

Deno.test("la memoria manda la conversación anterior para que recuerde lo que se dijo", () => {
  const historial = [
    mensaje(1, "user", "Recuerda este número: 70"),
    mensaje(2, "assistant", "# Anotado\nListo, el número es **70**."),
  ];

  assertEquals(memoria(historial), [
    { role: "user", content: "Recuerda este número: 70" },
    { role: "assistant", content: "# Anotado\nListo, el número es **70**." },
  ]);
});

Deno.test("la memoria guarda solo los últimos 40 mensajes y empieza con uno del usuario", () => {
  const historial = Array.from({ length: 45 }, (_, i) => mensaje(i + 1, i % 2 === 0 ? "user" : "assistant", `m${i + 1}`));

  const enviados = memoria(historial);

  // Los últimos 40 son del 6 al 45; el 6 es una respuesta, así que arranca en el 7.
  assertEquals(enviados.length, 39);
  assertEquals(enviados[0], { role: "user", content: "m7" });
});

Deno.test("la memoria le cuenta a Claude si el usuario confirmó lo que propuso", () => {
  const historial = [
    mensaje(1, "user", "Gasté 20 mil en almuerzo"),
    mensaje(2, "assistant", "# Gasto listo\nConfírmalo abajo.", {
      accion: { tipo: "gasto", monto: 20000, categoria: "comida", descripcion: "Almuerzo" },
      estado_accion: "confirmada",
    }),
  ];

  const contenido = memoria(historial)[1].content as string;

  assertStringIncludes(contenido, "Confírmalo abajo.");
  assertStringIncludes(contenido, "[Acción propuesta: gasto de $20.000 en comida (\"Almuerzo\"). Estado: el usuario la confirmó");
});

Deno.test("describe un aporte pendiente con el nombre de la meta", () => {
  const texto = describirAccion({ tipo: "aporte", monto: 150000, id_meta: 3, nombre_meta: "Viaje a Cartagena" }, "pendiente");

  assertEquals(texto, "[Acción propuesta: aporte de $150.000 a la meta \"Viaje a Cartagena\". Estado: el usuario todavía no la ha confirmado.]");
});

Deno.test("las instrucciones usan el primer nombre y la fecha de Colombia", () => {
  // 27 de octubre a las 2 de la mañana en UTC todavía es 26 de octubre en Colombia.
  const texto = instrucciones("Paula Gómez", new Date("2026-10-27T02:00:00Z"));

  assertStringIncludes(texto, "Hablas con Paula,");
  assertStringIncludes(texto, "(2026-10-26)");
});

Deno.test("responde con el historial y la pregunta nueva", async () => {
  const { cliente, pedidos } = claudeFalso([
    { stop_reason: "end_turn", content: [{ type: "text", text: "# Tu número\nEra **70**.", citations: null }] },
  ]);
  const historial = [mensaje(1, "user", "Recuerda el 70"), mensaje(2, "assistant", "# Listo\nAnotado.")];

  const respuesta = await responder(cliente, contextoSinBase(), "Paula", historial, "¿Cuál era el número?");

  assertEquals(respuesta, { texto: "# Tu número\nEra **70**.", accion: null });
  assertEquals(pedidos[0].messages.map((m) => m.content), ["Recuerda el 70", "# Listo\nAnotado.", "¿Cuál era el número?"]);
  assertEquals(pedidos[0].model, "claude-opus-5-5");
});

Deno.test("si Claude propone un gasto, la respuesta lo trae para confirmarlo y no se guarda nada", async () => {
  const { cliente, pedidos } = claudeFalso([
    {
      stop_reason: "tool_use",
      content: [{
        type: "tool_use",
        id: "uso_1",
        name: "proponer_gasto",
        input: { monto: 20000, categoria: "comida", descripcion: "Almuerzo" },
      } as Anthropic.Beta.BetaToolUseBlock],
    },
    { stop_reason: "end_turn", content: [{ type: "text", text: "# Gasto listo\nConfírmalo abajo.", citations: null }] },
  ]);

  const respuesta = await responder(cliente, contextoSinBase(), "Paula", [], "Gasté 20 mil en almuerzo");

  assertEquals(respuesta.accion, { tipo: "gasto", monto: 20000, categoria: "comida", descripcion: "Almuerzo" });
  // La segunda vuelta lleva el resultado de la herramienta.
  const resultado = (pedidos[1].messages[2].content as Anthropic.Beta.BetaToolResultBlockParam[])[0];
  assertEquals(resultado.tool_use_id, "uso_1");
  assert(!resultado.is_error);
});

Deno.test("una categoría que no existe vuelve a Claude como error para que la corrija", async () => {
  const { cliente, pedidos } = claudeFalso([
    {
      stop_reason: "tool_use",
      content: [{
        type: "tool_use",
        id: "uso_1",
        name: "proponer_gasto",
        input: { monto: 20000, categoria: "mercado", descripcion: "Mercado" },
      } as Anthropic.Beta.BetaToolUseBlock],
    },
    { stop_reason: "end_turn", content: [{ type: "text", text: "# ¿Qué categoría?\n¿Comida o gastos fijos?", citations: null }] },
  ]);

  const respuesta = await responder(cliente, contextoSinBase(), "Paula", [], "Gasté 20 mil en mercado");

  assertEquals(respuesta.accion, null);
  const resultado = (pedidos[1].messages[2].content as Anthropic.Beta.BetaToolResultBlockParam[])[0];
  assert(resultado.is_error);
  assertStringIncludes(resultado.content as string, "gastos_fijos, comida, diversion, transporte");
});

Deno.test("solo se acepta una propuesta por respuesta", async () => {
  const gasto = (id: string, monto: number) =>
    ({ type: "tool_use", id, name: "proponer_gasto", input: { monto, categoria: "comida", descripcion: "Comida" } }) as Anthropic.Beta.BetaToolUseBlock;
  const { cliente, pedidos } = claudeFalso([
    { stop_reason: "tool_use", content: [gasto("uso_1", 1000), gasto("uso_2", 2000)] },
    { stop_reason: "end_turn", content: [{ type: "text", text: "# Listo\nTe dejé el primero.", citations: null }] },
  ]);

  const respuesta = await responder(cliente, contextoSinBase(), "Paula", [], "Registra dos gastos");

  assertEquals(respuesta.accion?.monto, 1000);
  const resultados = pedidos[1].messages[2].content as Anthropic.Beta.BetaToolResultBlockParam[];
  assertEquals(resultados.map((r) => Boolean(r.is_error)), [false, true]);
});

Deno.test("si los filtros de seguridad rechazan la pregunta, responde con amabilidad y sin acción", async () => {
  const { cliente } = claudeFalso([{ stop_reason: "refusal", content: [] }]);

  const respuesta = await responder(cliente, contextoSinBase(), "Paula", [], "...");

  assertStringIncludes(respuesta.texto, "# No puedo ayudarte con eso");
  assertEquals(respuesta.accion, null);
});
