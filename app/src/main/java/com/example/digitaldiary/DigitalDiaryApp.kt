package com.example.digitaldiary

import com.example.digitaldiary.ui.CreateEntryScreen
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.digitaldiary.ui.DetailEntryScreen
import com.example.digitaldiary.ui.HomeScreen
import com.example.digitaldiary.ui.MapScreen
import com.example.digitaldiary.viewmodel.HomeViewModel
import com.example.digitaldiary.viewmodel.checkAndRequestNotificationPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalDiaryApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var selectedEntryId by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        when (currentScreen) {
            Screen.Home -> {
                val viewModel = remember { HomeViewModel() }
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCreate = { currentScreen = Screen.Create },
                    onNavigateToMap = {currentScreen = Screen.Map},
                    onNavigateToEdit = { entryId ->
                        selectedEntryId = entryId
                        currentScreen = Screen.Entry
                    }
                )
            }
            Screen.Create -> {
                CreateEntryScreen(
                    onNavigateBack = { currentScreen = Screen.Home }
                )
            }
            Screen.Map -> {
                val viewModel = remember { HomeViewModel() }
                MapScreen(
                    viewModel = viewModel,
                    onNavigateBack = {currentScreen = Screen.Home}
                )
            }
            Screen.Entry -> {
                selectedEntryId?.let {entryId ->
                    DetailEntryScreen(
                        entryId = entryId,
                        onNavigateBack = {currentScreen = Screen.Home}
                    )
                }
            }
        }
    }
}
