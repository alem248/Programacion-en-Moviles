# AvisosCampus

Aplicación Android desarrollada con Kotlin, Jetpack Compose y Material 3. Muestra notificaciones locales, programa recordatorios de diez segundos, escucha cambios del modo avión y registra los avisos en un historial en memoria.

## Estructura del proyecto

```text
app/src/main/java/com/quispe/avisoscampus/
├── AvisosApp.kt
├── MainActivity.kt
├── data/
│   └── AvisosRepository.kt
├── model/
│   └── Modelos.kt
├── notificacion/
│   └── Notificador.kt
├── receiver/
│   └── ModoAvionReceiver.kt
├── ui/
│   ├── HistorialAvisos.kt
│   ├── PantallaAvisos.kt
│   └── theme/
└── viewmodel/
    ├── AvisosViewModel.kt
    └── AvisosViewModelFactory.kt
```

## Arquitectura MVVM + Repository

```mermaid
flowchart LR
    V[PantallaAvisos] -->|acciones| VM[AvisosViewModel]
    VM -->|registra avisos| R[AvisosRepository]
    R --> RM[AvisosRepositoryMemoria]
    VM -->|evento de aviso| V
    V -->|Context y aviso| N[Notificador]
    N --> A[Notificaciones Android]
    V -->|registra y quita| BR[ModoAvionReceiver]
    BR -->|estado del modo avión| VM
```

El ViewModel actualiza el estado y registra avisos mediante el repositorio. La pantalla recoge los eventos y llama al notificador, ya que es la capa que dispone de `Context`. El receptor comunica el cambio del modo avión al ViewModel y se desregistra cuando deja de estar activo en la pantalla.

## Capturas de la aplicación

### Notificación en el panel

![Notificación en el panel de Android](capturas/notificacion.png)

### Cuenta regresiva del recordatorio

![Cuenta regresiva del recordatorio](capturas/recordatorio.png)

### Receptor del modo avión

![Aviso generado por el cambio del modo avión](capturas/modo-avion.png)

### Historial de avisos

![Historial de avisos recientes](capturas/historial.png)

### Permiso denegado

![Mensaje al denegar el permiso de notificaciones](capturas/permiso-denegado.png)

## Respuestas para pensar

1. **¿Por qué una notificación necesita un canal y un permiso?**
   El canal permite configurar cómo Android presenta las notificaciones de la app. En Android 13 o superior, la app también debe pedir permiso. Sin canal, la notificación no se puede clasificar; sin permiso, Android no la muestra.

2. **¿Por qué el ViewModel emite un evento en vez de mostrar la notificación?**
   El ViewModel no debe conocer `Context` ni gestionar APIs de notificaciones. Emite un evento y la pantalla, que sí tiene acceso al contexto, se encarga de llamar al notificador.

3. **¿Qué pasa si se usa el mismo ID para dos notificaciones?**
   La nueva notificación puede reemplazar a la anterior. El repositorio asigna un ID distinto a cada aviso.

4. **¿Por qué `onReceive` debe ser corto y hay que quitar el receptor?**
   `onReceive` corre en el hilo principal y debe terminar pronto. Desregistrar el receptor al salir evita que siga escuchando y ayuda a prevenir fugas de memoria.

5. **¿Cuándo usar una notificación local y cuándo una notificación push de Firebase?**
   Una notificación local sirve para avisos que la app puede generar en el dispositivo, como un recordatorio. Una push de Firebase sirve para comunicar un evento enviado desde un servidor, incluso si la app no lo generó localmente.
