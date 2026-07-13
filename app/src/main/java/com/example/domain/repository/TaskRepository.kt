package com.example.domain.repository

import com.example.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión de tareas.
 */
interface TaskRepository {
    fun obtenerTareas(): Flow<List<Task>>
    suspend fun insertarTarea(tarea: Task)
    suspend fun actualizarTarea(tarea: Task)
    suspend fun eliminarTarea(tarea: Task)
}
