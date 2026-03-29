package com.wall.bizhi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wall.bizhi.BizhiTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.wall.bizhi.feature.home.HomeScreen
import com.wall.bizhi.feature.home.HomeViewModel
import com.wall.bizhi.feature.settings.RuleStore
import com.wall.bizhi.feature.settings.SettingsScreen
import com.wall.bizhi.feature.settings.SettingsViewModel
import com.wall.bizhi.feature.tools.ToolsScreen
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scheduleWorker()
        setContent { BizhiApp(ruleStore = RuleStore(applicationContext)) }
    }

    private fun scheduleWorker() {
        val req = PeriodicWorkRequestBuilder<WallpaperRefreshWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("refresh", ExistingPeriodicWorkPolicy.KEEP, req)
    }
}

@Composable
private fun BizhiApp(ruleStore: RuleStore) {
    BizhiTheme {
        var tab by remember { mutableStateOf(0) }
        val homeVm: HomeViewModel = viewModel(factory = HomeVmFactory(ruleStore))
        val settingsVm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(ruleStore))
        LaunchedEffect(Unit) { homeVm.load() }

        Scaffold(bottomBar = {
            NavigationBar {
                listOf("首页", "工具", "设置").forEachIndexed { index, title ->
                    NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Text("•") }, label = { Text(title) })
                }
            }
        }) { padding ->
            androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.padding(padding)) {
                when (tab) {
                    0 -> HomeScreen(homeVm)
                    1 -> ToolsScreen()
                    else -> SettingsScreen(settingsVm)
                }
            }
        }
    }
}
