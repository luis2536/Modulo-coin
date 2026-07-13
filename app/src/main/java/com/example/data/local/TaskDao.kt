package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY fechaCreacion DESC")
    fun obtenerTodasLasTareas(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TaskEntity)

    @Update
    suspend fun actualizarTarea(tarea: TaskEntity)

    @Delete
    suspend fun eliminarTarea(tarea: TaskEntity)
}
