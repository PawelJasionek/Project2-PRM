@file:Suppress("DEPRECATION")

package com.example.digitaldiary.ui

import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.digitaldiary.data.DiaryEntry
import com.example.digitaldiary.data.FirebaseRepository
import androidx.core.net.toUri
import checkAndRequestAudioPermission
import android.util.Log
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEntryScreen(
    entryId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { FirebaseRepository() }

    fun playAudio(context: android.content.Context, audioUrl: String) {
        try {
            MediaPlayer().apply {
                setDataSource(context, audioUrl.toUri())
                prepare()
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed playing recording", Toast.LENGTH_SHORT).show()
            Log.d("Blad", "$e")
        }
    }



    var entry by remember { mutableStateOf<DiaryEntry?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf(entry?.imageUrl) }


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri?.toString()
        }
    )

    //audio

    var isRecording by remember { mutableStateOf(false) }
    var selectedAudioUri by remember { mutableStateOf(entry?.audioUrl) }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }

    fun startRecording() {
        val fileName = "${context.externalCacheDir?.absolutePath}/recording_${System.currentTimeMillis()}.3gp"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
        selectedAudioUri = fileName
        isRecording = true
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
    }

    LaunchedEffect(entryId) {
        repository.getEntryById(entryId) { result ->
            entry = result
            title = result?.title.orEmpty()
            content = result?.content.orEmpty()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            TopAppBar(title = { Text("Edit entry") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                entry?.let {
                    val updated = it.copy(
                        title = title,
                        content = content,
                        imageUrl = selectedImageUri ?: it.imageUrl,
                        audioUrl = selectedAudioUri ?: it.audioUrl,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.updateEntry(updated)
                    onNavigateBack()
                }
            }) {
                Text("💾")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text("Location: ${entry?.locationName ?: "Brak"}")

            AsyncImage(
                model = selectedImageUri ?: entry?.imageUrl,
                contentDescription = "Photo",
                modifier = Modifier.size(200.dp)
            )

            Button(onClick = {
                pickImageLauncher.launch("image/*")
            }) {
                Text("Change photo")
            }
            val activity = context as? Activity
            if (!isRecording) {
                Button(onClick = {
                    if (activity != null && checkAndRequestAudioPermission(activity)) {
                        startRecording()
                    }
                     }) {
                    Text("🎙 Begin a new recording")
                }
            } else {
                Button(onClick = { stopRecording() }) {
                    Text("⏹ Stop recording")
                }
            }

            val currentEntry = entry

            currentEntry?.audioUrl?.let { url ->
                Button(onClick = { playAudio(context, url) }) {
                    Text("🎧 Listen to recording")
                }
            }
        }
    }


}
