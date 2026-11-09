# CodeRole

CodeRole es una aplicación Android en Kotlin para conversar con una mentora virtual de programación. Compose muestra un único personaje modular y Firebase mantiene la cuenta y su conversación individual. El chat genera las respuestas con la API de Google Gemini desde la app y persiste cada intercambio en Firestore.

## Arquitectura

El proyecto mantiene capas separadas dentro de `app/src/main/java/com/quispe/coderole/`:

- `data/remote`: Retrofit, DTO y servicio de perfil.
- `data/local`: Room, `PersonajeEntity` y DAO.
- `data/mapper`: conversiones DTO → Entity → modelo de UI.
- `data/auth`: Firebase Authentication y lectura del rol desde Firestore.
- `data/chat`: observación reactiva del historial del usuario y cliente de la API de Gemini (`GeminiChatService` y `GeminiConfig`).
- `data/media/Imagenes.kt`: reescalado y codificación a Base64 de las imágenes que viajan a Firestore (capas PNG y foto de perfil).
- `data/prefs/PreferenciasTema.kt`: preferencia persistente del modo oscuro (`null` = seguir al sistema).
- `data/repository`: implementación de `ItemRepository`, `ActividadRepository`, `CapasRepository` y `PerfilRepository` sobre Firestore.
- `domain/repository`: contratos de conversación, actividad diaria, capas del personaje y perfil, desacoplados de Firebase.
- `domain/model`: `Personaje`, `Sesion`, `Rol`, `CharacterEmotion`, `ActividadDiaria`, `CapasPersonaje` y `PerfilUsuario`.
- `di/AppContainer.kt`: creación de Room, Retrofit, Firebase, preferencias de tema y notificador.
- `ui/`: pantallas Compose (`admin`, `perfil`, `detail`, `login`, `favorites`), `UiState` y ViewModels.
- `ui/theme`: paleta de CodeRole en variantes clara y oscura (`EsquemaClaro` / `EsquemaOscuro`).

La lista y el detalle observan Room mediante `Flow`; Retrofit solo actualiza la caché. La aplicación muestra una sola mentora: la sincronización guarda el primer perfil del servicio de ejemplo bajo el identificador local `1`, con el nombre y el diseño propios de CodeRole. La barra inferior tiene destinos para Mentora, Respuestas, Perfil y, solo para administradores, Admin. Las respuestas favoritas se guardan por usuario en Firestore.

## Firebase

El archivo `app/google-services.json` está ignorado por Git y debe conservarse localmente. El paquete registrado debe ser `com.quispe.coderole`. En Firebase Console:

1. Habilita Email/Password en Authentication.
2. Para crear un administrador, agrega primero su cuenta en **Authentication → Users**. Copia el UID que Firebase le asigne y crea en Firestore `usuarios/{uid}` con los campos `rol: "admin"` y `correo` con el correo de esa cuenta. Esta configuración se hace en Firebase Console; la app no permite que un usuario normal se otorgue el rol admin.
3. Publica las reglas del archivo `firestore.rules`. El registro desde la app crea `usuarios/{uid}` con correo y rol fijo `usuario`; cada cuenta solo puede leer sus propios mensajes, crear mensajes con la forma `texto`/`rol`/`favorita`/`creadoEn`, actualizar únicamente el indicador de favorito de respuestas de la mentora y modificar su propio `nombre` y `fotoBase64` (nunca `rol` ni `correo`). `actividad/{fecha}` y sus `registros` solo se leen como administrador y los contadores los incrementa cualquier cuenta autenticada. `configuraciones/personaje` y su subcolección `capas/{clave}` las lee cualquier cuenta autenticada para dibujar al mentor, pero solo un administrador asocia o restablece un PNG (campo `base64` de hasta 900 000 caracteres o `null`).

   ```powershell
   firebase deploy --only firestore:rules
   ```

5. (Opcional) Si prefieres generar la respuesta en el servidor en lugar de desde la app, instala Node.js 22 y Firebase CLI, ejecuta `npm install` dentro de `functions/`, autentícate con `firebase login` y publica la Cloud Function con la clave de Gemini como secreto:

   ```powershell
   firebase functions:secrets:set GEMINI_API_KEY
   firebase deploy --only functions
   ```

   La función `responderMentor` valida Firebase Auth, deriva el UID de la sesión verificada, limita los mensajes a 5 000 caracteres y conserva los últimos 20 mensajes como contexto; llama a la misma API de Gemini que la app. El despliegue de Cloud Functions exige que el proyecto Firebase tenga habilitados los servicios y la facturación que Firebase requiere.

Cada conversación se consulta desde Firestore con un listener reactivo; Firestore conserva su caché local para mostrar el historial ya sincronizado sin conexión. Enviar mensajes y obtener respuestas de la IA requiere conexión. Si Gemini falla, el mensaje del usuario sigue guardado y la app muestra el error para reintentar.

El rol nunca se infiere del correo ni se elige en la pantalla. Si falla la creación del perfil en Firestore, la app elimina la cuenta recién creada cuando Firebase lo permite y vuelve al formulario con el error.

## Chat con Gemini

`data/chat/GeminiConfig.kt` concentra la lista de modelos de reserva (`MODELOS`), el límite de 5 000 caracteres, los 20 mensajes de contexto y la instrucción de sistema. La clave local se lee de `GEMINI_API_KEY` en `local.properties`, archivo ignorado por Git.

`GeminiChatService.kt` hace la llamada REST a `generativelanguage.googleapis.com/v1beta/models/{modelo}:generateContent` con OkHttp y Gson, sin dependencias adicionales. Si un modelo responde 404, 429 o 503, se prueba el siguiente de la lista; los errores 401/403 y 400 se traducen a mensajes claros en la pantalla del chat.

Al enviar un mensaje, `FirebaseChatRepository` guarda el texto del usuario en Firestore, envía el historial normalizado (la conversación empieza en el usuario y los turnos consecutivos se fusionan, como exige Gemini) y guarda la respuesta con rol `mentora`.

### Latencia de respuesta

La consulta a Gemini se ejecuta en `Dispatchers.IO` con OkHttp y el historial vive en memoria, alimentado por el listener de Firestore, así que enviar el mensaje del usuario y llamar a la API ocurren en paralelo y la pantalla nunca vuelve a leer la colección completa para reconstruir la conversación. La respuesta se consume en streaming (`enqueue` + `suspendCancellableCoroutine`, `flowOn(Dispatchers.IO)`) y el texto parcial llega a la UI por `StateFlow`, por lo que la mentora empieza a escribir antes de que termine la generación. Los tiempos de espera son de 10 s de conexión, 45 s de lectura y 10 s de escritura, y los códigos 404, 429, 500, 502, 503 y 504 se reintenan con el siguiente modelo de la lista `MODELOS`.

> La llamada actual se hace desde Android, por lo que la clave se incorpora al APK y puede extraerse. `local.properties` evita dejarla en el código fuente, pero no protege la clave del APK. Rota la clave que estuvo escrita en el código y, para distribuir la app, usa la Cloud Function con `GEMINI_API_KEY` como secreto. Administra las claves desde [Google AI Studio](https://aistudio.google.com/apikey).



Los SVG originales están en `app/src/main/assets/character/`. `base.svg` conserva pose, rostro y ropa, mientras que `cabello.svg` y `flequillo.svg` separan el pelo para poder cambiarlo pieza por pieza; `eyes_*.svg`, `mouth_*.svg`, `cejas_*.svg` y `eyes_closed.svg` comparten el mismo `viewBox` para superponerse desde `ModularCharacterView`. AndroidSVG rasteriza esas capas en ejecución.

`ModularCharacterView` apila las 18 piezas en este orden: cabello (atrás), cabeza y cuerpo, orejas, flequillo, cejas, ojos, boca y accesorios. Cada pieza usa el PNG que el administrador asocie en `configuraciones/personaje/capas/{clave}` o, si no tiene, el SVG incluido; las piezas sin diseño (orejas y accesorios) no se dibujan hasta que reciben un PNG. La pila queda viva con una respiración suave, un parpadeo cada ~4 s (intercambiando los ojos por `eyes_closed.svg`) y un fundido de 240 ms al cambiar la expresión. `DetalleViewModel` devuelve al mentor a `NEUTRAL` 3,5 s después de cada reacción para que no quede con la última cara pegada.

## Panel de administración

El menú **Admin** (solo rol `admin`) tiene dos paneles:

- **Actividad diaria**: `FirestoreActividadRepository` incrementa los contadores de `actividad/{aaaa-MM-dd}` (`mensajes`, `respuestas`, `favoritos`, `sesiones`) con `FieldValue.increment` y guarda cada interacción en la subcolección `registros`. Los datos se escriben en un `CoroutineScope` propio (`SupervisorJob` + `Dispatchers.IO`) para no frenar el chat. `ActividadPanel` permite elegir el día, ver los totales y el listado de registros; solo una cuenta `admin` puede leerlos.
- **Piezas del personaje**: `FirestoreCapasRepository` escucha en tiempo real el documento `configuraciones/personaje` (los campos heredados `base`, `ojos` y `boca`, que siguen leyéndose como respaldo) y su subcolección `capas`, con un documento por pieza (`{clave}` + `base64`), y combina ambas fuentes. `CapasPanel` agrupa las 18 piezas en estructura, cejas, ojos, boca y parpadeo, deja previsualizar el mentor con cualquiera de las cuatro expresiones y asocia o quita un PNG por pieza. `Imagenes.pngParaCapa` valida la firma PNG, reescala a 440 px y vuelve a codificar para no superar el límite de un documento.

## Perfil de usuario y modo oscuro

`ui/perfil/PerfilScreen` permite editar el nombre y la foto de perfil y cerrar sesión. La foto se reescala a 360 px, se guarda como JPEG en Base64 dentro de `usuarios/{uid}` (sin depender de Firebase Storage) y las reglas limitan el documento a 700 000 caracteres. `FirestorePerfilRepository` expone un `Flow<PerfilUsuario>` basado en el snapshot listener: el nombre y la foto se reflejan al instante en el perfil, en la cabecera del chat y en cualquier otro dispositivo de la misma cuenta.

La paleta de la app es Rojo Principal `#e04a4e`, Rosa Secundario `#f4777f`, Crema `#feedb7` y Rosa Pastel `#fbd2cc`, con variantes `EsquemaClaro` y `EsquemaOscuro` en `ui/theme`. El conmutador **Modo oscuro** del perfil guarda la preferencia en `PreferenciasTema` (SharedPreferences); si el usuario aún no eligió nada, la app sigue el tema del sistema. `values-night/themes.xml` evita el destello blanco al arrancar.

## Ejecución

Abre el proyecto en Android Studio y ejecútalo en un dispositivo con Google Play Services. Agrega `GEMINI_API_KEY=...` a `local.properties` para las llamadas locales a Gemini. Room conserva el perfil de la mentora offline; Firestore conserva el chat y las respuestas favoritas por cuenta. Enviar mensajes requiere conexión. El permiso de notificaciones se solicita al sincronizar desde el panel Admin y la sincronización continúa aunque se deniegue.

La cuenta de administrador abre el panel Admin desde el menú inferior. Allí puede consultar la actividad de cada día, cambiar las 18 piezas del personaje (cabello, flequillo, orejas, cejas, ojos, boca y accesorios) previsualizando cada expresión, sincronizar y limpiar la caché. La foto, el nombre y el modo oscuro se configuran en **Perfil**.
