package com.example.di

import com.example.data.local.AppDatabase
import com.example.data.network.GhostShieldInterceptor
import com.example.data.repository.LogRepositoryImpl
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.repository.LogRepository
import com.example.domain.repository.TaskRepository
import com.example.domain.usecase.QueryRpcNodesUseCase
import com.example.MainViewModel
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

    // Use Cases
    single { QueryRpcNodesUseCase() }

    // ViewModel
    viewModel { MainViewModel(get(), get(), get(), get()) }
}
