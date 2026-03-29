package com.wall.feature.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wall.core.xpath.RuleJson
import com.wall.core.xpath.XPathRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("bizhi_settings")

class SettingsRepository(private val context: Context) {
    private val rulesKey = stringPreferencesKey("xpath_rules")

    val rulesFlow: Flow<List<XPathRule>> = context.dataStore.data.map { pref ->
        RuleJson.decode(pref[rulesKey] ?: "[]")
    }

    suspend fun upsert(rule: XPathRule) {
        context.dataStore.edit { pref ->
            val list = RuleJson.decode(pref[rulesKey] ?: "[]").toMutableList()
            val index = list.indexOfFirst { it.id == rule.id }
            if (index >= 0) list[index] = rule else list += rule
            pref[rulesKey] = RuleJson.encode(list)
        }
    }

    suspend fun delete(id: Long) {
        context.dataStore.edit { pref ->
            val list = RuleJson.decode(pref[rulesKey] ?: "[]").filterNot { it.id == id }
            pref[rulesKey] = RuleJson.encode(list)
        }
    }

    suspend fun importFromJson(json: String) {
        val incoming = RuleJson.decode(json)
        context.dataStore.edit { pref ->
            val merged = RuleJson.decode(pref[rulesKey] ?: "[]").toMutableList().apply { addAll(incoming) }
            pref[rulesKey] = RuleJson.encode(merged)
        }
    }
}
