package com.wall.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wall.core.network.NetworkClient
import com.wall.core.xpath.XPathParser
import com.wall.core.xpath.XPathRule

@Composable
fun HomeScreen(rules: List<XPathRule>) {
    var urls by remember { mutableStateOf(emptyList<String>()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(rules) {
        val enabledRule = rules.firstOrNull { it.enabled } ?: return@LaunchedEffect
        loading = true
        urls = runCatching {
            val html = NetworkClient.htmlApi.fetch(enabledRule.url)
            XPathParser().parse(html, enabledRule.xpath)
        }.getOrDefault(emptyList())
        loading = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Tomato 风格壁纸", style = MaterialTheme.typography.titleLarge)
            if (loading) Text("加载中...")
        }
        items(urls) { url ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(
                        model = url,
                        contentDescription = url,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Button(onClick = {}) { Text("预览") }
                }
            }
        }
    }
}
