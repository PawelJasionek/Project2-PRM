package com.example.digitaldiary.data

import java.util.UUID


data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String= "",
    val content: String = "",
    val locationName: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
){}