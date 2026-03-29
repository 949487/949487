package com.wall.bizhi.feature.settings

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.wall.bizhi.core.network.XPathRule

@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val rules by vm.rules.collectAsStateWithLifecycle()
    val test by vm.testResult.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<XPathRule?>(null) }
    var importJson by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { editing = XPathRule(name = "", url = "", xpath = "") }) { Text("添加规则") }
            Button(onClick = { importJson = true }) { Text("导入JSON") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rules, key = { it.id }) { rule ->
                RuleCard(rule,
                    onEdit = { editing = rule },
                    onDelete = { vm.deleteRule(rule.id) },
                    onEnabled = { vm.upsertRule(rule.copy(enabled = !rule.enabled)) },
                    onTest = { vm.testRule(rule) })
            }
        }
        if (test.isNotEmpty()) {
            Text("测试结果: ${test.take(3).joinToString()}", style = MaterialTheme.typography.bodySmall)
        }
    }

    editing?.let { RuleEditor(initial = it, onDismiss = { editing = null }, onSave = {
        vm.upsertRule(it); editing = null
    }) }

    if (importJson) {
        var text by remember { mutableStateOf("[]") }
        AlertDialog(onDismissRequest = { importJson = false },
            title = { Text("导入规则") },
            text = { OutlinedTextField(text, { text = it }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { Button(onClick = { vm.importJson(text); importJson = false }) { Text("导入") } },
            dismissButton = { TextButton(onClick = { importJson = false }) { Text("取消") } })
    }
}

@Composable
private fun RuleCard(rule: XPathRule, onEdit: () -> Unit, onDelete: () -> Unit, onEnabled: () -> Unit, onTest: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(rule.name, style = MaterialTheme.typography.titleMedium)
                Switch(rule.enabled, onCheckedChange = { onEnabled() })
            }
            Text(rule.url, style = MaterialTheme.typography.bodySmall)
            Text(rule.xpath, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) { Text("编辑") }
                TextButton(onClick = onDelete) { Text("删除") }
                TextButton(onClick = onTest) { Text("测试XPath") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleEditor(initial: XPathRule, onDismiss: () -> Unit, onSave: (XPathRule) -> Unit) {
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var url by remember(initial.id) { mutableStateOf(initial.url) }
    var xpath by remember(initial.id) { mutableStateOf(initial.xpath) }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("规则编辑") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("name") })
                OutlinedTextField(url, { url = it }, label = { Text("url") })
                OutlinedTextField(xpath, { xpath = it }, label = { Text("xpath") })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(initial.copy(name = name, url = url, xpath = xpath)) }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
