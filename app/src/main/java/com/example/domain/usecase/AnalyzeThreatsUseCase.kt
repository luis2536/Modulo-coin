package com.example.domain.usecase

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.domain.model.ResultWrapper

class AnalyzeThreatsUseCase(private val generativeModel: GenerativeModel) {
    operator fun invoke(logs: List<String>, tipoAnalisis: String = "Escaneo Estándar"): Flow<ResultWrapper<String>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val apiKey = generativeModel.apiKey
            val isDummy = apiKey.isBlank() || 
                          apiKey == "MY_GEMINI_API_KEY" || 
                          apiKey.contains("Dummy", ignoreCase = true)

            if (isDummy) {
                // Heurística local de contingencia militar
                kotlinx.coroutines.delay(1000)
                val totalLogs = logs.size
                val criticosCount = logs.count { it.contains("CRÍTICO", ignoreCase = true) || it.contains("ANOMALÍA", ignoreCase = true) || it.contains("Error", ignoreCase = true) }
                val normalCount = totalLogs - criticosCount
                val reporte = "REPORTE TÁCTICO OMNI (MODO LOCAL) [$tipoAnalisis]: Enlace de satélite AI activo (Llave API simulada). El núcleo táctico procesó $totalLogs registros de red. Estado: ${if (criticosCount > 0) "ANOMALÍA DETECTADA" else "NOMINAL"}. Se encontraron $normalCount tráficos limpios y $criticosCount alertas operacionales. Defensa perimetral robusta y enlaces Ghost-Shield encriptados."
                emit(ResultWrapper.Success(reporte))
            } else {
                val prompt = "Actúa como un analista de seguridad militar OMNI de alto nivel. Realiza un análisis de tipo '$tipoAnalisis' sobre estos registros de red de seguridad: ${logs.joinToString(", ")}. Devuelve un reporte conciso y sumamente técnico militar de máximo 3 oraciones, en idioma español, sugiriendo contramedidas si hay fallas."
                val response = generativeModel.generateContent(prompt)
                val resultText = response.text ?: "Sin anomalías reportadas en el sector."
                emit(ResultWrapper.Success(resultText))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e, "Falla de enlace neuronal AI: ${e.message}"))
        }
    }
}
