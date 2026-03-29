package com.wall.bizhi.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wall.bizhi.core.network.WallpaperItem
import com.wall.bizhi.core.network.WallpaperRepository
import com.wall.bizhi.feature.settings.RuleStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WallpaperRepository = WallpaperRepository(),
    private val ruleStore: RuleStore
) : ViewModel() {
    private val _items = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val items: StateFlow<List<WallpaperItem>> = _items.asStateFlow()

    fun load() = viewModelScope.launch {
        val rules = ruleStore.rules
        rules.collect { list ->
            _items.value = list.filter { it.enabled }.flatMap { repository.fetchByRule(it) }
        }
    }
}
