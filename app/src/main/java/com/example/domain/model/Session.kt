package com.example.domain.model

/**
 * Representa el estado de sesión activa y privilegios asignados en el ecosistema.
 */
data class Session(
    val id: String,
    val nombreUsuario: String,
    val rol: RolOperativo,
    val llaveCifrada: String,
    val ultimoAcceso: Long = System.currentTimeMillis()
)

enum class RolOperativo {
    ADMINISTRADOR, // Modo Arquitecto
    OPERADORA     // Modo María
}
