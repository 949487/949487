package com.wall.bizhi.feature.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wall.bizhi.core.network.XPathRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "bizhi_settings")

class RuleStore(private val context: Context) {
    private val key = stringPreferencesKey("xpath_rules")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    val rules: Flow<List<XPathRule>> = context.dataStore.data.map { pref ->
        pref[key]?.let { json.decodeFromString<List<XPathRule>>(it) } ?: defaultRules()
    }

    suspend fun saveRules(rules: List<XPathRule>) {
        context.dataStore.edit { it[key] = json.encodeToString(rules) }
    }

    suspend fun importJson(jsonText: String): Result<List<XPathRule>> = runCatching {
        val imported = json.decodeFromString<List<XPathRule>>(jsonText)
        saveRules(imported)
        imported
    }

    private fun defaultRules() = listOf(
        XPathRule(name = "Wallhaven", url = "https://wallhaven.cc/toplist", xpath = "//img/@data-src", enabled = true)
    )
}
