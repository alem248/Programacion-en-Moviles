package com.quispe.coderole.domain.model

/**
 * Grupos de piezas del mentor. El panel de administración lista las piezas
 * grupo por grupo para que sea fácil localizar la que se quiere cambiar.
 */
enum class GrupoCapa(val titulo: String) {
    ESTRUCTURA("Cuerpo, cabello y accesorios"),
    CEJAS("Cejas (expresión)"),
    OJOS("Ojos (expresión)"),
    BOCA("Boca (expresión)"),
    PARPADEO("Parpadeo"),
}

/**
 * Piezas modulares del personaje. Cada pieza puede llevar un PNG personalizado
 * guardado en `configuraciones/personaje/capas/{clave}`. Si no tiene uno, se
 * muestra el PNG o SVG incluido en la app.
 *
 * El orden de declaración no importa para el dibujado: la pinta la vista
 * [com.quispe.coderole.ui.components.ModularCharacterView].
 */
enum class CapaPersonaje(
    /** Identificador del documento en la subcolección `capas`. */
    val clave: String,
    val titulo: String,
    val grupo: GrupoCapa,
    /** Emoción a la que pertenece la pieza; `null` si es fija. */
    val emocion: CharacterEmotion? = null,
    /** SVG incluido en la app que se usa cuando la pieza no tiene PNG. */
    val asset: String? = null,
    /** Campo heredado del documento `configuraciones/personaje`. */
    val legado: String? = null,
    /** PNG incluido en la app que se usa cuando no hay una personalización. */
    val assetPng: String? = null,
) {
    // --- Ilustración nueva, de atrás hacia delante ---
    CABELLO("cabello", "Cabello (atrás)", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/cabello_atras.png"),
    CABELLO_INFERIOR("cabello_inferior", "Cabello inferior", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/cabello_inferior.png"),
    BASE("cuerpo", "Cuerpo", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/cuerpo.png"),
    CUELLO("cuello", "Cuello", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/cuello.png"),
    BUFANDA_ATRAS("bufanda_atras", "Bufanda (atrás)", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/bufanda_atras.png"),
    OREJAS("orejas", "Orejas (personalizadas)", GrupoCapa.ESTRUCTURA),
    OREJA_IZQUIERDA("oreja_izquierda", "Oreja izquierda", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/oreja_izquierda.png"),
    OREJA_DERECHA("oreja_derecha", "Oreja derecha", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/oreja_derecha.png"),
    ROSTRO("rostro", "Rostro", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/rostro.png"),
    FLEQUILLO("flequillo", "Cabello (frente)", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/cabello_superior.png"),
    NARIZ("nariz", "Nariz", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/nariz.png"),
    BUFANDA("bufanda", "Bufanda (frente)", GrupoCapa.ESTRUCTURA, assetPng = "character/mentora/bufanda.png"),
    ACCESORIOS("accesorios", "Accesorios (gafas, aretes…)", GrupoCapa.ESTRUCTURA),

    // --- Cejas: cambian con la expresión ---
    CEJAS_NEUTRAL(
        "cejas_neutral", "Cejas · neutral", GrupoCapa.CEJAS,
        CharacterEmotion.NEUTRAL, "character/cejas_neutral.svg",
    ),
    CEJAS_THINKING(
        "cejas_thinking", "Cejas · pensando", GrupoCapa.CEJAS,
        CharacterEmotion.THINKING, "character/cejas_thinking.svg",
    ),
    CEJAS_HAPPY(
        "cejas_happy", "Cejas · feliz", GrupoCapa.CEJAS,
        CharacterEmotion.HAPPY, "character/cejas_happy.svg",
    ),
    CEJAS_SURPRISED(
        "cejas_surprised", "Cejas · sorprendido", GrupoCapa.CEJAS,
        CharacterEmotion.SURPRISED, "character/cejas_surprised.svg",
    ),

    // --- Ojos e iris: el PNG de ojos se conserva como base para cada gesto ---
    OJOS_NEUTRAL(
        "ojos_neutral", "Ojos · neutral", GrupoCapa.OJOS,
        CharacterEmotion.NEUTRAL, assetPng = "character/mentora/ojos.png",
    ),
    OJOS_THINKING(
        "ojos_thinking", "Ojos · pensando", GrupoCapa.OJOS,
        CharacterEmotion.THINKING, assetPng = "character/mentora/ojos.png",
    ),
    OJOS_HAPPY(
        "ojos_happy", "Ojos · feliz", GrupoCapa.OJOS,
        CharacterEmotion.HAPPY, assetPng = "character/mentora/ojos.png",
    ),
    OJOS_SURPRISED(
        "ojos_surprised", "Ojos · sorprendido", GrupoCapa.OJOS,
        CharacterEmotion.SURPRISED, assetPng = "character/mentora/ojos.png",
    ),
    IRIS("iris", "Iris", GrupoCapa.OJOS, assetPng = "character/mentora/iris.png"),

    // --- Boca: conserva las expresiones existentes ---
    BOCA_NEUTRAL(
        "boca_neutral", "Boca · neutral", GrupoCapa.BOCA,
        CharacterEmotion.NEUTRAL, "character/mouth_neutral.svg", "boca", "character/mentora/boca_feliz.png",
    ),
    BOCA_THINKING(
        "boca_thinking", "Boca · pensando", GrupoCapa.BOCA,
        CharacterEmotion.THINKING, "character/mouth_thinking.svg", "boca", "character/mentora/boca_triste.png",
    ),
    BOCA_HAPPY(
        "boca_happy", "Boca · feliz", GrupoCapa.BOCA,
        CharacterEmotion.HAPPY, "character/mouth_happy.svg", "boca", "character/mentora/boca_feliz.png",
    ),
    BOCA_SURPRISED(
        "boca_surprised", "Boca · sorprendido", GrupoCapa.BOCA,
        CharacterEmotion.SURPRISED, "character/mouth_surprised.svg", "boca",
    ),
    BOCA_SPEAKING("boca_speaking", "Boca · hablando", GrupoCapa.BOCA),

    // --- Parpadeo: se muestra un instante cada pocos segundos ---
    OJOS_CERRADO("ojos_cerrado", "Ojos cerrados (parpadeo)", GrupoCapa.PARPADEO, asset = "character/eyes_closed.svg"),
    ;

    /** Pieza de [grupo] correspondiente a la emoción indicada. */
    fun paraEmocion(emocion: CharacterEmotion): CapaPersonaje = porGrupoYEmocion.getValue(grupo to emocion)

    companion object {
        private val porGrupoYEmocion: Map<Pair<GrupoCapa, CharacterEmotion>, CapaPersonaje> =
            entries.filter { it.emocion != null }.associate { (it.grupo to it.emocion!!) to it }
    }
}

/** Mapa de capas personalizadas: clave = pieza, valor = PNG en Base64. */
data class CapasPersonaje(
    val png: Map<CapaPersonaje, String> = emptyMap(),
) {
    fun pngDe(capa: CapaPersonaje): String? = png[capa]

    fun tienePng(capa: CapaPersonaje): Boolean = png[capa] != null

    val algunaPersonalizada: Boolean get() = png.isNotEmpty()

    /** Cantidad de piezas con PNG propio, sobre el total del personaje. */
    val resumen: String get() = "${png.size} de ${CapaPersonaje.entries.size} piezas con PNG propio"
}
