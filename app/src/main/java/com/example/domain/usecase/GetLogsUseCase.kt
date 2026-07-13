package com.example.domain.usecase

import com.example.domain.model.LogEntry
import com.example.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class GetLogsUseCase(private val repository: LogRepository) {
    operator fun invoke(): Flow<List<LogEntry>> {
        return repository.obtenerLogs()
    }
}
