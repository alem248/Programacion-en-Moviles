import OpenAI from "openai";
import { initializeApp } from "firebase-admin/app";
import { FieldValue, getFirestore } from "firebase-admin/firestore";
import { HttpsError, onCall } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";

initializeApp();
const firestore = getFirestore();
const openAiApiKey = defineSecret("OPENAI_API_KEY");
const maxCharacters = 5_000;
const contextMessageCount = 20;

export const responderMentor = onCall(
  { secrets: [openAiApiKey], timeoutSeconds: 90, memory: "256MiB" },
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
    const historial = previos.docs.reverse().flatMap((documento) => {
      const contenido = documento.get("texto");
      const rol = documento.get("rol");
      return typeof contenido === "string" && (rol === "usuario" || rol === "mentora")
        ? [{ role: rol === "usuario" ? "user" as const : "assistant" as const, content: contenido }]
        : [];
    });

    await mensajes.add({ texto, rol: "usuario", creadoEn: FieldValue.serverTimestamp() });

    try {
      const openai = new OpenAI({ apiKey: openAiApiKey.value() });
      const response = await openai.responses.create({
        model: "gpt-6-astra",
        instructions: "Eres la mentora de programación de CodeRole, una compañera amable con personalidad cálida y expresiva. Responde en español, explica conceptos con claridad y ejemplos pequeños, guía paso a paso y adapta la profundidad al nivel del usuario. Mantén el foco en aprendizaje y desarrollo de software. No afirmes haber ejecutado código ni acceder a archivos del usuario.",
        input: [...historial, { role: "user", content: texto }],
        max_output_tokens: 900,
      });
      const respuesta = response.output_text.trim();
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
