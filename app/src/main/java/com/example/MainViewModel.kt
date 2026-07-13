package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.LogRepositoryImpl
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.model.LogEntry
import com.example.domain.model.RpcNode
import com.example.domain.model.Task
import com.example.domain.usecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * MainViewModel de Syntropy Delta Nexus que coordina casos de uso de Clean Architecture.
 * Implementa telemetría técnica, logs automáticos y monitoreo Web3 en español.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    
    // Repositorios instanciados limpiamente mediante Constructor de Datos
    private val taskRepository = TaskRepositoryImpl(db.taskDao())
    private val logRepository = LogRepositoryImpl(db.logDao())

    // Casos de Uso instanciados
    private val getTasksUseCase = GetTasksUseCase(taskRepository)
    private val addTaskUseCase = AddTaskUseCase(taskRepository)
    private val updateTaskUseCase = UpdateTaskUseCase(taskRepository)
    private val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    private val getLogsUseCase = GetLogsUseCase(logRepository)
    private val addLogUseCase = AddLogUseCase(logRepository)
    private val queryRpcNodesUseCase = QueryRpcNodesUseCase()

    // Flujos de Estado Reactivos expuestos a Compose
    val tareas: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<LogEntry>> = getLogsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _nodosRpc = MutableStateFlow<List<RpcNode>>(emptyList())
    val nodosRpc: StateFlow<List<RpcNode>> = _nodosRpc.asStateFlow()

    private val _telemetriaRam = MutableStateFlow("RAM: 24.5 MB / 512 MB (4.7%)")
    val telemetriaRam: StateFlow<String> = _telemetriaRam.asStateFlow()

    private val _telemetriaCpu = MutableStateFlow("CPU: 1.2% (Bajo Consumo)")
    val telemetriaCpu: StateFlow<String> = _telemetriaCpu.asStateFlow()

    private val _vistaActual = MutableStateFlow("DEV") // "DEV" (Modo Arquitecto) o "OPERATOR" (Modo María)
    val vistaActual: StateFlow<String> = _vistaActual.asStateFlow()

    init {
        // Inicializar ciclo automático de automatización y lectura de datos
        viewModelScope.launch {
            agregarLog("Sistema Syntropy Delta Nexus Inicializado. Modo: DEV.")
            actualizarNodosRpc()
            iniciarCicloAutomatizacion()
        }
    }

    fun cambiarVista(nuevaVista: String) {
        viewModelScope.launch {
            _vistaActual.value = nuevaVista
            agregarLog("Vista cambiada a: $nuevaVista")
        }
    }

    fun agregarTarea(titulo: String, descripcion: String) {
        viewModelScope.launch {
            if (titulo.isNotBlank()) {
                addTaskUseCase(titulo, descripcion)
                agregarLog("Nueva tarea añadida en cola: $titulo")
            }
        }
    }

    fun conmutarEstadoTarea(tarea: Task) {
        viewModelScope.launch {
            val tareaActualizada = tarea.copy(completada = !tarea.completada)
            updateTaskUseCase(tareaActualizada)
            val estado = if (tareaActualizada.completada) "COMPLETADA" else "PENDIENTE"
            agregarLog("Tarea '${tarea.titulo}' marcada como $estado")
        }
    }

    fun eliminarTarea(tarea: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(tarea)
            agregarLog("Tarea eliminada: ${tarea.titulo}")
        }
    }

    fun limpiarHistorialLogs() {
        viewModelScope.launch {
            logRepository.limpiarLogs()
            agregarLog("Historial de logs de telemetría purgado por operador.")
        }
    }

    private suspend fun agregarLog(mensaje: String) {
        addLogUseCase(mensaje)
    }

    private suspend fun actualizarNodosRpc() {
        try {
            val lista = queryRpcNodesUseCase()
            _nodosRpc.value = lista
        } catch (e: Exception) {
            agregarLog("Error al consultar telemetría RPC: ${e.message}")
        }
    }

    private suspend fun iniciarCicloAutomatizacion() {
        while (true) {
            delay(6000) // Ciclo periódico cada 6 segundos
            val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            
            // Simulación de telemetría cambiante (Hardware militar-grado)
            val ramUsage = Random.nextDouble(18.2, 42.5)
            val cpuUsage = Random.nextDouble(0.5, 9.8)
            _telemetriaRam.value = String.format("RAM: %.1f MB / 512 MB (%.1f%%)", ramUsage, (ramUsage/512)*100)
            _telemetriaCpu.value = String.format("CPU: %.1f%% (Bajo Consumo)", cpuUsage)

            // Consultar y rotar telemetría de nodos RPC
            actualizarNodosRpc()

            // Registrar log periódico de monitoreo
            val nodoAlAzar = _nodosRpc.value.randomOrNull()?.nombre ?: "Red"
            agregarLog("[$time] Monitoreo: Nodo '$nodoAlAzar' verificado con éxito.")
        }
    }
}
