package com.example.digitaldiary.ui

import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import checkAndRequestLocationPermission
import com.example.digitaldiary.viewmodel.GeofenceManager
import com.example.digitaldiary.ui.components.AddEntryButton
import com.example.digitaldiary.viewmodel.HomeViewModel
import logCurrentLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val context = LocalContext.current
    val entries by viewModel.entries.collectAsState()
    val activity = context as? Activity

    LaunchedEffect(entries) {

        if (activity != null && checkAndRequestLocationPermission(activity)) {

            logCurrentLocation(context)

            val geofenceManager = GeofenceManager(context)
            geofenceManager.registerGeofences(entries)
        }

    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            TopAppBar(title = { Text("Digital Diary") },
                actions = {
                    IconButton(onClick = onNavigateToMap) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Map")
                    }
                })
        },
        floatingActionButton = {
            AddEntryButton(onClick = onNavigateToCreate)

        },

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(entries) { entry ->
                DiaryCard(entry = entry, onEditClick = {onNavigateToEdit(entry.id)})
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
