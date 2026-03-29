package com.wall.bizhi.core.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import kotlin.math.sin

class HydrogenWallpaperModule : LiveWallpaperModule {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var tick = 0f

    override fun onCreate(engine: WallpaperService.Engine) {
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(12, 24, 40))
        repeat(18) { i ->
            val alpha = (120 + 100 * sin((tick + i) / 7f)).toInt().coerceIn(20, 220)
            paint.color = Color.argb(alpha, 110, 200, 255)
            val radius = 20f + i * 8f
            canvas.drawCircle(canvas.width * (i + 1) / 19f, canvas.height * 0.45f, radius, paint)
        }
        tick += 1f
    }

    override fun onTouch(event: MotionEvent) {
        tick += event.x / 100f
    }

    override fun onDestroy() = Unit
}
