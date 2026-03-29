package com.wall.bizhi.core.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent

class TravelFrogWallpaperModule : LiveWallpaperModule {
    private val frogPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(102, 196, 112) }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(232, 245, 214) }
    private var x = 100f
    private var y = 300f
    private var dx = 8f

    override fun onCreate(engine: WallpaperService.Engine) = Unit

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), bgPaint)
        x += dx
        if (x > canvas.width - 140f || x < 20f) dx = -dx
        val body = RectF(x, y, x + 120f, y + 90f)
        canvas.drawRoundRect(body, 35f, 35f, frogPaint)
    }

    override fun onTouch(event: MotionEvent) {
        y = event.y.coerceIn(120f, 1200f)
    }

    override fun onDestroy() = Unit
}
