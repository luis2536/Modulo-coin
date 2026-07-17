package com.example.di

import com.example.data.local.AppDatabase
import com.example.data.network.GhostShieldInterceptor
import com.example.data.repository.LogRepositoryImpl
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.repository.LogRepository
import com.example.domain.repository.TaskRepository
import com.example.domain.usecase.QueryRpcNodesUseCase
import com.example.domain.usecase.AnalyzeThreatsUseCase
import com.example.MainViewModel
import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import okhttp3.OkHttpClient

val appModule = module {
    // Database
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().logDao() }

    // Repositories
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<LogRepository> { LogRepositoryImpl(get()) }

    // Network & Security
    single { GhostShieldInterceptor() }
    single { 
        OkHttpClient.Builder()
            .addInterceptor(get<GhostShieldInterceptor>())
            .build()
    }

    // AI Configuration
    single { 
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.2f
            }
        ) 
    }

    // Use Cases
    single { QueryRpcNodesUseCase() }
    single { AnalyzeThreatsUseCase(get()) }

    // ViewModel
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
}
