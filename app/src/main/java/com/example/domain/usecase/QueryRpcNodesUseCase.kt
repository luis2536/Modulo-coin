package com.example.domain.usecase

import com.example.domain.model.RpcNode
import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.random.Random

class QueryRpcNodesUseCase {
    private val nodosPredeterminados = listOf(
        Pair("Ethereum Sepolia", "https://ethereum-sepolia-rpc.publicnode.com"),
        Pair("Arbitrum Sepolia", "https://sepolia-rollup.arbitrum.io/rpc"),
        Pair("Optimism Sepolia", "https://sepolia.optimism.io"),
        Pair("Base Sepolia", "https://sepolia.base.org"),
        Pair("Polygon Amoy", "https://rpc-amoy.polygon.technology")
    )

    // Lista de User-Agents simulados para rotación y evasión de bloqueos en redes de prueba
    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1"
    )

    suspend operator fun invoke(): List<RpcNode> {
        // Simulación de consultas RPC asíncronas con rotación de cabeceras de red
        return nodosPredeterminados.map { (nombre, url) ->
            val userAgentSeleccionado = userAgents.random()
            val latencia = Random.nextLong(45, 350)
            delay(Random.nextLong(100, 300)) // Simulación de tiempo de red
            
            // Generar una altura de bloque realista para la red correspondiente
            val bloqueBase = when (nombre) {
                "Ethereum Sepolia" -> 6200000L
                "Arbitrum Sepolia" -> 84000000L
                "Optimism Sepolia" -> 118000000L
                "Base Sepolia" -> 16000000L
                else -> 9200000L
            }
            val bloqueActual = bloqueBase + (System.currentTimeMillis() / 12000) % 50000

            RpcNode(
                nombre = nombre,
                url = url,
                bloqueActual = bloqueActual,
                latenciaMs = latencia,
                activo = true
            )
        }
    }
}
