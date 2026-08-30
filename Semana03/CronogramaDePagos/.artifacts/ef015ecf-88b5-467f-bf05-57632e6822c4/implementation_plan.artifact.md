# Plan de Configuración para Ejecución Directa (Consola)

El objetivo es configurar el proyecto para que la lógica de consola en `MainCronograma.kt` pueda ejecutarse directamente desde Android Studio con soporte para entrada de teclado.

## User Review Required

> [!IMPORTANT]
> El proyecto está configurado como un módulo Android, pero tu lógica es de consola (JVM). Para que funcione correctamente en Android Studio, se debe configurar una tarea de Gradle o una configuración de ejecución específica.
> Además, he notado una configuración inusual en `compileSdk` que podría causar problemas de indexación.

## Proposed Changes

### [Componente: Build Configuration]

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/USER/StudioProjects/Programacion-en-Moviles/Semana03/CronogramaDePagos/app/build.gradle.kts)
- Corregir `compileSdk` a un valor estándar (35).
- Añadir una tarea `JavaExec` para permitir la ejecución de la consola directamente vía Gradle con soporte para entrada estándar.

### [Componente: Código Fuente]

#### [MODIFY] [MainCronograma.kt](file:///C:/Users/USER/StudioProjects/Programacion-en-Moviles/Semana03/CronogramaDePagos/app/src/main/java/com/quispe/cronogramadepagos/cronograma/MainCronograma.kt)
- Pequeños ajustes si son necesarios para asegurar la compatibilidad con la ejecución JVM pura fuera de Android si fuera necesario (aunque `java.time` ya está cubierto).

## Verification Plan

### Manual Verification
1. Ejecutar la tarea `./gradlew :app:runCronograma` desde la terminal de Android Studio.
2. Verificar que se pueda ingresar datos (Nombre, Monto, etc.) y que se imprima la tabla correctamente.
3. Instruir al usuario sobre cómo activar "Emulate terminal in output console" para la configuración de ejecución de Kotlin en el IDE.
