package com.wall.bizhi

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wall.feature.home.HomeScreen
import com.wall.feature.settings.SettingsRepository
import com.wall.feature.settings.SettingsRoute
import com.wall.feature.tools.ToolsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if ((context.resources.configuration.uiMode and 0x30) == 0x20) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        else -> if ((context.resources.configuration.uiMode and 0x30) == 0x20) darkColorScheme() else lightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme) {
        var tab by remember { mutableIntStateOf(0) }
        val repo = remember { SettingsRepository(context) }
        val rules by repo.rulesFlow.collectAsStateCompat(initial = emptyList())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    listOf("首页", "工具", "设置").forEachIndexed { i, title ->
                        NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = {}, label = { Text(title) })
                    }
                }
            }
        ) {
            AnimatedContent(tab, modifier = Modifier.fillMaxSize()) { t ->
                Box(Modifier.fillMaxSize()) {
                    when (t) {
                        0 -> HomeScreen(rules)
                        1 -> ToolsScreen(context)
                        else -> SettingsRoute(repo)
                    }
                }
            }
        }
    }
}
