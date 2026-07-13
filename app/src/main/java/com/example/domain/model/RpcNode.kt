package com.example.domain.model

/**
 * Representa un nodo RPC Web3 (DePIN / Testnets) de solo lectura consultado para telemetría.
 */
data class RpcNode(
    val nombre: String,
    val url: String,
    val bloqueActual: Long,
    val latenciaMs: Long,
    val activo: Boolean
)
