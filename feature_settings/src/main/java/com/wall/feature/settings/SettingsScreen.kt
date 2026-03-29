package com.wall.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wall.core.xpath.XPathRule

@Composable
fun SettingsRoute(repo: SettingsRepository) {
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(repo))
    val rules by vm.rules.collectAsStateWithLifecycle()
    val result by vm.testResult.collectAsStateWithLifecycle()
    var editor by remember { mutableStateOf<XPathRule?>(null) }
    var importText by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("XPath 规则管理")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { editor = XPathRule(name = "", url = "", xpath = "") }) { Text("添加规则") }
                Button(onClick = { vm.importJson(importText) }) { Text("导入JSON") }
            }
            OutlinedTextField(value = importText, onValueChange = { importText = it }, label = { Text("JSON") }, modifier = Modifier.fillMaxWidth())
            if (result.isNotBlank()) Text("测试结果:\n$result")
        }
        items(rules, key = { it.id }) { rule ->
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(rule.name)
                    Text(rule.url)
                    Text(rule.xpath)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { editor = rule }) { Text("编辑") }
                        Button(onClick = { vm.test(rule) }) { Text("测试") }
                        Button(onClick = { vm.remove(rule.id) }) { Text("删除") }
                    }
                }
            }
        }
    }

    editor?.let { target ->
        RuleEditor(rule = target, onDismiss = { editor = null }, onSave = { vm.save(it); editor = null })
    }
}

@Composable
private fun RuleEditor(rule: XPathRule, onDismiss: () -> Unit, onSave: (XPathRule) -> Unit) {
    var name by remember(rule.id) { mutableStateOf(rule.name) }
    var url by remember(rule.id) { mutableStateOf(rule.url) }
    var xpath by remember(rule.id) { mutableStateOf(rule.xpath) }
    var enabled by remember(rule.id) { mutableStateOf(rule.enabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(rule.copy(name = name, url = url, xpath = xpath, enabled = enabled)) }) { Text("保存") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("name") })
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("url") })
                OutlinedTextField(value = xpath, onValueChange = { xpath = it }, label = { Text("xpath") })
                Row { Text("enabled"); Switch(checked = enabled, onCheckedChange = { enabled = it }) }
            }
        }
    )
}
