package com.example.digitaldiary.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.digitaldiary.data.DiaryEntry
import androidx.core.net.toUri
import android.util.Log
import androidx.compose.ui.Alignment
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun DiaryCard(entry: DiaryEntry, onEditClick: () -> Unit) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false)}
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }


    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📍 ${entry.locationName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            entry.imageUrl?.let { uriStr ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = uriStr,
                    contentDescription = "Entry Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            entry.audioUrl?.let { audioPath ->
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    if(!isPlaying){
                        val file = File(audioPath.toUri().path ?: "")
                        Log.d("AudioPlayback", "File exists: ${file.exists()}")
                        mediaPlayer = MediaPlayer().apply{
                            setDataSource(context, audioPath.toUri())
                            prepare()
                            start()
                            setOnCompletionListener {
                                isPlaying = false
                                release()
                                mediaPlayer = null
                            }
                        }
                    }else {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                        isPlaying = false
                    }
                }){
                    Text(if (isPlaying) "⏹ Stop" else "▶ Run recording")
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()) {
                IconButton(onClick = { onEditClick()}) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit entry")
                }
                Text(modifier = Modifier.align(Alignment.CenterVertically),
                    text = "Edit this entry")
            }
        }
    }
}
