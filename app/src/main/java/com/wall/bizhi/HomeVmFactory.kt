package com.wall.bizhi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wall.bizhi.feature.home.HomeViewModel
import com.wall.bizhi.feature.settings.RuleStore

class HomeVmFactory(private val ruleStore: RuleStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(ruleStore = ruleStore) as T
}
