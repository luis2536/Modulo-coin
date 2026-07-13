package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override fun obtenerTareas(): Flow<List<Task>> {
        return taskDao.obtenerTodasLasTareas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertarTarea(tarea: Task) {
        taskDao.insertarTarea(TaskEntity.fromDomain(tarea))
    }

    override suspend fun actualizarTarea(tarea: Task) {
        taskDao.actualizarTarea(TaskEntity.fromDomain(tarea))
    }

    override suspend fun eliminarTarea(tarea: Task) {
        taskDao.eliminarTarea(TaskEntity.fromDomain(tarea))
    }
}
