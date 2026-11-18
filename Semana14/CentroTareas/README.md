# CentroTareas

Aplicación Android (Kotlin + Jetpack Compose + Material 3, sin XML) que realiza tareas
deliberadamente lentas **sin congelar la pantalla**:

1. Una tarea con barra de progreso.
2. Un cálculo pesado: contar números primos.
3. Dos cargas de datos simuladas (secuencial vs. paralela con `async`).

**Arquitectura:** MVVM + Repository (interfaz) con corrutinas (`viewModelScope`,
`withContext`, `Dispatchers`, `Job`, `async`) y `StateFlow` para exponer el estado a la vista.

```
com.quispe.centrotareas/
├── MainActivity.kt                 [E]
├── model/
│   └── Estados.kt                  [E]  PrimosUi, CargaUi
├── data/
│   ├── TareasRepository.kt         [E]  interface
│   └── TareasRepositoryLocal.kt    [G]  cargas simuladas y contarPrimos
├── viewmodel/
│   ├── TareasViewModel.kt          [G]  lógica de segundo plano
│   └── TareasViewModelFactory.kt   [E]
└── ui/
    ├── PantallaTareas.kt           [G]  las tres tarjetas
    └── theme/                      [E]
```

## Observación del Paso 1

**Qué pasa con la pantalla:** al pulsar «Tarea larga (MAL)», la app se congela durante
4 segundos. Durante ese tiempo nada responde: el botón «Tócame» deja de contar, la
interacción táctil se ignora y la interfaz queda completamente congelada. Es exactamente
el problema que ocurre cuando una tarea larga corre en el hilo principal.

**Por qué no se ve «Trabajando...»:** `tareaLargaMala()` se ejecuta en el hilo principal
(`main`), el mismo hilo que dibuja la pantalla y atiende los toques. El estado
`_mensaje` sí cambia a «Trabajando...» y Compose programa la recomposición, pero esa
recomposición solo puede dibujarse cuando el hilo principal vuelve al bucle de mensajes
de Android. Como `Thread.sleep(4000)` lo mantiene dormido 4 segundos, la interfaz nunca
llega a pintar ese texto: cuando el hilo despierta, en el mismo ciclo ya se escribe
«Terminó», así que «Trabajando...» queda descartada antes de llegar al display.

**Qué mostraría Android si la espera fuera de 10 segundos:** el sistema detecta que el
hilo principal no responde durante aproximadamente 5 segundos y muestra el diálogo **ANR**
(*Application Not Responding*: «La aplicación no responde») con las opciones «Esperar» o
«Cerrar la aplicación». Además, el Play Store y Android 11+ pueden calificar/matar la app
por no responder, y el usuario percibiría la app como rota.
