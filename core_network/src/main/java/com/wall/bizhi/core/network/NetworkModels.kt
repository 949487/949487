package com.wall.bizhi.core.network

import kotlinx.serialization.Serializable

@Serializable
data class XPathRule(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val url: String,
    val xpath: String,
    val enabled: Boolean = true
)

data class WallpaperItem(
    val title: String,
    val imageUrl: String,
    val sourceRule: String
)
