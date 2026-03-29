package com.wall.core.xpath

import kotlinx.serialization.Serializable

@Serializable
data class XPathRule(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val url: String,
    val xpath: String,
    val enabled: Boolean = true
)
