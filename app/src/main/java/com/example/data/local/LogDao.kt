package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY id DESC LIMIT 50")
    fun obtenerLogs(): Flow<List<LogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarLog(log: LogEntity)

    @Query("DELETE FROM logs")
    suspend fun limpiarLogs()
}
