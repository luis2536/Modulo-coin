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
import androidx.compose.foundation.BorderStroke
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
import com.example.domain.model.RpcNode
import com.example.domain.model.Task
import com.example.ui.theme.*

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
fun SyntropyDeltaNexusApp(viewModel: MainViewModel = viewModel()) {
    val vistaActual by viewModel.vistaActual.collectAsState()
    val tareas by viewModel.tareas.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val nodosRpc by viewModel.nodosRpc.collectAsState()
    val ramMetric by viewModel.telemetriaRam.collectAsState()
    val cpuMetric by viewModel.telemetriaCpu.collectAsState()

    // Perfil de Operador local y mutable
    var nombreOperador by remember { mutableStateOf("María Delgado") }
    var rangoOperador by remember { mutableStateOf("Operadora de Enlace") }
    var mostrandoEditarPerfil by remember { mutableStateOf(false) }

    // Estado de la URL del Navegador Web3
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
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYNTROPY DELTA NEXUS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Obsidian
                ),
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .background(SlateGray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEXUS ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NeonGreen
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
            // Controles de Selección de Modo (Bento Grid Header)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .background(SlateGray, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.cambiarVista("DEV") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaActual == "DEV") NeonTeal else Color.Transparent,
                        contentColor = if (vistaActual == "DEV") Obsidian else TextSecondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("vista_dev_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DEV (Modo Arquitecto)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = { viewModel.cambiarVista("OPERATOR") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaActual == "OPERATOR") NeonTeal else Color.Transparent,
                        contentColor = if (vistaActual == "OPERATOR") Obsidian else TextSecondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("vista_operator_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OPERATOR (Modo María)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }

            // Separador con animación de gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, NeonTeal, Color.Transparent)
                        )
                    )
            )

            // Contenido Principal Animado
            AnimatedContent(
                targetState = vistaActual,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "cambio_vista"
            ) { targetVista ->
                if (targetVista == "DEV") {
                    DevModeLayout(
                        ramMetric = ramMetric,
                        cpuMetric = cpuMetric,
                        nodosRpc = nodosRpc,
                        logs = logs,
                        tareasCount = tareas.size,
                        onLimpiarLogs = { viewModel.limpiarHistorialLogs() }
                    )
                } else {
                    OperatorModeLayout(
                        tareas = tareas,
                        nombreOperador = nombreOperador,
                        rangoOperador = rangoOperador,
                        onAgregarTarea = { t, d -> viewModel.agregarTarea(t, d) },
                        onToggleTarea = { viewModel.conmutarEstadoTarea(it) },
                        onEliminarTarea = { viewModel.eliminarTarea(it) },
                        onEditarPerfil = { mostrandoEditarPerfil = true },
                        urlActual = urlActual,
                        onUrlCambiada = { urlActual = it },
                        cargandoWeb = cargandoWeb,
                        onCargandoWeb = { cargandoWeb = it }
                    )
                }
            }
        }
    }

    // Modal de Edición de Perfil de Operador
    if (mostrandoEditarPerfil) {
        AlertDialog(
            onDismissRequest = { mostrandoEditarPerfil = false },
            title = {
                Text(
                    "EDITAR OPERADOR DEL NEXUS",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NeonTeal
                )
            },
            text = {
                Column {
                    var inputNombre by remember { mutableStateOf(nombreOperador) }
                    var inputRango by remember { mutableStateOf(rangoOperador) }

                    OutlinedTextField(
                        value = inputNombre,
                        onValueChange = { inputNombre = it },
                        label = { Text("Nombre del Operador") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTeal,
                            focusedLabelColor = NeonTeal
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("perfil_nombre_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputRango,
                        onValueChange = { inputRango = it },
                        label = { Text("Rango u Oficio") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTeal,
                            focusedLabelColor = NeonTeal
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("perfil_rango_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { mostrandoEditarPerfil = false }) {
                            Text("Cancelar", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                nombreOperador = inputNombre
                                rangoOperador = inputRango
                                mostrandoEditarPerfil = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian)
                        ) {
                            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = CardBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ==========================================
// DISEÑO BENTO - MODO DEV (ARQUITECTO)
// ==========================================
@Composable
fun DevModeLayout(
    ramMetric: String,
    cpuMetric: String,
    nodosRpc: List<RpcNode>,
    logs: List<LogEntry>,
    tareasCount: Int,
    onLimpiarLogs: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Tarjeta Bento 1: Telemetría de Recursos (RAM / CPU)
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "TELEMETRÍA DE RECURSOS DEL SISTEMA",
                icono = Icons.Default.Dns,
                colorAcunado = NeonTeal
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Módulo de Memoria:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(ramMetric, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = NeonTeal)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NeonTeal,
                        trackColor = BorderColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Carga de Procesador:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(cpuMetric, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = NeonTeal)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.12f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NeonTeal,
                        trackColor = BorderColor
                    )
                }
            }
        }

        // Tarjeta Bento 2: Base de Datos SQLite Local
        item {
            BentoCard(
                titulo = "BASE DE DATOS SQLITE",
                icono = Icons.Default.Storage,
                colorAcunado = NeonGreen
            ) {
                Column {
                    Text("Base: syntropy_nexus_database.db", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tablas Locales:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("• tasks (tareas registradas: $tareasCount)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("• logs (historial total: ${logs.size})", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonGreen))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ESTADO: ENCRIPTADO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonGreen)
                    }
                }
            }
        }

        // Tarjeta Bento 3: Conexión RPC de Solo Lectura
        item {
            BentoCard(
                titulo = "NODOS RPC DE SOLO LECTURA",
                icono = Icons.Default.Refresh,
                colorAcunado = NeonAmber
            ) {
                Column {
                    Text("Conexiones de Redes:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    nodosRpc.take(3).forEach { nodo ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(nodo.nombre, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("${nodo.latenciaMs}ms", style = MaterialTheme.typography.bodySmall, color = if (nodo.latenciaMs < 150) NeonGreen else NeonAmber)
                        }
                    }
                }
            }
        }

        // Tarjeta Bento 4: Terminal de Logs en Tiempo Real (Autoscrollable)
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "TERMINAL DE LOGS OPERATIVOS",
                icono = Icons.Default.Terminal,
                colorAcunado = NeonTeal,
                accionExtra = {
                    IconButton(
                        onClick = onLimpiarLogs,
                        modifier = Modifier.size(24.dp).testTag("limpiar_logs_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Limpiar Logs", tint = NeonRed, modifier = Modifier.size(16.dp))
                    }
                }
            ) {
                val listState = rememberLazyListState()
                
                // Efecto de scroll automático al final cuando llega un nuevo log
                LaunchedEffect(logs.size) {
                    if (logs.isNotEmpty()) {
                        listState.animateScrollToItem(0)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        .background(DarkPurple)
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs) { log ->
                            Text(
                                text = ">> ${log.mensaje}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = NeonTeal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// DISEÑO BENTO - MODO OPERATOR (MARÍA)
// ==========================================
@Composable
fun OperatorModeLayout(
    tareas: List<Task>,
    nombreOperador: String,
    rangoOperador: String,
    onAgregarTarea: (String, String) -> Unit,
    onToggleTarea: (Task) -> Unit,
    onEliminarTarea: (Task) -> Unit,
    onEditarPerfil: () -> Unit,
    urlActual: String,
    onUrlCambiada: (String) -> Unit,
    cargandoWeb: Boolean,
    onCargandoWeb: (Boolean) -> Unit
) {
    var nuevaTareaTitulo by remember { mutableStateOf("") }
    var nuevaTareaDesc by remember { mutableStateOf("General") }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Tarjeta Bento 1: Perfil de Operador Actual
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "PERFIL DEL OPERADOR ACTIVO",
                icono = Icons.Default.Person,
                colorAcunado = NeonTeal,
                accionExtra = {
                    IconButton(
                        onClick = onEditarPerfil,
                        modifier = Modifier.size(24.dp).testTag("editar_perfil_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Editar Perfil", tint = NeonTeal, modifier = Modifier.size(16.dp))
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BorderColor)
                            .border(1.dp, NeonTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(nombreOperador, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(rangoOperador, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }

        // Tarjeta Bento 2: CRUD Cola de Tareas / To-Do
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "COLA DE TAREAS Y AUTOMATIZACIONES",
                icono = Icons.Default.Terminal,
                colorAcunado = NeonTeal
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nuevaTareaTitulo,
                            onValueChange = { nuevaTareaTitulo = it },
                            label = { Text("Nueva tarea o Quest...", style = MaterialTheme.typography.labelSmall) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTeal,
                                focusedLabelColor = NeonTeal
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tarea_titulo_input")
                        )
                        Button(
                            onClick = {
                                if (nuevaTareaTitulo.isNotBlank()) {
                                    onAgregarTarea(nuevaTareaTitulo, nuevaTareaDesc)
                                    nuevaTareaTitulo = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .testTag("agregar_tarea_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector de categoría rápido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("General", "DePIN", "Quest", "Testnet").forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (nuevaTareaDesc == cat) NeonTeal else BorderColor)
                                    .clickable { nuevaTareaDesc = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (nuevaTareaDesc == cat) Obsidian else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (tareas.isEmpty()) {
                        Text(
                            text = "No hay tareas activas en cola.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            tareas.take(5).forEach { tarea ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                        .background(SlateGray)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = tarea.completada,
                                        onCheckedChange = { onToggleTarea(tarea) },
                                        colors = CheckboxDefaults.colors(checkedColor = NeonTeal)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tarea.titulo,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                textDecoration = if (tarea.completada) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                            ),
                                            color = if (tarea.completada) TextSecondary else TextPrimary
                                        )
                                        Text(
                                            text = "Categoría: ${tarea.descripcion}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    IconButton(onClick = { onEliminarTarea(tarea) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = NeonRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tarjeta Bento 3: Navegador Web3 integrado (WebView)
        item(span = { GridItemSpan(2) }) {
            BentoCard(
                titulo = "EXPLORADOR DE REDES WEB3 PORTABLE",
                icono = Icons.Default.Web,
                colorAcunado = NeonTeal
            ) {
                var urlInput by remember { mutableStateOf(urlActual) }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("URL de Web3 Explorer", style = MaterialTheme.typography.labelSmall) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTeal,
                                focusedLabelColor = NeonTeal
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                onUrlCambiada(urlInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Obsidian),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("IR", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Accesos rápidos de Web3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("Etherscan", "https://sepolia.etherscan.io"),
                            Pair("Linea Scan", "https://sepolia.lineascan.build"),
                            Pair("Syntropy Explorer", "https://explorer.syntropynet.com")
                        ).forEach { (nom, targetUrl) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BorderColor)
                                    .clickable {
                                        urlInput = targetUrl
                                        onUrlCambiada(targetUrl)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = nom,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contenedor del WebView
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    ) {
                        AndroidWebViewComponent(
                            url = urlActual,
                            onCargandoWeb = onCargandoWeb
                        )

                        if (cargandoWeb) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x990A0C10)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = NeonTeal)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Componente para renderizar un WebView estándar de Android en Jetpack Compose
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

// ==========================================
// COMPONENTE BENTO CARD REUTILIZABLE
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
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icono, contentDescription = null, tint = colorAcunado, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = colorAcunado,
                        fontSize = 12.sp
                    )
                }
                accionExtra?.invoke()
            }
            Spacer(modifier = Modifier.height(12.dp))
            contenido()
        }
    }
}
