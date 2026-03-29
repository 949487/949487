package com.wall.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wall.core.network.NetworkClient
import com.wall.core.xpath.XPathParser
import com.wall.core.xpath.XPathRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    val rules = repo.rulesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val testResult = MutableStateFlow("")

    fun save(rule: XPathRule) = viewModelScope.launch { repo.upsert(rule) }
    fun remove(id: Long) = viewModelScope.launch { repo.delete(id) }
    fun importJson(json: String) = viewModelScope.launch { repo.importFromJson(json) }

    fun test(rule: XPathRule) = viewModelScope.launch {
        val html = NetworkClient.htmlApi.fetch(rule.url)
        val list = XPathParser().parse(html, rule.xpath)
        testResult.value = list.take(5).joinToString("\n")
    }

    companion object {
        fun factory(repo: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(repo) as T
            }
    }
}
