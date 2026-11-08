package com.quispe.coderole.domain.model

/**
 * Capas modulares del personaje (estilo anime / nekogirl).
 * Cada capa puede llevar un PNG personalizado enviado desde la administración;
 * si no lo tiene, se usa la capa por defecto / SVG incluida en la app.
 */
enum class CapaPersonaje(
    val titulo: String,
    val campo: String,
    val categoria: String,
) {
    // 1. Capa Base / Cuerpo: postura estática del personaje, ropa base (suéter de cuello alto, chaqueta rosada, falda)
    BASE("Capa Base / Cuerpo", "base", "Cuerpo y Ropa"),

    // 2. Capa de Cabello y Orejas: estructura fija superior (cabello castaño ondulado y orejas de animal)
    CABELLO("Cabello y Orejas", "cabello", "Cabello y Orejas"),

    // 3. Capa de Ojos (Estados de expresión)
    OJOS_NEUTRAL("Ojos (Neutral)", "ojos_neutral", "Ojos"),
    OJOS_THINKING("Ojos (Pensativo)", "ojos_thinking", "Ojos"),
    OJOS_HAPPY("Ojos (Feliz)", "ojos_happy", "Ojos"),
    OJOS_SURPRISED("Ojos (Sorprendido)", "ojos_surprised", "Ojos"),

    // 4. Capa de Boca / Gestos (Estados de habla y emoción)
    BOCA_NEUTRAL("Boca (Neutral)", "boca_neutral", "Boca"),
    BOCA_SPEAKING("Boca (Hablando)", "boca_speaking", "Boca"),
    BOCA_HAPPY("Boca (Feliz)", "boca_happy", "Boca"),
    BOCA_SURPRISED("Boca (Sorprendida)", "boca_surprised", "Boca"),

    // 5. Capa de Accesorios (Opcional)
    ACCESORIOS("Accesorios", "accesorios", "Accesorios");

    companion object {
        fun desdeCampo(campo: String): CapaPersonaje? = entries.find { it.campo == campo }
    }
}

/** Mapa de capas personalizadas: clave = capa, valor = PNG en Base64. */
data class CapasPersonaje(
    val png: Map<CapaPersonaje, String> = emptyMap(),
) {
    fun pngDe(capa: CapaPersonaje): String? = png[capa]

    fun tienePng(capa: CapaPersonaje): Boolean = png[capa] != null

    val algunaPersonalizada: Boolean get() = png.isNotEmpty()
}
