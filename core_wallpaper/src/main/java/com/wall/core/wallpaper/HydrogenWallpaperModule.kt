package com.wall.core.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import kotlin.math.sin

class HydrogenWallpaperModule : LiveWallpaperModule {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.CYAN }
    private var tick = 0f

    override fun onCreate(engine: WallpaperService.Engine) = Unit

    override fun onDraw(canvas: Canvas) {
        tick += 0.1f
        canvas.drawColor(Color.BLACK)
        for (i in 0..12) {
            val x = canvas.width * i / 12f
            val y = canvas.height / 2f + sin(tick + i) * 180f
            canvas.drawCircle(x, y, 20f + i, paint)
        }
    }

    override fun onTouch(event: MotionEvent) {
        paint.color = if (event.action == MotionEvent.ACTION_DOWN) Color.GREEN else Color.CYAN
    }

    override fun onDestroy() = Unit
}
