package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.LogEntry

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mensaje: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): LogEntry = LogEntry(
        id = id,
        mensaje = mensaje,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(log: LogEntry): LogEntity = LogEntity(
            id = log.id,
            mensaje = log.mensaje,
            timestamp = log.timestamp
        )
    }
}
