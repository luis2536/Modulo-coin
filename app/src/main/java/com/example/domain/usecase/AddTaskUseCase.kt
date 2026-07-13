package com.example.domain.usecase

import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(titulo: String, descripcion: String) {
        val nuevaTarea = Task(titulo = titulo, descripcion = descripcion)
        repository.insertarTarea(nuevaTarea)
    }
}
