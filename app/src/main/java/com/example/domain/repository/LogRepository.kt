package com.example.domain.repository

import com.example.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para el almacenamiento local de logs de telemetría.
 */
interface LogRepository {
    fun obtenerLogs(): Flow<List<LogEntry>>
    suspend fun agregarLog(mensaje: String)
    suspend fun limpiarLogs()
}
