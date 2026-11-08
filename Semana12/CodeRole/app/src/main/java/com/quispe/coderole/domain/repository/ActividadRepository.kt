package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.ActividadDiaria
import com.quispe.coderole.domain.model.RegistroActividad
import com.quispe.coderole.domain.model.TipoActividad
import kotlinx.coroutines.flow.Flow

/**
 * Métricas de interacción por día para el panel del mentor.
 * Las escrituras son en segundo plano: registrar actividad nunca frena al usuario.
 */
interface ActividadRepository {
    /** Cuenta la interacción del día en curso (mensaje, respuesta, favorito o sesión). */
    fun registrar(tipo: TipoActividad, detalle: String = "")

    /** Registra tiempo de uso activo en minutos acumulado por todos los usuarios. */
    fun registrarTiempoUso(minutos: Int)

    /** Últimos [dias] días con actividad, del más reciente al más antiguo. */
    fun observarDias(dias: Int = DIAS_POR_DEFECTO): Flow<List<ActividadDiaria>>

    /** Registros de interacción de un día concreto, del más reciente al más antiguo. */
    fun observarRegistros(fecha: String, limite: Int = REGISTROS_POR_DEFECTO): Flow<List<RegistroActividad>>

    companion object {
        const val DIAS_POR_DEFECTO = 14
        const val REGISTROS_POR_DEFECTO = 15
    }
}
