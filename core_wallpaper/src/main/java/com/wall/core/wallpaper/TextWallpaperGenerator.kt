package com.wall.core.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class TextWallpaperGenerator(private val context: Context) {
    fun generate(text: String, width: Int = 1080, height: Int = 2400): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.BLACK)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 180f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(text, width / 2f, height / 2f, paint)
        return bmp
    }

    fun applyToSystem(text: String) {
        val bmp = generate(text)
        val manager = WallpaperManager.getInstance(context)
        manager.setBitmap(bmp, null, true, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
    }
}
