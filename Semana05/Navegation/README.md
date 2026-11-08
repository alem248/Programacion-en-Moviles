# Portal Académico - Aplicación de Navegación Android (Jetpack Compose & Material Design 3)

Este proyecto ha sido rediseñado como un **Portal Académico** moderno implementado con **Android Jetpack Compose**, **Material Design 3** y **Navigation Compose**. Posee una interfaz moderna con una paleta de colores basada en tonos morados (`PurplePrimary`, `PurpleDark`, `PurpleLight`) y blancos.

---

## 📋 Prompt Utilizado para la Mejora con IA

> Actúa como un desarrollador experto en Android Jetpack Compose y Material Design 3. Necesito rediseñar las pantallas de mi aplicación de navegación actual para convertirla en un 'Portal Académico' moderno, utilizando una paleta de colores basada en tonos morados y blancos. Genera el código en Kotlin para las siguientes pantallas, asegurándote de recibir el navController como parámetro en cada una:
>
> **Pantalla de Login:** Un contenedor centrado verticalmente con el título 'Portal Académico' y el subtítulo 'Accede a tu cuenta'. Incluye dos OutlinedTextField (Correo institucional y Contraseña, este último con un ícono de visibilidad al final) y un Button ancho de color morado con el texto 'INICIAR SESIÓN'.
>
> **HomeScreen:** Un fondo con degradado morado suave. En la parte superior, el texto en blanco 'Bienvenido, Alexandra' y '¿Qué deseas gestionar hoy?'. Debajo, dos tarjetas (Card) grandes tipo botón: 'Directorio de Alumnos' y 'Mi Perfil Académico', ambas con un ícono a la izquierda. Al fondo de la pantalla, un TextButton rojo con el ícono de salir y el texto 'Cerrar Sesión Segura'.
>
> **ListScreen (Directorio de Alumnos):** Utiliza un Scaffold con un TopAppBar que tenga una flecha de retroceso. El contenido debe ser una LazyColumn donde cada elemento muestre un avatar circular (foto de perfil temporal), el nombre del alumno en negrita, su carrera debajo y un ícono de flecha (chevron) a la derecha. Al hacer clic, debe navegar a la ruta de detalle.
>
> **DetailScreen (Expediente Académico):** Un diseño con TopAppBar de retroceso. Muestra la foto grande del alumno seleccionado, su nombre, carrera y un bloque de 'Biografía' estructurado dentro de una tarjeta.
>
> **ProfileScreen (Configuración de Perfil):** Un TopAppBar de retroceso. La cabecera debe tener un fondo de color entero morado con el avatar circular del usuario centrado y su nombre. Debajo, divide la información en dos secciones usando ListItem: 'INFORMACIÓN PERSONAL' (con íconos para nombre, correo de Tecsup y teléfono) y 'ACADÉMICO' (con íconos para mostrar la carrera de Ingeniería/Desarrollo de Software y el ciclo actual). Al final, un botón de 'Cerrar Sesión'.
>
> **NOTA:** considera hacer esas modificaciones en la rama `mejora-ia`

---

## 📱 Pantallas Desarrolladas

1. **`LoginScreen` (Pantalla de Login)**
   - Contenedor centrado verticalmente con ícono y título "Portal Académico".
   - Subtítulo "Accede a tu cuenta".
   - `OutlinedTextField` de *Correo institucional* con ícono e *inputType email*.
   - `OutlinedTextField` de *Contraseña* con ícono de visibilidad conmutable (`IconButton`).
   - Botón de color morado ancho con el texto `"INICIAR SESIÓN"`.

2. **`HomeScreen` (Pantalla Principal)**
   - Fondo con degradado morado suave (`Brush.verticalGradient`).
   - Saludo en blanco: *"Bienvenido, Alexandra"* y *"¿Qué deseas gestionar hoy?"*.
   - Dos tarjetas (`Card`) grandes tipo botón:
     - *"Directorio de Alumnos"* con ícono de escuela/alumnos.
     - *"Mi Perfil Académico"* con ícono de perfil.
   - Botón inferior `TextButton` rojo *"Cerrar Sesión Segura"* con ícono de salir.

3. **`ListScreen` (Directorio de Alumnos)**
   - `Scaffold` con `TopAppBar` morado y botón de retroceso.
   - `LazyColumn` que renderiza la lista de estudiantes.
   - Avatar circular con iniciales como foto de perfil temporal.
   - Nombre en negrita, carrera profesional y chevron a la derecha para navegación a detalle.

4. **`DetailScreen` (Expediente Académico)**
   - `Scaffold` con `TopAppBar` de retroceso.
   - Avatar circular grande con iniciales/foto.
   - Nombre completo, carrera e insignia de ciclo actual.
   - Tarjeta (`Card`) con bloque de *"Biografía Académica"*.
   - Tarjeta con información de contacto (correo y teléfono).

5. **`ProfileScreen` (Configuración de Perfil)**
   - `TopAppBar` de retroceso.
   - Cabecera morada con avatar circular y nombre centrado (*"Alexandra Ximena Quispe Mallqui"*).
   - Sección **INFORMACIÓN PERSONAL** utilizando `ListItem` (Nombre, Correo Tecsup, Teléfono).
   - Sección **ACADÉMICO** utilizando `ListItem` (Carrera Profesional, Ciclo Actual).
   - Botón rojo de *"Cerrar Sesión"* al final.

---

## 🎨 Paleta de Colores

- **Purple Primary:** `#673AB7`
- **Purple Dark:** `#4A148C`
- **Purple Light Container:** `#EDE7F6`
- **Logout Red:** `#E53935`
- **Fondo / Superficie:** Blanco (`#FFFFFF`) y Gris Claro (`#F8F9FA`)
