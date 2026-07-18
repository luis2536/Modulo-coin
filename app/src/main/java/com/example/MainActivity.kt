package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.LogEntry
import com.example.domain.model.RolOperativo
import com.example.domain.model.RpcNode
import com.example.domain.model.Task
import com.example.ui.theme.*

import org.koin.androidx.compose.koinViewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.Canvas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SyntropyDeltaNexusApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyntropyDeltaNexusApp(viewModel: MainViewModel = koinViewModel()) {
    val vistaActual by viewModel.vistaActual.collectAsState()
    val tareas by viewModel.tareas.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val nodosRpc by viewModel.nodosRpc.collectAsState()
    val ramMetric by viewModel.telemetriaRam.collectAsState()
    val cpuMetric by viewModel.telemetriaCpu.collectAsState()
    val redMetric by viewModel.consumoRed.collectAsState()
    val historialRed by viewModel.historialRed.collectAsState()

    // Datos de Seguridad Ghost-Shield
    val proxyActivo by viewModel.proxyActivo.collectAsState()
    val proxySalud by viewModel.proxySalud.collectAsState()
    val userAgentActivo by viewModel.userAgentActivo.collectAsState()

    // Sesión Operativa Multi-Perfil
    val sesionActiva by viewModel.sesionActiva.collectAsState()

    // Estado del Navegador Web3
    var urlActual by remember { mutableStateOf("https://sepolia.etherscan.io") }
    var cargandoWeb by remember { mutableStateOf(false) }

    val escaneandoRadar by viewModel.escaneandoRadar.collectAsState()
    val amenazasDetectadas by viewModel.amenazasDetectadas.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = NeonTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYNTROPY DELTA NEXUS OMNI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            ),
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Obsidian
                ),
                actions = {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .background(SlateGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonGreen.copy(alpha = alpha)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GHOST: ACTIVO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NeonGreen.copy(alpha = alpha)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Obsidian)
                .padding(innerPadding)
        ) {
            // Selector de Vista (DEV / OPERATOR)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .background(SlateGray, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.cambiarVista("DEV") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaActual == "DEV") NeonTeal else Color.Transparent,
                        contentColor = if (vistaActual == "DEV") Obsidian else TextSecondary
                    ),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("vista_dev_btn"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DEV (Monitoreo)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { viewModel.cambiarVista("OPERATOR") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaActual == "OPERATOR") NeonTeal else Color.Transparent,
                        contentColor = if (vistaActual == "OPERATOR") Obsidian else TextSecondary
                    ),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("vista_operator_btn"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OPERATOR (Consola)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, NeonTeal, Color.Transparent)))
            )

            AnimatedContent(
                targetState = vistaActual,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                modifier = Modifier.fillMaxWidth().weight(1f),
                label = "foco_vista"
            ) { targetVista ->
                if (targetVista == "DEV") {
                    DevModeLayout(
                        ramMetric = ramMetric,
                        cpuMetric = cpuMetric,
                        redMetric = redMetric,
                        historialRed = historialRed,
                        proxyActivo = proxyActivo,
                        proxySalud = proxySalud,
                        userAgentActivo = userAgentActivo,
                        nodosRpc = nodosRpc,
                        logs = logs,
                        tareasCount = tareas.size,
                        escaneandoRadar = escaneandoRadar,
                        amenazasDetectadas = amenazasDetectadas,
                        onEjecutarEscaneo = { viewModel.ejecutarEscaneoRadar() },
                        onLimpiarLogs = { viewModel.limpiarHistorialLogs() },
                        onRotarProxy = { viewModel.rotarProxyManualmente() }
                    )
                } else {
                    val analisis by viewModel.analisisMilitar.collectAsState()
                    OperatorModeLayout(
                        tareas = tareas,
                        sesionActiva = sesionActiva,
                        analisisMilitar = analisis,
                        onAgregarTarea = { t, d -> viewModel.agregarTarea(t, d) },
                        onToggleTarea = { viewModel.conmutarEstadoTarea(it) },
                        onEliminarTarea = { viewModel.eliminarTarea(it) },
                        onCambiarRol = { viewModel.cambiarSesion(it) },
                        onEjecutarAnalisis = { viewModel.ejecutarAnalisisInteligencia(it) },
                        onGuardarSecreto = { nom, sec -> viewModel.guardarSecretoEnBoveda(nom, sec) },
                        cifrarSimetrico = { viewModel.cifrarSimetrico(it) },
                        urlActual = urlActual,
                        onUrlCambiada = { urlActual = it },
                        cargandoWeb = cargandoWeb,
                        onCargandoWeb = { cargandoWeb = it }
                    )
                }
            }
        }
    }
}

// ==========================================
// VISTA: MODO DEV (TELEMETRÍA DE ARQUITECTO)
// ==========================================
@Composable
fun DevModeLayout(
    ramMetric: String,
    cpuMetric: String,
    redMetric: String,
    historialRed: List<Float>,
    proxyActivo: String,
    proxySalud: String,
    userAgentActivo: String,
    nodosRpc: List<RpcNode>,
    logs: List<LogEntry>,
    tareasCount: Int,
    escaneandoRadar: Boolean,
    amenazasDetectadas: List<String>,
    onEjecutarEscaneo: () -> Unit,
    onLimpiarLogs: () -> Unit,
    onRotarProxy: () -> Unit
) {
    var logsFiltro by remember { mutableStateOf("TODOS") }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Telemetría de Recursos
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "TELEMETRÍA DE RECURSOS DEL SISTEMA", icono = Icons.Default.Dns, colorAcunado = NeonTeal) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Memoria RAM:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(ramMetric, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = NeonTeal)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    CyberPulseBar(progress = 0.35f, color = NeonTeal, modifier = Modifier.fillMaxWidth().height(6.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Procesador CPU:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(cpuMetric, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = NeonTeal)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    CyberPulseBar(progress = 0.12f, color = NeonTeal, modifier = Modifier.fillMaxWidth().height(6.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ancho de Banda:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(redMetric, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = NeonGreen)
                    }
                }
            }
        }

        // Gráfico de Tráfico Táctico (Análisis 3D)
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "ANÁLISIS GRÁFICO DE TRÁFICO TÁCTICO", icono = Icons.Default.Timeline, colorAcunado = NeonTeal) {
                Column {
                    Text(
                        "FLUJO DE DATOS DE RED EN TIEMPO REAL (DePIN)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TacticalAreaChart(
                        dataPoints = historialRed,
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        lineColor = NeonTeal
                    )
                }
            }
        }

        // Módulo Ghost-Shield
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "PROTECCIÓN PERIMETRAL GHOST-SHIELD",
                icono = Icons.Default.Shield,
                colorAcunado = NeonGreen,
                accionExtra = {
                    Button(onClick = onRotarProxy, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Obsidian), contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(24.dp)) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ROTAR PROXY", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Proxy SOCKS5 de Red:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(proxyActivo, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Canal Seguro de Datos:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(proxySalud, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = if (proxySalud == "CRÍTICO") NeonRed else NeonGreen)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(BorderColor, RoundedCornerShape(4.dp)).padding(6.dp)) {
                        Column {
                            Text("USER-AGENT TÁCTICO ACTIVO:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp), color = NeonTeal)
                            Text(userAgentActivo, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp), color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Bloques Blockchain RPC
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "NODOS BLOCKCHAIN ACTIVOS (DePIN)", icono = Icons.Default.Language, colorAcunado = NeonAmber) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (nodosRpc.isEmpty()) {
                        CircularProgressIndicator(color = NeonAmber, modifier = Modifier.size(20.dp))
                    } else {
                        nodosRpc.take(3).forEach { nodo ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (nodo.latenciaMs < 180) NeonGreen else NeonAmber))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(nodo.nombre, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                                Text("Bloque #${nodo.bloqueActual} [${nodo.latenciaMs}ms]", style = MaterialTheme.typography.bodySmall, color = if (nodo.latenciaMs < 180) NeonGreen else NeonAmber)
                            }
                        }
                    }
                }
            }
        }

        // NUEVO MÓDULO: Escáner de Amenazas de Red (Radar Táctico)
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "ESCÁNER TÁCTICO DE AMENAZAS (RADAR DE RED)",
                icono = Icons.Default.Sensors,
                colorAcunado = NeonTeal,
                accionExtra = {
                    Button(
                        onClick = onEjecutarEscaneo,
                        enabled = !escaneandoRadar,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (escaneandoRadar) "ESCANEANDO..." else "BARRIDO RADAR",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            ) {
                Column {
                    if (escaneandoRadar) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                            CircularProgressIndicator(color = NeonTeal, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Escaneando firmas de bloques y topologías de red en tiempo real...",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = NeonTeal
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            amenazasDetectadas.forEach { amenaza ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (amenaza.contains("sospechosa") || amenaza.contains("intrusión")) NeonRed else NeonGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = amenaza,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Terminal de Logs
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "REGISTROS DEL SISTEMA (TELEMETRÍA EN VIVO)",
                icono = Icons.Default.Terminal,
                colorAcunado = NeonTeal,
                accionExtra = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf("TODOS", "SISTEMA", "WEB3").forEach { f ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (logsFiltro == f) NeonTeal else BorderColor).clickable { logsFiltro = f }.padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Text(f, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = if (logsFiltro == f) Obsidian else TextSecondary, fontWeight = FontWeight.Bold)
                            }
                        }
                        IconButton(onClick = onLimpiarLogs, modifier = Modifier.size(18.dp).testTag("limpiar_logs_btn")) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = NeonRed, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            ) {
                val listState = rememberLazyListState()
                val logsFiltrados = remember(logs, logsFiltro) {
                    when (logsFiltro) {
                        "SISTEMA" -> logs.filter { "Monitoreo" !in it.mensaje && "Telemetría" !in it.mensaje }
                        "WEB3" -> logs.filter { "Monitoreo" in it.mensaje || "Telemetría" in it.mensaje }
                        else -> logs
                    }
                }

                LaunchedEffect(logsFiltrados.size) {
                    if (logsFiltrados.isNotEmpty()) {
                        listState.animateScrollToItem(0)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(120.dp).border(1.dp, BorderColor, RoundedCornerShape(6.dp)).background(DarkPurple).padding(6.dp)) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(logsFiltrados) { log ->
                            Text(text = ">> ${log.mensaje}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = if ("Error" in log.mensaje) NeonRed else NeonTeal)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// VISTA: MODO OPERATOR (CONSOLA DE CONTROL)
// ==========================================
@Composable
fun OperatorModeLayout(
    tareas: List<Task>,
    sesionActiva: com.example.domain.model.Session,
    analisisMilitar: com.example.domain.model.ResultWrapper<String>,
    onAgregarTarea: (String, String) -> Unit,
    onToggleTarea: (Task) -> Unit,
    onEliminarTarea: (Task) -> Unit,
    onCambiarRol: (RolOperativo) -> Unit,
    onEjecutarAnalisis: (String) -> Unit,
    onGuardarSecreto: (String, String) -> Unit,
    cifrarSimetrico: (String) -> String,
    urlActual: String,
    onUrlCambiada: (String) -> Unit,
    cargandoWeb: Boolean,
    onCargandoWeb: (Boolean) -> Unit
) {
    var nuevaTareaTitulo by remember { mutableStateOf("") }
    var nuevaTareaDesc by remember { mutableStateOf("General") }

    // Campos para agregar credenciales en la bóveda segura
    var nuevoSecretoNombre by remember { mutableStateOf("") }
    var nuevoSecretoValor by remember { mutableStateOf("") }

    // Filtros de tareas y secretos en base de datos SQLite (Room)
    val tareasGenerales = remember(tareas) { tareas.filter { !it.descripcion.startsWith("CIFRADO:") } }
    val tareasSecretos = remember(tareas) { tareas.filter { it.descripcion.startsWith("CIFRADO:") } }

    var tipoAnalisisSeleccionado by remember { mutableStateOf("Escaneo Estándar") }
    var secretoReveladoId by remember { mutableStateOf<Int?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Perfil Activo
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "PERFIL DE OPERADOR NEXUS",
                icono = Icons.Default.Group,
                colorAcunado = NeonTeal,
                accionExtra = {
                    Row(modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(6.dp)).background(DarkPurple, RoundedCornerShape(6.dp)).padding(2.dp)) {
                        Text(text = "MARÍA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = if (sesionActiva.rol == RolOperativo.OPERADORA) NeonTeal else TextSecondary, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (sesionActiva.rol == RolOperativo.OPERADORA) BorderColor else Color.Transparent).clickable { onCambiarRol(RolOperativo.OPERADORA) }.padding(horizontal = 6.dp, vertical = 2.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "ADMIN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = if (sesionActiva.rol == RolOperativo.ADMINISTRADOR) NeonTeal else TextSecondary, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (sesionActiva.rol == RolOperativo.ADMINISTRADOR) BorderColor else Color.Transparent).clickable { onCambiarRol(RolOperativo.ADMINISTRADOR) }.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(BorderColor).border(1.dp, NeonTeal, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(sesionActiva.nombreUsuario, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Acceso: ${sesionActiva.rol.name} | Clave de Sesión: ${sesionActiva.llaveCifrada}", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                    }
                }
            }
        }

        // Inteligencia Artificial (Gemini)
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "INTELIGENCIA TÁCTICA OMNI (GEMINI AI)",
                icono = Icons.Default.Android,
                colorAcunado = NeonAmber,
                accionExtra = {
                    Button(
                        onClick = { onEjecutarAnalisis(tipoAnalisisSeleccionado) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = Obsidian),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EJECUTAR ANÁLISIS", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Column {
                    // Selector de tipo de análisis táctico
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Escaneo Estándar", "Cripto-Auditoría", "Ruta DePIN").forEach { tipo ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (tipoAnalisisSeleccionado == tipo) NeonAmber else BorderColor)
                                    .clickable { tipoAnalisisSeleccionado = tipo }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tipo,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = if (tipoAnalisisSeleccionado == tipo) Obsidian else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().background(DarkPurple, RoundedCornerShape(6.dp)).border(1.dp, BorderColor, RoundedCornerShape(6.dp)).padding(8.dp)) {
                        when (analisisMilitar) {
                            is com.example.domain.model.ResultWrapper.Loading -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(color = NeonAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Procesando inteligencia AI táctica militar...", style = MaterialTheme.typography.bodySmall, color = NeonAmber)
                                }
                            }
                            is com.example.domain.model.ResultWrapper.Success -> {
                                Text(analisisMilitar.data, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = TextPrimary)
                            }
                            is com.example.domain.model.ResultWrapper.Error -> {
                                Text(analisisMilitar.mensaje, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = NeonRed)
                            }
                        }
                    }
                }
            }
        }

        // BÓVEDA CRIPTOGRÁFICA DE SEGURIDAD (XOR SECURE ENGINE)
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "BÓVEDA CRIPTOGRÁFICA DE CREDENCIALES", icono = Icons.Default.Lock, colorAcunado = NeonTeal) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = nuevoSecretoNombre,
                            onValueChange = { nuevoSecretoNombre = it },
                            label = { Text("Nombre (Ej: Wallet)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, focusedLabelColor = NeonTeal),
                            modifier = Modifier.weight(1f).height(46.dp)
                        )
                        OutlinedTextField(
                            value = nuevoSecretoValor,
                            onValueChange = { nuevoSecretoValor = it },
                            label = { Text("Secreto / Clave", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, focusedLabelColor = NeonTeal),
                            modifier = Modifier.weight(1f).height(46.dp)
                        )
                        Button(
                            onClick = {
                                if (nuevoSecretoNombre.isNotBlank() && nuevoSecretoValor.isNotBlank()) {
                                    onGuardarSecreto(nuevoSecretoNombre, nuevoSecretoValor)
                                    nuevoSecretoNombre = ""
                                    nuevoSecretoValor = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (tareasSecretos.isEmpty()) {
                        Text(
                            text = "No hay claves guardadas en la bóveda militar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tareasSecretos.forEach { secreto ->
                                val cifradoPayload = secreto.descripcion.removePrefix("CIFRADO:")
                                val estaRevelado = secretoReveladoId == secreto.id
                                val textoAMostrar = if (estaRevelado) cifrarSimetrico(cifradoPayload) else "••••••••••••"

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                                        .background(SlateGray)
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = secreto.titulo,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Clave: $textoAMostrar",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = if (estaRevelado) NeonGreen else TextSecondary
                                            ),
                                            fontSize = 9.sp
                                        )
                                    }
                                    
                                    // Botón para revelar / ocultar secreto
                                    IconButton(
                                        onClick = {
                                            secretoReveladoId = if (estaRevelado) null else secreto.id
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (estaRevelado) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = NeonTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEliminarTarea(secreto) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = NeonRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cola de Tareas (General Quests)
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "COLA DE OBJETIVOS EN COLA (SQLITE)", icono = Icons.AutoMirrored.Filled.FormatListBulleted, colorAcunado = NeonTeal) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = nuevaTareaTitulo,
                            onValueChange = { nuevaTareaTitulo = it },
                            label = { Text("Agregar objetivo en base de datos SQLite...", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, focusedLabelColor = NeonTeal),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("tarea_titulo_input")
                        )
                        Button(onClick = { if (nuevaTareaTitulo.isNotBlank()) { onAgregarTarea(nuevaTareaTitulo, nuevaTareaDesc); nuevaTareaTitulo = "" } }, colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(48.dp).testTag("agregar_tarea_btn")) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("General", "DePIN", "Quest", "Testnet").forEach { cat ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (nuevaTareaDesc == cat) NeonTeal else BorderColor).clickable { nuevaTareaDesc = cat }.padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text(text = cat, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = if (nuevaTareaDesc == cat) Obsidian else TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (tareasGenerales.isEmpty()) {
                        Text(text = "No hay objetivos nominales activos.", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tareasGenerales.take(4).forEach { tarea ->
                                Row(modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(6.dp)).background(SlateGray).padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = tarea.completada, onCheckedChange = { onToggleTarea(tarea) }, colors = CheckboxDefaults.colors(checkedColor = NeonTeal), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = tarea.titulo, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, textDecoration = if (tarea.completada) androidx.compose.ui.text.style.TextDecoration.LineThrough else null), color = if (tarea.completada) TextSecondary else TextPrimary)
                                        Text(text = "Categoría: ${tarea.descripcion}", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextSecondary)
                                    }
                                    IconButton(onClick = { onEliminarTarea(tarea) }, modifier = Modifier.size(28.dp)) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = NeonRed, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Navegador Web3
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "EXPLORADOR DE REDES WEB3", icono = Icons.Default.Web, colorAcunado = NeonTeal) {
                var urlInput by remember { mutableStateOf(urlActual) }
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("URL de Testnet", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonTeal, focusedLabelColor = NeonTeal),
                            modifier = Modifier.weight(1f).height(48.dp)
                        )
                        Button(onClick = { onUrlCambiada(urlInput) }, colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(48.dp)) {
                            Text("IR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(Pair("Etherscan", "https://sepolia.etherscan.io"), Pair("Linea", "https://sepolia.lineascan.build"), Pair("Syntropy", "https://explorer.syntropynet.com")).forEach { (n, u) ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(BorderColor).clickable { urlInput = u; onUrlCambiada(u) }.padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text(text = n, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, BorderColor, RoundedCornerShape(6.dp)).clip(RoundedCornerShape(6.dp)).background(Color.White)) {
                        AndroidWebViewComponent(url = urlActual, onCargandoWeb = onCargandoWeb)
                        if (cargandoWeb) {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0x990A0C10)), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = NeonTeal, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES TÁCTICOS REUTILIZABLES
// ==========================================
@Composable
fun BentoCard(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorAcunado: Color,
    accionExtra: @Composable (() -> Unit)? = null,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icono, contentDescription = null, tint = colorAcunado, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = titulo, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontFamily = FontFamily.Monospace), color = colorAcunado, fontSize = 10.sp)
                }
                accionExtra?.invoke()
            }
            Spacer(modifier = Modifier.height(8.dp))
            contenido()
        }
    }
}

@Composable
fun CyberPulseBar(progress: Float, color: Color, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(8.dp)) {
        val width = size.width
        val height = size.height
        val progressWidth = width * progress

        // Track (Fondo)
        drawRoundRect(
            color = BorderColor,
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(height / 2)
        )

        // Progress (Brillante)
        if (progressWidth > 0) {
            drawRoundRect(
                color = color.copy(alpha = alpha),
                size = Size(progressWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(height / 2)
            )
        }
        
        // Puntos de pulso
        if (progressWidth > 4.dp.toPx()) {
            drawCircle(
                color = Color.White,
                radius = height / 3,
                center = Offset(progressWidth - height / 2, height / 2)
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AndroidWebViewComponent(url: String, onCargandoWeb: (Boolean) -> Unit) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onCargandoWeb(true)
                    }
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onCargandoWeb(false)
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun TacticalAreaChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonTeal,
    gridColor: Color = BorderColor.copy(alpha = 0.2f)
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Dibujar rejilla táctica (Grid de fondo)
        val gridLinesX = 6
        for (i in 0..gridLinesX) {
            val x = width * i / gridLinesX
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
        }
        val gridLinesY = 4
        for (i in 0..gridLinesY) {
            val y = height * i / gridLinesY
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        if (dataPoints.size < 2) return@Canvas

        val maxVal = dataPoints.maxOrNull()?.coerceAtLeast(10f) ?: 10f
        val minVal = 0f
        val range = maxVal - minVal

        val points = dataPoints.mapIndexed { index, value ->
            val x = width * index / (dataPoints.size - 1)
            val y = height - ((value - minVal) / range) * height
            Offset(x, y)
        }

        // Sombreado bajo la curva (Gradiente 3D)
        val fillPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(points.first().x, height)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                startY = points.map { it.y }.minOrNull() ?: 0f,
                endY = height
            )
        )

        // Línea principal brillante
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Punto de pulso del último dato registrado
        val lastPoint = points.last()
        drawCircle(
            color = Color.White,
            radius = 4.dp.toPx(),
            center = lastPoint
        )
        drawCircle(
            color = lineColor,
            radius = 7.dp.toPx(),
            center = lastPoint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
        )
    }
}
