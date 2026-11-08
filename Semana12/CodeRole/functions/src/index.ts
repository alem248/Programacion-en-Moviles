import { initializeApp } from "firebase-admin/app";
import { FieldValue, getFirestore } from "firebase-admin/firestore";
import { HttpsError, onCall } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";

initializeApp();
const firestore = getFirestore();
const geminiApiKey = defineSecret("GEMINI_API_KEY");
const maxCharacters = 5_000;
const contextMessageCount = 20;

const endpoint = "https://generativelanguage.googleapis.com/v1beta/models/";
const models = ["gemini-3.5-flash", "gemini-flash-latest", "gemini-flash-lite-latest"];
const instructions =
  "Eres la mentora de programación de CodeRole, una compañera amable con personalidad cálida y " +
  "expresiva. Respondes en español, explicas conceptos con claridad y ejemplos pequeños, guías " +
  "paso a paso y adaptas la profundidad al nivel del usuario. Mantén el foco en aprendizaje y " +
  "desarrollo de software. No afirmes haber ejecutado código ni acceder a archivos del usuario.";

type Turno = { role: "user" | "model"; content: string };

/**
 * Versión de servidor del chat: usa la misma clave de Gemini que la app pero la
 * guarda como secreto de Functions. Es la alternativa cuando se prefiera generar
 * la respuesta sin exponer la clave en el APK.
 */
export const responderMentor = onCall(
  { secrets: [geminiApiKey], timeoutSeconds: 90, memory: "256MiB" },
  async (request) => {
    const uid = request.auth?.uid;
    if (!uid) throw new HttpsError("unauthenticated", "Inicia sesión para conversar con tu mentora.");

    const texto = typeof request.data?.texto === "string" ? request.data.texto.trim() : "";
    if (!texto) throw new HttpsError("invalid-argument", "El mensaje no puede estar vacío.");
    if (texto.length > maxCharacters) {
      throw new HttpsError("invalid-argument", "El mensaje supera el límite de 5 000 caracteres.");
    }

    const conversacionId = "mentor";
    const mensajes = firestore.collection("usuarios").doc(uid)
      .collection("conversaciones").doc(conversacionId).collection("mensajes");
    const previos = await mensajes.orderBy("creadoEn", "desc").limit(contextMessageCount).get();
    const historial: Turno[] = previos.docs.reverse().flatMap((documento) => {
      const contenido = documento.get("texto");
      const rol = documento.get("rol");
      return typeof contenido === "string" && (rol === "usuario" || rol === "mentora")
        ? [{ role: rol === "usuario" ? ("user" as const) : ("model" as const), content: contenido }]
        : [];
    });

    await mensajes.add({ texto, rol: "usuario", creadoEn: FieldValue.serverTimestamp() });

    try {
      const respuesta = await callGemini([...historial.map((turno) => ({ role: turno.role, content: turno.content })), { role: "user" as const, content: texto }]);
      if (!respuesta) throw new Error("El modelo devolvió una respuesta vacía.");

      const documentoRespuesta = await mensajes.add({
        texto: respuesta,
        rol: "mentora",
        creadoEn: FieldValue.serverTimestamp(),
      });
      return { respuesta, mensajeId: documentoRespuesta.id };
    } catch (error) {
      console.error("Falló la respuesta de la mentora", error);
      throw new HttpsError("unavailable", "No pudimos generar una respuesta ahora. Tu mensaje quedó guardado; inténtalo de nuevo.");
    }
  },
);

async function callGemini(turnos: Turno[]): Promise<string> {
  const body = JSON.stringify({
    systemInstruction: { parts: [{ text: instructions }] },
    contents: turnos.map((turno) => ({ role: turno.role, parts: [{ text: turno.content }] })),
    generationConfig: { maxOutputTokens: 4_096, temperature: 0.8 },
  });

  for (const model of models) {
    const response = await fetch(`${endpoint}${model}:generateContent`, {
      method: "POST",
      headers: { "Content-Type": "application/json", "x-goog-api-key": geminiApiKey.value() },
      body,
    });
    if (response.ok) {
      const data = (await response.json()) as {
        candidates?: Array<{ content?: { parts?: Array<{ text?: string }> } }>;
      };
      const text = (data.candidates ?? [])
        .map((candidate) => (candidate.content?.parts ?? []).map((part) => part.text ?? "").join(""))
        .join("\n")
        .trim();
      if (text) return text;
    } else if (![404, 429, 500, 503].includes(response.status)) {
      console.error(`Gemini rechazó la solicitud en ${model}: ${response.status} ${await response.text()}`);
      break;
    } else {
      console.warn(`Gemini devolvió ${response.status} en ${model}, se prueba el siguiente modelo.`);
    }
  }
  throw new Error("Gemini no respondió con ningún modelo disponible.");
}
