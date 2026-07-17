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

    // Datos de Seguridad Ghost-Shield
    val proxyActivo by viewModel.proxyActivo.collectAsState()
    val proxySalud by viewModel.proxySalud.collectAsState()
    val userAgentActivo by viewModel.userAgentActivo.collectAsState()

    // Sesión Operativa Multi-Perfil
    val sesionActiva by viewModel.sesionActiva.collectAsState()

    // Estado del Navegador Web3
    var urlActual by remember { mutableStateOf("https://sepolia.etherscan.io") }
    var cargandoWeb by remember { mutableStateOf(false) }

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
                        proxyActivo = proxyActivo,
                        proxySalud = proxySalud,
                        userAgentActivo = userAgentActivo,
                        nodosRpc = nodosRpc,
                        logs = logs,
                        tareasCount = tareas.size,
                        onLimpiarLogs = { viewModel.limpiarHistorialLogs() },
                        onRotarProxy = { viewModel.rotarProxyManualmente() }
                    )
                } else {
                    OperatorModeLayout(
                        tareas = tareas,
                        sesionActiva = sesionActiva,
                        onAgregarTarea = { t, d -> viewModel.agregarTarea(t, d) },
                        onToggleTarea = { viewModel.conmutarEstadoTarea(it) },
                        onEliminarTarea = { viewModel.eliminarTarea(it) },
                        onCambiarRol = { viewModel.cambiarSesion(it) },
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
    proxyActivo: String,
    proxySalud: String,
    userAgentActivo: String,
    nodosRpc: List<RpcNode>,
    logs: List<LogEntry>,
    tareasCount: Int,
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
            BentoCard(titulo = "TELEMETRÍA DE RECURSOS", icono = Icons.Default.Dns, colorAcunado = NeonTeal) {
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

        // Módulo Ghost-Shield
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "PROTECCIÓN GHOST-SHIELD",
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
                        Text("Proxy SOCKS5:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(proxyActivo, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estado de Canal:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(proxySalud, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = if (proxySalud == "CRÍTICO") NeonRed else NeonGreen)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(BorderColor, RoundedCornerShape(4.dp)).padding(6.dp)) {
                        Column {
                            Text("USER-AGENT ACTIVO:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp), color = NeonTeal)
                            Text(userAgentActivo, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp), color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Bloques Blockchain RPC
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "NODOS BLOCKCHAIN ACTIVOS", icono = Icons.Default.Language, colorAcunado = NeonAmber) {
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

        // Terminal de Logs
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "REGISTROS DEL SISTEMA",
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
    onAgregarTarea: (String, String) -> Unit,
    onToggleTarea: (Task) -> Unit,
    onEliminarTarea: (Task) -> Unit,
    onCambiarRol: (RolOperativo) -> Unit,
    urlActual: String,
    onUrlCambiada: (String) -> Unit,
    cargandoWeb: Boolean,
    onCargandoWeb: (Boolean) -> Unit
) {
    var nuevaTareaTitulo by remember { mutableStateOf("") }
    var nuevaTareaDesc by remember { mutableStateOf("General") }

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
                titulo = "PERFIL OPERADOR NEXUS",
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
                        Text("Acceso: ${sesionActiva.rol.name} | Clave: ${sesionActiva.llaveCifrada}", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                    }
                }
            }
        }

        // Cola de Tareas
        item(span = { GridItemSpan(2) }) {
            BentoCard(titulo = "COLA DE OBJETIVOS (QUESTS)", icono = Icons.AutoMirrored.Filled.FormatListBulleted, colorAcunado = NeonTeal) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = nuevaTareaTitulo,
                            onValueChange = { nuevaTareaTitulo = it },
                            label = { Text("Agregar tarea en SQLite...", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp) },
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
                    if (tareas.isEmpty()) {
                        Text(text = "No hay objetivos registrados.", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tareas.take(4).forEach { tarea ->
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
