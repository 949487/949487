package com.wall.core.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent

class TravelFrogWallpaperModule : LiveWallpaperModule {
    private val frogPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(92, 181, 92) }
    private val bgPaint = Paint().apply { color = Color.rgb(245, 233, 190) }
    private var x = 100f
    private var y = 200f
    private var dx = 8f
    private val actionCycle = listOf("吃饭", "写东西", "看书", "背帽子", "做手工", "整理背包", "音符")
    private var frame = 0

    override fun onCreate(engine: WallpaperService.Engine) = Unit

    override fun onDraw(canvas: Canvas) {
        frame++
        x += dx
        if (x > canvas.width - 120f || x < 0f) dx *= -1
        val actionIndex = (frame / 60) % actionCycle.size
        y = 180f + actionIndex * 35f
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), bgPaint)
        canvas.drawRoundRect(RectF(x, y, x + 120f, y + 100f), 32f, 32f, frogPaint)
    }

    override fun onTouch(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            x = event.x - 60f
            y = event.y - 50f
        }
    }

    override fun onDestroy() = Unit
}
