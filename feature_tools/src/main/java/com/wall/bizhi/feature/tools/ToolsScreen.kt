package com.wall.bizhi.feature.tools

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class ToolFeature(val name: String, val desc: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolsScreen() {
    val context = LocalContext.current
    val features = listOf(
        ToolFeature("氢动态壁纸", "轻量动态粒子效果，可设为动态壁纸"),
        ToolFeature("文字壁纸", "输入文字生成并一键应用锁屏与桌面"),
        ToolFeature("旅行青蛙", "含动作时间配置与图标保留策略"),
        ToolFeature("姓氏壁纸", "单字姓氏海报生成，支持自定义字体配色")
    )
    val text = remember { mutableStateOf("WALL BIZHI") }

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = text.value, onValueChange = { text.value = it }, modifier = Modifier.fillMaxWidth(), label = { Text("输入文字") })
        Button(onClick = { applyTextWallpaper(context, text.value) }) { Text("一键应用锁屏+桌面") }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(180.dp),
            contentPadding = PaddingValues(4.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(features) { item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(item.name)
                        Text(item.desc)
                    }
                }
            }
        }
    }
}

private fun applyTextWallpaper(context: Context, text: String) {
    val bmp = Bitmap.createBitmap(1080, 2400, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    canvas.drawColor(Color.BLACK)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 120f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(text, 540f, 1200f, paint)
    WallpaperManager.getInstance(context).apply {
        setBitmap(bmp, null, true, WallpaperManager.FLAG_SYSTEM)
        setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK)
    }
}
