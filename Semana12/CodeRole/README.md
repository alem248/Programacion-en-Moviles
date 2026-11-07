# CodeRole

CodeRole es una aplicación Android en Kotlin para conversar con una mentora virtual de programación. La interfaz usa Compose y la guía visual rosada del proyecto; en esta primera fase las respuestas del chat son locales y demostrativas. La integración con IA queda para una fase posterior.

## Arquitectura

El proyecto mantiene capas separadas dentro de `app/src/main/java/com/quispe/coderole/`:

- `data/remote`: Retrofit, DTO y servicio de perfil.
- `data/local`: Room, `PersonajeEntity` y DAO.
- `data/mapper`: conversiones DTO → Entity → modelo de UI.
- `data/auth`: Firebase Authentication y lectura del rol desde Firestore.
- `data/repository`: implementación del contrato `ItemRepository`.
- `domain/model`: `Personaje`, `Sesion`, `Rol` y `CharacterEmotion`.
- `di/AppContainer.kt`: creación de Room, Retrofit, Firebase y notificador.
- `ui/`: pantallas Compose, `UiState` y ViewModels.

La lista y el detalle observan Room mediante `Flow`; Retrofit solo actualiza la caché. La aplicación muestra un solo personaje: la sincronización guarda el primer perfil del servicio de ejemplo bajo el identificador local `1`, con el nombre y el diseño propios de CodeRole.

## Firebase

El archivo `app/google-services.json` está ignorado por Git y debe conservarse localmente. El paquete registrado debe ser `com.quispe.coderole`. En Firebase Console:

1. Habilita Email/Password en Authentication.
2. Publica las reglas del archivo `firestore.rules`. El registro desde la app crea `usuarios/{uid}` con el rol fijo `usuario`; las reglas no permiten que la app cambie o eleve ese rol.
3. Crea la cuenta administradora desde Firebase Console y asigna manualmente `rol: "admin"` en `usuarios/{uid}`.

El rol nunca se infiere del correo ni se elige en la pantalla. Si falla la creación del perfil en Firestore, la app elimina la cuenta recién creada cuando Firebase lo permite y vuelve al formulario con el error.

## Personaje modular

Los SVG originales están en `app/src/main/assets/character/`. `base.svg` contiene pose, cabello, orejas, rostro y ropa; `eyes_*.svg` y `mouth_*.svg` tienen el mismo `viewBox` para superponerse desde `ModularCharacterView`. AndroidSVG rasteriza esas capas en ejecución. Cuando estén listas las ilustraciones PNG, se puede cambiar el cargador visual sin alterar `CharacterEmotion`, `DetalleViewModel` ni el layout del chat.

## Ejecución

Abre el proyecto en Android Studio y ejecútalo en un dispositivo con Google Play Services. Se requiere conexión para el primer perfil remoto y Firestore; después, Room conserva el mentor y su favorito para el uso offline. El permiso de notificaciones se solicita al sincronizar desde el panel Admin y la sincronización continúa aunque se deniegue.

La cuenta de administrador abre el panel Admin desde la pestaña Perfil o la barra inferior. Allí puede consultar los totales, sincronizar y limpiar la caché.
