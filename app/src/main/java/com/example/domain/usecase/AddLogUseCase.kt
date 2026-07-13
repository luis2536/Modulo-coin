package com.example.domain.usecase

import com.example.domain.repository.LogRepository

class AddLogUseCase(private val repository: LogRepository) {
    suspend operator fun invoke(mensaje: String) {
        repository.agregarLog(mensaje)
    }
}
