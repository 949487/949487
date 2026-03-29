package com.wall.core.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WallpaperSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return runCatching {
            NetworkClient.htmlApi.fetch(inputData.getString("url") ?: "https://bing.com")
        }.fold(onSuccess = { Result.success() }, onFailure = { Result.retry() })
    }
}
