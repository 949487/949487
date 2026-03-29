package com.wall.feature.tools

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wall.core.wallpaper.TextWallpaperGenerator

data class ToolItem(val name: String, val desc: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolsScreen(context: Context) {
    val tools = listOf(
        ToolItem("氢动态壁纸", "来自 H2Wallpaper 的流体粒子动态背景。"),
        ToolItem("文字壁纸", "输入文字生成壁纸，并一键应用到锁屏和桌面。"),
        ToolItem("旅行青蛙动态壁纸", "动作时间配置：吃饭/写东西/看书/背帽子/做手工/整理背包/音符。"),
        ToolItem("姓氏壁纸", "基于 NamePic，支持单字姓氏图自定义背景与字体。")
    )
    var text by remember { mutableStateOf("WALL") }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.padding(12.dp),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("文字生成壁纸")
                    OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("文本") })
                    Button(onClick = { TextWallpaperGenerator(context).applyToSystem(text) }) { Text("应用到锁屏+桌面") }
                }
            }
        }
        items(tools) { tool ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tool.name)
                    Text(tool.desc)
                    Button(onClick = {}) { Text("打开") }
                }
            }
        }
    }
}
