package com.example.domain.usecase

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.domain.model.ResultWrapper

class AnalyzeThreatsUseCase(private val generativeModel: GenerativeModel) {
    operator fun invoke(logs: List<String>): Flow<ResultWrapper<String>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val prompt = "Actúa como un sistema de inteligencia táctica OMNI. Analiza estos logs de red y busca posibles vulnerabilidades o accesos no autorizados. Responde con un reporte conciso militar de máximo 3 oraciones. Logs: ${logs.joinToString(", ")}"
            val response = generativeModel.generateContent(prompt)
            val resultText = response.text ?: "Sin hallazgos reportados."
            emit(ResultWrapper.Success(resultText))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e, "Falla de enlace neuronal AI: ${e.message}"))
        }
    }
}
