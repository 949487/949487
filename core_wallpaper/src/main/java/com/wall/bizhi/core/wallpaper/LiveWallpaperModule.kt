package com.wall.bizhi.core.wallpaper

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent

interface LiveWallpaperModule {
    fun onCreate(engine: WallpaperService.Engine)
    fun onDraw(canvas: Canvas)
    fun onTouch(event: MotionEvent)
    fun onDestroy()
}
