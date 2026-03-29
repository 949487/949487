package com.wall.core.wallpaper

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

class BizhiLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = BizhiEngine()

    private inner class BizhiEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val drawer = object : Runnable {
            override fun run() {
                drawFrame()
                handler.postDelayed(this, 16)
            }
        }

        private var module: LiveWallpaperModule = HydrogenWallpaperModule()

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            module.onCreate(this)
            setTouchEventsEnabled(true)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) drawer.run() else handler.removeCallbacks(drawer)
        }

        private fun drawFrame() {
            val canvas: Canvas = surfaceHolder.lockCanvas() ?: return
            try {
                module.onDraw(canvas)
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            module.onTouch(event)
        }

        override fun onDestroy() {
            handler.removeCallbacks(drawer)
            module.onDestroy()
            super.onDestroy()
        }
    }
}
