package com.wall.bizhi.core.wallpaper

data class WallpaperRuntimeConfig(
    val actionIntervalMs: Long = 60_000,
    val keepLauncherIcon: Boolean = true,
    val actions: List<String> = listOf("吃饭", "写东西", "看书", "背帽子", "做手工", "整理背包", "蜗牛", "音符")
)
