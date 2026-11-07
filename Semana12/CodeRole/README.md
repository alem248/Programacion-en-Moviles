# CodeRole

CodeRole es una aplicación Android en Kotlin para conversar con una mentora virtual de programación. Compose muestra un único personaje modular y Firebase mantiene la cuenta y su conversación individual. Una Cloud Function autenticada llama a OpenAI Responses API y persiste cada intercambio en Firestore.

## Arquitectura

El proyecto mantiene capas separadas dentro de `app/src/main/java/com/quispe/coderole/`:

- `data/remote`: Retrofit, DTO y servicio de perfil.
- `data/local`: Room, `PersonajeEntity` y DAO.
- `data/mapper`: conversiones DTO → Entity → modelo de UI.
- `data/auth`: Firebase Authentication y lectura del rol desde Firestore.
- `data/chat`: observación reactiva del historial del usuario y llamada a Firebase Callable Function.
- `data/repository`: implementación del contrato `ItemRepository`.
- `domain/repository/ChatRepository.kt`: contrato de conversación, desacoplado de Firebase.
- `domain/model`: `Personaje`, `Sesion`, `Rol` y `CharacterEmotion`.
- `di/AppContainer.kt`: creación de Room, Retrofit, Firebase y notificador.
- `ui/`: pantallas Compose, `UiState` y ViewModels.

La lista y el detalle observan Room mediante `Flow`; Retrofit solo actualiza la caché. La aplicación muestra una sola mentora: la sincronización guarda el primer perfil del servicio de ejemplo bajo el identificador local `1`, con el nombre y el diseño propios de CodeRole. La barra inferior tiene destinos únicos para Mentora, Favoritos, Perfil y, solo para administradores, Admin.

## Firebase

El archivo `app/google-services.json` está ignorado por Git y debe conservarse localmente. El paquete registrado debe ser `com.quispe.coderole`. En Firebase Console:

1. Habilita Email/Password en Authentication.
2. Publica las reglas del archivo `firestore.rules`. El registro desde la app crea `usuarios/{uid}` con correo y rol fijo `usuario`; cada cuenta solo puede leer sus propios mensajes y no puede escribir mensajes directamente. Las reglas no permiten que la app cambie o eleve el rol.
3. Crea la cuenta administradora desde Firebase Console y asigna manualmente `rol: "admin"` en `usuarios/{uid}`.
4. Instala Node.js 22 y Firebase CLI, ejecuta `npm install` dentro de `functions/` y autentícate con `firebase login`. La configuración `.firebaserc` selecciona el proyecto `coderole-8ba99`.
5. Configura la clave de OpenAI como secreto de Functions (no la agregues a Kotlin, `google-services.json` ni al repositorio) y despliega el backend y las reglas:

   ```powershell
   firebase functions:secrets:set OPENAI_API_KEY
   firebase deploy --only functions,firestore:rules
   ```

   El despliegue de Cloud Functions requiere que el proyecto Firebase tenga habilitados los servicios y facturación que Firebase exige para Functions. La función `responderMentor` valida Firebase Auth, deriva el UID de la sesión verificada, limita los mensajes a 5 000 caracteres y conserva los últimos 20 mensajes como contexto para OpenAI. Usa Responses API con `gpt-6-astra`; el historial se guarda en `usuarios/{uid}/conversaciones/mentor/mensajes`.

Cada conversación se consulta desde Firestore con un listener reactivo; Firestore conserva su caché local para mostrar el historial ya sincronizado sin conexión. Enviar mensajes y obtener respuestas de la IA requiere conexión. Si OpenAI falla, el mensaje del usuario sigue guardado y la app muestra un error para reintentar.

El rol nunca se infiere del correo ni se elige en la pantalla. Si falla la creación del perfil en Firestore, la app elimina la cuenta recién creada cuando Firebase lo permite y vuelve al formulario con el error.

## Personaje modular

Los SVG originales están en `app/src/main/assets/character/`. `base.svg` contiene pose, cabello, orejas, rostro y ropa; `eyes_*.svg` y `mouth_*.svg` tienen el mismo `viewBox` para superponerse desde `ModularCharacterView`. AndroidSVG rasteriza esas capas en ejecución. Cuando estén listas las ilustraciones PNG, se puede cambiar el cargador visual sin alterar `CharacterEmotion`, `DetalleViewModel` ni el layout del chat.

## Ejecución

Abre el proyecto en Android Studio y ejecútalo en un dispositivo con Google Play Services. Se requiere conexión para el primer perfil remoto y Firestore; después, Room conserva el mentor y su favorito para el uso offline. El permiso de notificaciones se solicita al sincronizar desde el panel Admin y la sincronización continúa aunque se deniegue.

La cuenta de administrador abre el panel Admin desde la barra inferior. Allí puede consultar los totales, sincronizar y limpiar la caché.
