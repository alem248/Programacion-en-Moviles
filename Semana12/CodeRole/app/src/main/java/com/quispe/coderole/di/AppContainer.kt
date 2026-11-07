package com.quispe.coderole.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quispe.coderole.data.auth.AuthRepository
import com.quispe.coderole.data.auth.FirebaseAuthRepository
import com.quispe.coderole.data.local.AppDatabase
import com.quispe.coderole.data.notification.Notificador
import com.quispe.coderole.data.notification.NotificadorAndroid
import com.quispe.coderole.data.remote.RetrofitClient
import com.quispe.coderole.data.repository.PersonajeRepositoryImpl
import com.quispe.coderole.domain.repository.ItemRepository

class AppContainer(contexto: Context) {
    private val database = Room.databaseBuilder(
        contexto.applicationContext,
        AppDatabase::class.java,
        "coderole.db",
    ).build()

    val personajes: ItemRepository = PersonajeRepositoryImpl(
        api = RetrofitClient.personajes,
        dao = database.personajeDao(),
    )
    val auth: AuthRepository = FirebaseAuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    val notificador: Notificador = NotificadorAndroid(contexto)
}
