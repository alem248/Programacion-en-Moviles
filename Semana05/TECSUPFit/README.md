# TECSUP Fit - Refactorización y Mejoras UI/UX

TECSUP Fit es una aplicación móvil de gestión y reserva de clases deportivas diseñada para estudiantes e instructores. Este proyecto ha sido refactorizado bajo principios de Clean Code y Material Design 3, optimizando la modularidad de la interfaz de usuario, incorporando soporte completo para modo oscuro y agregando transiciones y animaciones fluidas sin alterar la arquitectura base centrada en el manejo de estado nativo de Jetpack Compose.

## Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación principal para el desarrollo Android.
- **Jetpack Compose**: Kit de herramientas moderno para la construcción de interfaces de usuario declarativas.
- **Material Design 3 (M3)**: Sistema de diseño aplicado para esquemas de color dinámicos, tipografía estructurada y componentes estandarizados.
- **Navigation Compose**: Gestión de rutas y navegación entre pantallas con animaciones de transición personalizadas.
- **State Management**: Manejo de estado ligero mediante `remember`, `mutableStateOf` y `SnapshotStateList` en la capa de UI.

## Comparativa de Interfaz: Antes vs. Después

A continuación se presenta la tabla comparativa de las pantallas principales antes y después del proceso de refactorización visual y arquitectónica.

| Pantalla | Versión Original (Antes) | Versión Refactorizada (Después) |
| :--- | :--- | :--- |
| **Inicio / Clases** | ![Antes - Inicio](docs/images/antes_inicio.png) | ![Después - Inicio](docs/images/despues_inicio.png) |
| **Detalle de Clase** | ![Antes - Detalle](docs/images/antes_detalle.png) | ![Después - Detalle](docs/images/despues_detalle.png) |
| **Confirmación de Reserva** | ![Antes - Confirmación](docs/images/antes_confirmacion.png) | ![Después - Confirmación](docs/images/despues_confirmacion.png) |
| **Mis Reservas** | ![Antes - Reservas](docs/images/antes_reservas.png) | ![Después - Reservas](docs/images/despues_reservas.png) |
| **Rutinas** | ![Antes - Rutinas](docs/images/antes_rutinas.png) | ![Después - Rutinas](docs/images/despues_rutinas.png) |
| **Perfil de Usuario** | ![Antes - Perfil](docs/images/antes_perfil.png) | ![Después - Perfil](docs/images/despues_perfil.png) |

## Prompt Utilizado para la Refactorización

```text
Actúa como un desarrollador senior de Android especializado en Jetpack Compose, UI/UX y Clean Code. Tu tarea es analizar y refactorizar el código actual del proyecto "TECSUP Fit". El objetivo es elevar la calidad visual y estructural de la aplicación sin alterar su arquitectura base.

1. Requerimientos de Arquitectura y Estado (Reglas Estrictas):

Estado: Mantén el manejo del estado puramente en la interfaz de usuario utilizando remember y mutableStateOf. NO implementes ViewModels, StateFlow, ni arquitecturas MVVM.

Datos: Sigue utilizando las listas de datos mock en memoria temporal (data classes). NO integres bases de datos locales como Room ni llamadas a red.

Refactorización (Clean Code): Analiza las pantallas actuales (InicioScreen, DetalleClaseScreen, MisReservasScreen, etc.) y extrae los elementos visuales repetitivos o complejos en funciones @Composable independientes y reutilizables (ej. ClaseCard, ReservaItem, EstadisticaCard). Organiza estos componentes en un paquete ui/components.

2. Requerimientos de UI/UX y Material 3:

Animaciones: Implementa transiciones suaves entre pantallas dentro del NavHost (ej. slideInHorizontally, fadeOut). Agrega pequeñas animaciones en la interfaz, como un animateContentSize en las tarjetas o un ligero efecto visual al pulsar los botones de reserva.

Modo Oscuro: Asegúrate de que todos los colores utilizados (especialmente el verde principal y los fondos de los estados de reserva) soporten el modo oscuro dinámicamente utilizando MaterialTheme.colorScheme en lugar de colores estáticos hardcodeados.

Pulido Visual: Mejora la jerarquía tipográfica y los espaciados utilizando estrictamente los lineamientos de Material Design 3.

3. Requerimientos del README:
Genera el código Markdown para un archivo README.md en la raíz del proyecto. Este archivo debe tener una estructura profesional y directa, e incluir:

Título del proyecto y breve descripción.

Tecnologías utilizadas (Jetpack Compose, Material 3, Navigation).

Una sección específica estructurada en tablas para comparar el "Antes" y el "Después", dejando los espacios listos (![Antes Inicio](ruta_imagen)) para que yo inserte las capturas de pantalla de la versión original y de esta nueva versión refactorizada.

4. Requerimientos de Formato y Comentarios:

Añade comentarios en el código solo cuando expliquen animaciones complejas o lógica de estado intrincada.

Los comentarios deben ser breves, directos y técnicos, escritos en español.

REGLA ESTRICTA: No utilices emojis ni emoticones en ninguna parte de tu respuesta. Esto incluye el texto explicativo, los comentarios del código, el archivo README y los mensajes de commit. Cero emojis.

5. Requerimientos de Git:
Todo este trabajo debe realizarse en una nueva rama. Al final de tu respuesta, proporciona un bloque de código Bash con los comandos exactos para:

Crear y cambiar a una nueva rama llamada mejora-ia.

Agregar todos los cambios al stage (incluyendo el nuevo README).

Crear los commits necesarios y bien estructurados que expliquen la refactorización, las mejoras de UI y el README.

Hacer el push de esta nueva rama al repositorio remoto.

NOTA: Me gustarian que en el readme se pueda colocar imagenes del antes y despues de la mejhora realizada y ademas se coloque en una zona el promt brindado
```

## Estructura del Proyecto

```text
com.quispe.tecsupfit/
├── modelos/
│   └── Modelos.kt
├── navegacion/
│   └── AppNavigation.kt
├── ui/
│   ├── components/
│   │   ├── ClaseCard.kt
│   │   ├── EstadisticaCard.kt
│   │   ├── FilaInfoClase.kt
│   │   ├── HeaderSeccion.kt
│   │   ├── ReservaItem.kt
│   │   └── RutinaCard.kt
│   ├── screens/
│   │   ├── ConfirmacionScreen.kt
│   │   ├── DetalleClaseScreen.kt
│   │   ├── InicioScreen.kt
│   │   ├── MainScreen.kt
│   │   ├── MisReservasScreen.kt
│   │   ├── PerfilScreen.kt
│   │   └── RutinasScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```
