package com.example.domain.usecase

import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(tarea: Task) {
        repository.eliminarTarea(tarea)
    }
}
