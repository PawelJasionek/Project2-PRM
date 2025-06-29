package com.example.digitaldiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.digitaldiary.viewmodel.HomeViewModel
import com.google.maps.android.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.room.util.copy
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@ExperimentalMaterial3Api
@Composable
fun MapScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            TopAppBar(
                title = { Text("Mapa wpisów") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { padding ->

        val startLocation = entries.firstOrNull { it.latitude != null && it.longitude != null }
            ?.let { LatLng(it.latitude!!, it.longitude!!) }
            ?: LatLng(47.3768983, 8.5417)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(startLocation, 10f)
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            entries.forEach { entry ->
                Marker(
                    state = MarkerState(position = LatLng(entry.latitude, entry.longitude)),
                    title = entry.title,
                    snippet = entry.locationName
                )
            }
        }
    }
}
