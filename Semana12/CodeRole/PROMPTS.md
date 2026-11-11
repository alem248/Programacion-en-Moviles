# Registro de consultas de IA

Consultas de desarrollo que dieron lugar a cambios en esta app durante la sesión del 11 de noviembre de 2026. Los cambios se revisaron y adaptaron a la arquitectura Kotlin/Compose existente.

| Herramienta | Consulta | Resultado aplicado |
|---|---|---|
| Codex | Sustituir al mentor por las capas PNG compartidas, usar `wallpaper.png` en inicio/registro y ordenar la navegación y el panel administrativo en `mejora-ia`. | Se integraron las imágenes del personaje y el fondo, y se ajustó la navegación y el panel Admin. |
| Codex | Corregir el orden de las capas del personaje según las referencias feliz y triste. | Se revisó la composición y el apilado del personaje. |
| Codex | Incorporar boca feliz/triste y nariz, y usar el parpadeo con ojos cerrados. | Se añadieron las piezas faciales y el estado de parpadeo al personaje. |
| Codex | Corregir la API de Gemini cuando dejó de funcionar. | Se revisaron la configuración del servicio y los errores de respuesta. |
| Codex | Explicar cada parte de la app y comparar su estructura/requisitos con el PDF entregado, excluyendo el tema y la API. | Se inspeccionaron los módulos, las pantallas, los repositorios y la persistencia frente a los requisitos. |
| Codex | Corregir las partes parciales o incumplidas de la estructura pedida. | Se completó el flujo visual de catálogo/detalle/favoritos, la ruta de datos offline, las fábricas con `AppContainer`, el procesamiento de imágenes fuera de ViewModels, las notificaciones con conteo y pruebas unitarias con repositorio falso. |

