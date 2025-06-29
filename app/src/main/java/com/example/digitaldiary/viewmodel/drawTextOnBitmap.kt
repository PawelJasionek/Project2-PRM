package com.example.digitaldiary.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

fun drawTextOnBitmap(original: Bitmap, text: String): Bitmap {
    val result = original.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(result)
    val paint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        style = Paint.Style.FILL
        setShadowLayer(6f, 0f, 0f, Color.BLACK)
    }
    val x = 40f
    val y = result.height - 60f
    canvas.drawText(text, x, y, paint)
    return result
}