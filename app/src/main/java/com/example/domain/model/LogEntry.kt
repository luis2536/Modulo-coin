package com.example.domain.model

/**
 * Representa una entrada de log técnica del sistema de telemetría.
 */
data class LogEntry(
    val id: Int = 0,
    val mensaje: String,
    val timestamp: Long = System.currentTimeMillis()
)
