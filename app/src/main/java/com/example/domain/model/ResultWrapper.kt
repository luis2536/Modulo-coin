package com.example.domain.model

/**
 * Envoltorio seguro para encapsular estados de operaciones de red y de persistencia local.
 * Garantiza que ningún error o excepción imprevista interrumpa la estabilidad del sistema.
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val exception: Throwable, val mensaje: String) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}
