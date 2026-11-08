package com.quispe.coderole.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.quispe.coderole.domain.model.ActividadDiaria
import com.quispe.coderole.domain.model.RegistroActividad
import com.quispe.coderole.domain.model.TipoActividad
import com.quispe.coderole.domain.repository.ActividadRepository
import com.quispe.coderole.domain.util.Fechas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Actividad diaria en Firestore: el documento `actividad/{aaaa-MM-dd}` acumula
 * contadores con `FieldValue.increment` y guarda cada interacción en su
 * subcolección `registros`.
 *
 * [registrar] y [registrarTiempoUso] corren en segundo plano sin bloquear la UI.
 */
class FirestoreActividadRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ActividadRepository {
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun registrar(tipo: TipoActividad, detalle: String) {
        val uid = auth.currentUser?.uid ?: return
        val fecha = Fechas.hoy()
        alcance.launch {
            runCatching {
                val dia = firestore.collection(COLECCION).document(fecha)
                val lote = firestore.batch()
                lote.set(
                    dia,
                    mapOf(
                        "fecha" to fecha,
                        tipo.contador to FieldValue.increment(1),
                    ),
                    SetOptions.merge(),
                )
                lote.set(
                    dia.collection(REGISTROS).document(),
                    mapOf(
                        "tipo" to tipo.name,
                        "uid" to uid,
                        "detalle" to detalle.take(DETALLE_MAXIMO),
                        "creadoEn" to FieldValue.serverTimestamp(),
                    ),
                )
                lote.commit().await()
            }
        }
    }

    override fun registrarTiempoUso(minutos: Int) {
        if (minutos <= 0) return
        val uid = auth.currentUser?.uid ?: return
        val fecha = Fechas.hoy()
        alcance.launch {
            runCatching {
                val dia = firestore.collection(COLECCION).document(fecha)
                val lote = firestore.batch()
                lote.set(
                    dia,
                    mapOf(
                        "fecha" to fecha,
                        "tiempoUsoMinutos" to FieldValue.increment(minutos.toLong()),
                    ),
                    SetOptions.merge(),
                )
                lote.set(
                    dia.collection(REGISTROS).document(),
                    mapOf(
                        "tipo" to TipoActividad.TIEMPO_USO.name,
                        "uid" to uid,
                        "detalle" to "Uso activo de la mentora: $minutos min",
                        "creadoEn" to FieldValue.serverTimestamp(),
                    ),
                )
                lote.commit().await()
            }
        }
    }

    override fun observarDias(dias: Int): Flow<List<ActividadDiaria>> = callbackFlow {
        val registration = firestore.collection(COLECCION)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(dias.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else if (snapshot != null) {
                    val diasActividad = snapshot.documents.map { documento ->
                        ActividadDiaria(
                            fecha = documento.getString("fecha") ?: documento.id,
                            mensajes = documento.getLong("mensajes")?.toInt() ?: 0,
                            respuestas = documento.getLong("respuestas")?.toInt() ?: 0,
                            favoritos = documento.getLong("favoritos")?.toInt() ?: 0,
                            sesiones = documento.getLong("sesiones")?.toInt() ?: 0,
                            tiempoUsoMinutos = documento.getLong("tiempoUsoMinutos")?.toInt() ?: 0,
                        )
                    }
                    trySend(diasActividad)
                }
            }
        awaitClose { registration.remove() }
    }

    override fun observarRegistros(fecha: String, limite: Int): Flow<List<RegistroActividad>> = callbackFlow {
        val registration = firestore.collection(COLECCION).document(fecha)
            .collection(REGISTROS)
            .orderBy("creadoEn", Query.Direction.DESCENDING)
            .limit(limite.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else if (snapshot != null) {
                    val registros = snapshot.documents.mapNotNull { documento ->
                        val nombre = documento.getString("tipo") ?: return@mapNotNull null
                        val tipo = runCatching { TipoActividad.valueOf(nombre) }.getOrNull()
                            ?: return@mapNotNull null
                        RegistroActividad(
                            id = documento.id,
                            tipo = tipo,
                            detalle = documento.getString("detalle").orEmpty(),
                            creadoEn = documento.getTimestamp("creadoEn")?.toDate()?.time ?: 0L,
                        )
                    }
                    trySend(registros)
                }
            }
        awaitClose { registration.remove() }
    }

    private companion object {
        const val COLECCION = "actividad"
        const val REGISTROS = "registros"
        const val DETALLE_MAXIMO = 140
    }
}

/** Nombre del campo contador asociado a cada tipo de interacción. */
private val TipoActividad.contador: String
    get() = when (this) {
        TipoActividad.MENSAJE -> "mensajes"
        TipoActividad.RESPUESTA -> "respuestas"
        TipoActividad.FAVORITO -> "favoritos"
        TipoActividad.SESION -> "sesiones"
        TipoActividad.TIEMPO_USO -> "tiempoUsoMinutos"
    }
