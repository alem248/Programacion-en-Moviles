package com.quispe.coderole.domain.model

/**
 * Grupos de piezas del mentor. El panel de administración lista las piezas
 * grupo por grupo para que sea fácil localizar la que se quiere cambiar.
 */
enum class GrupoCapa(val titulo: String) {
    ESTRUCTURA("Estructura y cabello"),
    CEJAS("Cejas (expresión)"),
    OJOS("Ojos (expresión)"),
    BOCA("Boca (expresión)"),
    PARPADEO("Parpadeo"),
}

/**
 * Piezas modulares del personaje. Cada pieza puede llevar un PNG personalizado
 * guardado en `configuraciones/personaje/capas/{clave}`; si no lo tiene se usa
 * el SVG incluido en `assets/character/` y, si tampoco existe, la pieza no se
 * dibuja (así se agregan accesorios u orejas solo cuando el mentor los necesita).
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
) {
    // --- Estructura: se dibujan detrás y delante de la cabeza ---
    CABELLO("cabello", "Cabello (atrás)", GrupoCapa.ESTRUCTURA, asset = "character/cabello.svg"),
    BASE("base", "Cabeza y cuerpo", GrupoCapa.ESTRUCTURA, asset = "character/base.svg", legado = "base"),
    OREJAS("orejas", "Orejas", GrupoCapa.ESTRUCTURA),
    FLEQUILLO("flequillo", "Flequillo", GrupoCapa.ESTRUCTURA, asset = "character/flequillo.svg"),
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

    // --- Ojos: cambian con la expresión (antes eran un solo campo `ojos`) ---
    OJOS_NEUTRAL(
        "ojos_neutral", "Ojos · neutral", GrupoCapa.OJOS,
        CharacterEmotion.NEUTRAL, "character/eyes_neutral.svg", "ojos",
    ),
    OJOS_THINKING(
        "ojos_thinking", "Ojos · pensando", GrupoCapa.OJOS,
        CharacterEmotion.THINKING, "character/eyes_thinking.svg", "ojos",
    ),
    OJOS_HAPPY(
        "ojos_happy", "Ojos · feliz", GrupoCapa.OJOS,
        CharacterEmotion.HAPPY, "character/eyes_happy.svg", "ojos",
    ),
    OJOS_SURPRISED(
        "ojos_surprised", "Ojos · sorprendido", GrupoCapa.OJOS,
        CharacterEmotion.SURPRISED, "character/eyes_surprised.svg", "ojos",
    ),

    // --- Boca: cambia con la expresión (antes era un solo campo `boca`) ---
    BOCA_NEUTRAL(
        "boca_neutral", "Boca · neutral", GrupoCapa.BOCA,
        CharacterEmotion.NEUTRAL, "character/mouth_neutral.svg", "boca",
    ),
    BOCA_THINKING(
        "boca_thinking", "Boca · pensando", GrupoCapa.BOCA,
        CharacterEmotion.THINKING, "character/mouth_thinking.svg", "boca",
    ),
    BOCA_HAPPY(
        "boca_happy", "Boca · feliz", GrupoCapa.BOCA,
        CharacterEmotion.HAPPY, "character/mouth_happy.svg", "boca",
    ),
    BOCA_SURPRISED(
        "boca_surprised", "Boca · sorprendido", GrupoCapa.BOCA,
        CharacterEmotion.SURPRISED, "character/mouth_surprised.svg", "boca",
    ),

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
