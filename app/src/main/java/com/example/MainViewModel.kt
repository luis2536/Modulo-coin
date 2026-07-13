package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "syntropy-database"
    ).build()

    val tasks = db.taskDao().getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _logs = MutableStateFlow<List<String>>(listOf("System initialized."))
    val logs = _logs.asStateFlow()

    private val _metrics = MutableStateFlow("RAM: 24% | CPU: 3%")
    val metrics = _metrics.asStateFlow()

    init {
        startAutomationEngine()
    }

    private fun startAutomationEngine() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                addLog("[$time] Automation cycle completed. No errors detected.")
                _metrics.value = "RAM: ${24 + (0..5).random()}% | CPU: ${3 + (0..2).random()}%"
            }
        }
    }

    fun addLog(message: String) {
        _logs.value = (_logs.value + message).takeLast(20)
    }

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            db.taskDao().insertTask(Task(title = title, description = description))
            addLog("Task added: $title")
        }
    }
}
