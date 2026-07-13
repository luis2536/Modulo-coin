package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme

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

@Composable
fun SyntropyDeltaNexusApp(viewModel: MainViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState(emptyList())
    val logs by viewModel.logs.collectAsState()
    val metrics by viewModel.metrics.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text("Syntropy Delta Nexus", style = MaterialTheme.typography.headlineMedium)
            }
            item {
                BentoCard("Telemetry") { Text(metrics, style = MaterialTheme.typography.bodySmall) }
            }
            item {
                BentoCard("Status") { Text("Node.js Core: Active", style = MaterialTheme.typography.bodySmall) }
            }
            item(span = { GridItemSpan(2) }) {
                BentoCard("Task Queue") {
                    tasks.forEach { task -> Text("• ${task.title}") }
                }
            }
            item(span = { GridItemSpan(2) }) {
                BentoCard("Live Logs") {
                    logs.forEach { log -> Text(log, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

@Composable
fun BentoCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
