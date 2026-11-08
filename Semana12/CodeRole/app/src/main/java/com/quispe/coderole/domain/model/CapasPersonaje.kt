package com.quispe.coderole.domain.model

/**
 * Capas modulares del personaje. Cada capa puede llevar un PNG personalizado;
 * si no lo tiene se usa el SVG incluido en `assets/character/`.
 */
enum class CapaPersonaje(val titulo: String, val campo: String) {
    BASE("Capa base", "base"),
    OJOS("Ojos", "ojos"),
    BOCA("Boca", "boca"),
}

/** Mapa de capas personalizadas: clave = capa, valor = PNG en Base64. */
data class CapasPersonaje(
    val png: Map<CapaPersonaje, String> = emptyMap(),
) {
    fun pngDe(capa: CapaPersonaje): String? = png[capa]

    fun tienePng(capa: CapaPersonaje): Boolean = png[capa] != null

    val algunaPersonalizada: Boolean get() = png.isNotEmpty()
}
