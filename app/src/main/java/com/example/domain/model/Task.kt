package com.example.domain.model

/**
 * Modelo de datos puro que representa una tarea en el sistema Syntropy Delta Nexus.
 */
data class Task(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)
