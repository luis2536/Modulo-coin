package com.example.data.repository

import com.example.data.local.LogDao
import com.example.data.local.LogEntity
import com.example.domain.model.LogEntry
import com.example.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LogRepositoryImpl(private val logDao: LogDao) : LogRepository {
    override fun obtenerLogs(): Flow<List<LogEntry>> {
        return logDao.obtenerLogs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun agregarLog(mensaje: String) {
        logDao.agregarLog(LogEntity(mensaje = mensaje))
    }

    override suspend fun limpiarLogs() {
        logDao.limpiarLogs()
    }
}
