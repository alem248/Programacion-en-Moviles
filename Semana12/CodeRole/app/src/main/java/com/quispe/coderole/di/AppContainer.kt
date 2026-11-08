package com.quispe.coderole.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quispe.coderole.data.chat.FirebaseChatRepository
import com.quispe.coderole.data.chat.GeminiChatService
import com.quispe.coderole.data.auth.AuthRepository
import com.quispe.coderole.data.auth.FirebaseAuthRepository
import com.quispe.coderole.data.local.AppDatabase
import com.quispe.coderole.data.notification.Notificador
import com.quispe.coderole.data.notification.NotificadorAndroid
import com.quispe.coderole.data.prefs.PreferenciasTema
import com.quispe.coderole.data.remote.RetrofitClient
import com.quispe.coderole.data.repository.FirestoreActividadRepository
import com.quispe.coderole.data.repository.FirestoreCapasRepository
import com.quispe.coderole.data.repository.FirestorePerfilRepository
import com.quispe.coderole.data.repository.PersonajeRepositoryImpl
import com.quispe.coderole.domain.repository.ItemRepository
import com.quispe.coderole.domain.repository.ActividadRepository
import com.quispe.coderole.domain.repository.CapasRepository
import com.quispe.coderole.domain.repository.ChatRepository
import com.quispe.coderole.domain.repository.PerfilRepository

import com.google.firebase.FirebaseApp

class AppContainer(contexto: Context) {
    init {
        if (FirebaseApp.getApps(contexto).isEmpty()) {
            FirebaseApp.initializeApp(contexto)
        }
    }

    private val firestore get() = FirebaseFirestore.getInstance()
    private val authFirebase get() = FirebaseAuth.getInstance()

    private val database = Room.databaseBuilder(
        contexto.applicationContext,
        AppDatabase::class.java,
        "coderole.db",
    ).build()

    val personajes: ItemRepository = PersonajeRepositoryImpl(
        api = RetrofitClient.personajes,
        dao = database.personajeDao(),
    )
    val auth: AuthRepository = FirebaseAuthRepository(authFirebase, firestore)

    val actividad: ActividadRepository = FirestoreActividadRepository(authFirebase, firestore)

    val capas: CapasRepository = FirestoreCapasRepository(firestore)

    /** Foto y nombre de usuario en tiempo real (Firestore `usuarios/{uid}`). */
    val perfil: PerfilRepository = FirestorePerfilRepository(authFirebase, firestore)

    /** Preferencia del modo oscuro elegida en el perfil. */
    val tema: PreferenciasTema = PreferenciasTema(contexto)

    val chat: ChatRepository = FirebaseChatRepository(
        auth = authFirebase,
        firestore = firestore,
        gemini = GeminiChatService(),
        actividad = actividad,
    )

    val notificador: Notificador = NotificadorAndroid(contexto)
}
