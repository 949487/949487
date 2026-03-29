package com.wall.core.xpath

import kotlinx.serialization.json.Json

object RuleJson {
    private val json = Json { ignoreUnknownKeys = true }

    fun encode(rules: List<XPathRule>): String = json.encodeToString(rules)

    fun decode(text: String): List<XPathRule> = runCatching {
        if (text.trim().startsWith("[")) json.decodeFromString<List<XPathRule>>(text)
        else listOf(json.decodeFromString<XPathRule>(text))
    }.getOrDefault(emptyList())
}
