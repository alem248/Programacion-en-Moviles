# Documentación de Modificaciones con IA

Este documento detalla los prompts utilizados para mejorar la aplicación "Registro de Producto" y los resultados obtenidos mediante Gemini.

| Qué usé (Prompt) | Qué generó Gemini | Qué acepté o corregí (y por qué) |
| :--- | :--- | :--- |
| "Considerando el proyecto realizado, podrias agregar una validación de campos vacíos (si falta un dato al presionar AGREGAR, mostrar un mensaje de error en rojo en lugar de la Card) y un botón Limpiar que vacíe el formulario." | Generó lógica usando `mutableStateOf` para errores, validaciones con `.isBlank()` y un botón adicional para resetear los campos. | Acepté la implementación completa. La validación era clara y el botón "LIMPIAR" cumplía con lo solicitado para mejorar la experiencia de usuario. |
| "considerando lo realizado, deseo que le des un arreglo de diseño, mas que todo en el encabezado para que quede como en la imagen" | Añadió un `TopAppBar` azul (Indigo), mejoró la jerarquía visual con negritas, incluyó un mensaje de estado vacío ("Aún no has registrado ningún producto") y un footer con el nombre del desarrollador. | Acepté el diseño. Se ajustó fielmente a la imagen de referencia, mejorando significativamente el aspecto profesional de la aplicación y añadiendo contexto para el usuario. |
