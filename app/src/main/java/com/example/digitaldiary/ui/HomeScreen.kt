package com.example.digitaldiary.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digitaldiary.data.DiaryEntry
import com.example.digitaldiary.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pamiętnik Cyfrowy") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(entries) { entry ->
                DiaryCard(entry)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun DiaryCard(entry: DiaryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
            Text(text = entry.content, style = MaterialTheme.typography.bodyMedium)
            Text(text = "📍 ${entry.locationName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
