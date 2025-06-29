@file:Suppress("DEPRECATION")

package com.example.digitaldiary.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import android.view.WindowInsets
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import checkAndRequestAudioPermission
import coil3.compose.AsyncImage
import com.example.digitaldiary.data.DiaryEntry
import com.example.digitaldiary.data.FirebaseRepository
import com.example.digitaldiary.data.LocationUtils
import com.example.digitaldiary.viewmodel.drawTextOnBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun CreateEntryScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    //audio
    var recorder = remember { MediaRecorder() }
    var isRecording by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    val audioFile = File(context.cacheDir, "audio_${System.currentTimeMillis()}.3gp")
    fun startRecording(){
        recorder = MediaRecorder().apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
        }

        try {
            recorder.prepare()
            recorder.start()
            isRecording = true
        } catch (e: Exception){
            e.printStackTrace()
        }

    }
    fun stopRecording(){
        try{
            recorder.stop()
            recorder.reset()
            isRecording = false
            audioFilePath = audioFile.absolutePath
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    // photo
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var inputText by remember { mutableStateOf("")}
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var updatedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
            uri: Uri? ->
        uri?.let{
            imageUri = uri
            val inputStream = context.contentResolver.openInputStream(it)
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    val repository = remember { FirebaseRepository() }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("Location fetching...") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        val location = LocationUtils.getLastKnownLocation(context)
        locationName = LocationUtils.getCityName(context, location)
        latitude = location?.latitude ?: 0.0
        longitude = location?.longitude ?: 0.0
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars)) {

                Text(text = "New entry", style = MaterialTheme.typography.headlineSmall)

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
                .height(160.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "📍 Location: $locationName")
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = {
                imagePickerLauncher.launch("image/*")}){
                Text("Choose a photo from galery")
            }
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Tekst do dodania") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Chosen photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        val activity = context as? Activity
        Button(
            onClick = {
                if (!isRecording) {
                    if (activity != null && checkAndRequestAudioPermission(activity)) {
                        startRecording()
                    }
                }else {
                    stopRecording()
                }
            }
        ) {
            Text(if (!isRecording) "🎙️ Start recording" else "⏹️ Stop")
        }

        audioFilePath?.let {
            Text("✅ Recording saved")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {

            bitmap?.let { original ->
                 updatedBitmap = drawTextOnBitmap(original, inputText)

                val filename = "entry_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, filename)

                try {
                    val outputStream = FileOutputStream(file)
                    updatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    imageUri = file.toUri()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            val entry = DiaryEntry(
                title = title,
                content = content,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                imageUrl = imageUri?.toString(),
                audioUrl = audioFilePath
            )
            repository.addEntry(entry)
            onNavigateBack()
        },
            modifier = Modifier.align(Alignment.End)
            ) {
            Text("Save")
        }

        Button(onClick = {onNavigateBack()},
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("CANCEL")
        }
    }
}


