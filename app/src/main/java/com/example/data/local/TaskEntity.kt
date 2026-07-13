package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    fun toDomain(): Task = Task(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        completada = completada,
        fechaCreacion = fechaCreacion
    )

    companion object {
        fun fromDomain(task: Task): TaskEntity = TaskEntity(
            id = task.id,
            titulo = task.titulo,
            descripcion = task.descripcion,
            completada = task.completada,
            fechaCreacion = task.fechaCreacion
        )
    }
}
