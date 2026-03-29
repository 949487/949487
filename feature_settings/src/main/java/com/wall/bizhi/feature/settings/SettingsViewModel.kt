package com.wall.bizhi.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wall.bizhi.core.network.WallpaperRepository
import com.wall.bizhi.core.network.XPathRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val ruleStore: RuleStore,
    private val repository: WallpaperRepository = WallpaperRepository()
) : ViewModel() {
    val rules = ruleStore.rules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val testResult = MutableStateFlow<List<String>>(emptyList())
    val message = MutableStateFlow<String?>(null)

    fun upsertRule(rule: XPathRule) = viewModelScope.launch {
        val list = rules.value.toMutableList()
        val index = list.indexOfFirst { it.id == rule.id }
        if (index >= 0) list[index] = rule else list.add(rule)
        ruleStore.saveRules(list)
    }

    fun deleteRule(id: Long) = viewModelScope.launch {
        ruleStore.saveRules(rules.value.filterNot { it.id == id })
    }

    fun importJson(json: String) = viewModelScope.launch {
        ruleStore.importJson(json).onFailure { message.value = it.message }
    }

    fun testRule(rule: XPathRule) = viewModelScope.launch {
        testResult.value = repository.testRule(rule)
    }

    class Factory(private val store: RuleStore) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(store) as T
    }
}
