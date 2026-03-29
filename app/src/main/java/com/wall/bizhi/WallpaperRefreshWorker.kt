package com.wall.bizhi

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WallpaperRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val bmp = Bitmap.createBitmap(1080, 2400, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.DKGRAY) }
        WallpaperManager.getInstance(applicationContext).setBitmap(bmp)
        return Result.success()
    }
}
