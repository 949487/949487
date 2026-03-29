package com.wall.bizhi

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.wall.bizhi.core.wallpaper.HydrogenWallpaperModule
import com.wall.bizhi.core.wallpaper.LiveWallpaperModule

class UnifiedLiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = UnifiedEngine(HydrogenWallpaperModule())

    inner class UnifiedEngine(private val module: LiveWallpaperModule) : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val drawRunner = object : Runnable {
            override fun run() {
                drawFrame()
                handler.postDelayed(this, 33L)
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            module.onCreate(this)
            handler.post(drawRunner)
        }

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            val canvas: Canvas = holder.lockCanvas() ?: return
            try { module.onDraw(canvas) } finally { holder.unlockCanvasAndPost(canvas) }
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            module.onTouch(event)
        }

        override fun onDestroy() {
            handler.removeCallbacks(drawRunner)
            module.onDestroy()
            super.onDestroy()
        }
    }
}
