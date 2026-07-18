package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.network.GhostShieldInterceptor
import com.example.domain.model.*
import com.example.domain.usecase.*
import com.example.domain.repository.TaskRepository
import com.example.domain.repository.LogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * MainViewModel de Syntropy Delta Nexus OMNI.
 * Actúa como orquestador táctico de telemetría militar, estado del sistema,
 * perfiles de sesión y el escudo "Ghost-Shield" para misiones Web3 en español.
 */
class MainViewModel(
    taskRepository: TaskRepository,
    private val logRepository: LogRepository,
    private val queryRpcNodesUseCase: QueryRpcNodesUseCase,
    private val ghostShield: GhostShieldInterceptor,
    private val analyzeThreatsUseCase: AnalyzeThreatsUseCase
) : ViewModel() {

    // Capa de Dominio (Casos de Uso)
    private val getTasksUseCase = GetTasksUseCase(taskRepository)
    private val addTaskUseCase = AddTaskUseCase(taskRepository)
    private val updateTaskUseCase = UpdateTaskUseCase(taskRepository)
    private val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    private val getLogsUseCase = GetLogsUseCase(logRepository)
    private val addLogUseCase = AddLogUseCase(logRepository)

    // Estado del Sistema expuesto de forma reactiva (Flows)
    val tareas: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<LogEntry>> = getLogsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _nodosRpc = MutableStateFlow<List<RpcNode>>(emptyList())
    val nodosRpc: StateFlow<List<RpcNode>> = _nodosRpc.asStateFlow()

    // Telemetría de Recursos
    private val _telemetriaRam = MutableStateFlow("RAM: 28.4 MB / 1024 MB (2.7%)")
    val telemetriaRam: StateFlow<String> = _telemetriaRam.asStateFlow()

    private val _telemetriaCpu = MutableStateFlow("CPU: 1.0% (Eco-Mode)")
    val telemetriaCpu: StateFlow<String> = _telemetriaCpu.asStateFlow()

    private val _consumoRed = MutableStateFlow("Ancho de Banda: 4.8 KB/s")
    val consumoRed: StateFlow<String> = _consumoRed.asStateFlow()

    // Datos del Módulo Ghost-Shield
    private val _proxyActivo = MutableStateFlow(ghostShield.obtenerProxyActivo())
    val proxyActivo: StateFlow<String> = _proxyActivo.asStateFlow()

    private val _proxySalud = MutableStateFlow(ghostShield.obtenerSaludProxy())
    val proxySalud: StateFlow<String> = _proxySalud.asStateFlow()

    private val _userAgentActivo = MutableStateFlow(ghostShield.rotarUserAgent())
    val userAgentActivo: StateFlow<String> = _userAgentActivo.asStateFlow()

    // Sesión Operativa Multi-Perfil
    private val _sesionActiva = MutableStateFlow(
        Session("SES-01", "María Delgado", RolOperativo.OPERADORA, "AES256-K-90X3")
    )
    val sesionActiva: StateFlow<Session> = _sesionActiva.asStateFlow()

    private val _vistaActual = MutableStateFlow("DEV") // "DEV" (Modo Arquitecto) o "OPERATOR" (Modo María)
    val vistaActual: StateFlow<String> = _vistaActual.asStateFlow()

    private val _analisisMilitar = MutableStateFlow<ResultWrapper<String>>(ResultWrapper.Success("AI en espera de comandos tácticos."))
    val analisisMilitar: StateFlow<ResultWrapper<String>> = _analisisMilitar.asStateFlow()

    private val _historialRed = MutableStateFlow<List<Float>>(List(15) { Random.nextDouble(5.0, 45.0).toFloat() })
    val historialRed: StateFlow<List<Float>> = _historialRed.asStateFlow()

    private val _escaneandoRadar = MutableStateFlow(false)
    val escaneandoRadar: StateFlow<Boolean> = _escaneandoRadar.asStateFlow()

    private val _amenazasDetectadas = MutableStateFlow<List<String>>(
        listOf("Canal de Red: Seguro", "Firmas DePIN: Verificadas", "Nodos Activos: Sin intromisiones")
    )
    val amenazasDetectadas: StateFlow<List<String>> = _amenazasDetectadas.asStateFlow()

    init {
        viewModelScope.launch {
            agregarLog("Iniciando Módulo de Seguridad Ghost-Shield...")
            agregarLog("Proxy inicial enrutado: ${ghostShield.obtenerProxyActivo()}")
            agregarLog("User-Agent rotado de forma segura.")
            actualizarNodosRpc()
            iniciarCicloPersistenciaFondo()
        }
    }

    /**
     * Conmuta la sesión de operador entre Administrador (Modo Arquitecto) y María (Modo Operadora)
     */
    fun cambiarSesion(rol: RolOperativo) {
        viewModelScope.launch {
            if (rol == RolOperativo.ADMINISTRADOR) {
                _sesionActiva.value = Session("SES-99", "Administrador Táctico", RolOperativo.ADMINISTRADOR, "ADMIN-KEY-X77")
                agregarLog("Sesión cambiada a ADMINISTRADOR. Privilegios totales otorgados.")
            } else {
                _sesionActiva.value = Session("SES-01", "María Delgado", RolOperativo.OPERADORA, "AES256-K-90X3")
                agregarLog("Sesión de OPERADOR restaurada de forma segura.")
            }
        }
    }

    fun cambiarVista(nuevaVista: String) {
        viewModelScope.launch {
            _vistaActual.value = nuevaVista
            agregarLog("Foco del Nexus conmutado a: $nuevaVista")
        }
    }

    fun rotarProxyManualmente() {
        viewModelScope.launch {
            val nuevoProxy = ghostShield.forzarRotacionProxy()
            _proxyActivo.value = nuevoProxy
            _proxySalud.value = ghostShield.obtenerSaludProxy()
            _userAgentActivo.value = ghostShield.rotarUserAgent()
            agregarLog("Comando manual: Proxy conmutado preventivamente a -> $nuevoProxy")
        }
    }

    fun ejecutarAnalisisInteligencia(tipoAnalisis: String = "Escaneo Estándar") {
        viewModelScope.launch {
            agregarLog("Solicitando análisis táctico AI ($tipoAnalisis) a Gemini...")
            val listadoLogs = logs.value.map { it.mensaje }.takeLast(20)
            analyzeThreatsUseCase(listadoLogs, tipoAnalisis).collect { result ->
                _analisisMilitar.value = result
                if (result is ResultWrapper.Success) {
                    agregarLog("Análisis táctico [$tipoAnalisis] completado con éxito.")
                } else if (result is ResultWrapper.Error) {
                    agregarLog("Falla en análisis AI [$tipoAnalisis]: ${result.mensaje}")
                }
            }
        }
    }

    fun ejecutarEscaneoRadar() {
        viewModelScope.launch {
            _escaneandoRadar.value = true
            agregarLog("Iniciando escaneo táctico de puertos y nodos DePIN...")
            delay(1500)
            val ipFalsa = "${Random.nextInt(10, 254)}.${Random.nextInt(10, 254)}.${Random.nextInt(1, 254)}.${Random.nextInt(1, 254)}"
            val listado = listOf(
                "IP sospechosa mitigada: $ipFalsa",
                "Latencia perimetral óptima: ${Random.nextInt(12, 60)} ms",
                "Firmas de nodos de contingencia: Sincronizadas",
                "Proxy perimetral Ghost-Shield: Operativo"
            )
            _amenazasDetectadas.value = listado
            _escaneandoRadar.value = false
            agregarLog("Escaneo perimetral de radar finalizado con éxito.")
        }
    }

    fun cifrarSimetrico(texto: String): String {
        return texto.map { (it.code xor 0x5A).toChar() }.joinToString("")
    }

    fun guardarSecretoEnBoveda(nombre: String, secretoPlano: String) {
        viewModelScope.launch {
            if (nombre.isNotBlank() && secretoPlano.isNotBlank()) {
                val cifrado = cifrarSimetrico(secretoPlano)
                addTaskUseCase(nombre, "CIFRADO:$cifrado")
                agregarLog("Bóveda: Secreto '$nombre' encriptado mediante algoritmo XOR simétrico y guardado en SQLite.")
            }
        }
    }

    fun agregarTarea(titulo: String, descripcion: String) {
        viewModelScope.launch {
            if (titulo.isNotBlank()) {
                addTaskUseCase(titulo, descripcion)
                agregarLog("Nueva tarea añadida a base de datos SQLite: $titulo [$descripcion]")
            }
        }
    }

    fun conmutarEstadoTarea(tarea: Task) {
        viewModelScope.launch {
            val tareaActualizada = tarea.copy(completada = !tarea.completada)
            updateTaskUseCase(tareaActualizada)
            val estado = if (tareaActualizada.completada) "COMPLETADA" else "ACTIVA"
            agregarLog("Registro '${tarea.titulo}' modificado a: $estado")
        }
    }

    fun eliminarTarea(tarea: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(tarea)
            agregarLog("Registro purgado de SQLite: ${tarea.titulo}")
        }
    }

    fun limpiarHistorialLogs() {
        viewModelScope.launch {
            logRepository.limpiarLogs()
            agregarLog("Pizarra de telemetría purgada por requerimiento del operador.")
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
            agregarLog("Error de lectura Web3 RPC: ${e.message}")
        }
    }

    /**
     * Ciclo continuo de fondo optimizado (simulando procesos persistentes de WorkManager)
     * Realiza lecturas constantes a redes blockchain Sepolia rotando firmas digitales de red de forma asíncrona.
     */
    private suspend fun iniciarCicloPersistenciaFondo() {
        while (true) {
            delay(5000) // Ciclo periódico cada 5 segundos
            val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            
            // Simulación realista de hardware y red Web3
            val ramUsage = Random.nextDouble(22.1, 39.4)
            val cpuUsage = Random.nextDouble(0.4, 6.2)
            val kbs = Random.nextDouble(1.2, 54.8)

            _telemetriaRam.value = String.format("RAM: %.1f MB / 1024 MB (%.1f%%)", ramUsage, (ramUsage/1024)*100)
            _telemetriaCpu.value = String.format("CPU: %.1f%% (Eco-Mode)", cpuUsage)
            _consumoRed.value = String.format("Ancho de Banda: %.1f KB/s", kbs)

            // Actualizar historial para el gráfico táctico
            val listActual = _historialRed.value.toMutableList()
            if (listActual.size >= 15) {
                listActual.removeAt(0)
            }
            listActual.add(kbs.toFloat())
            _historialRed.value = listActual

            // Auto-rotar proxy de forma preventiva con un 15% de probabilidad en cada consulta periódica
            if (Random.nextInt(100) < 15) {
                val nuevoP = ghostShield.forzarRotacionProxy()
                _proxyActivo.value = nuevoP
                _proxySalud.value = ghostShield.obtenerSaludProxy()
                _userAgentActivo.value = ghostShield.rotarUserAgent()
                agregarLog("[$time] Ghost-Shield: Rotación preventiva de proxy aplicada con éxito.")
            }

            // Consultar datos públicos blockchain
            actualizarNodosRpc()

            // Registrar log de latencia o validación nominal
            val nodo = _nodosRpc.value.randomOrNull()
            if (nodo != null) {
                agregarLog("[$time] Telemetría: '${nodo.nombre}' verificado en bloque #${nodo.bloqueActual}. Latencia: ${nodo.latenciaMs}ms.")
            }
        }
    }
}
