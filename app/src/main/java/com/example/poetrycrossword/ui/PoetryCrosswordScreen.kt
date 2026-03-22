package com.example.poetrycrossword.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poetrycrossword.data.CrosswordEngine
import com.example.poetrycrossword.model.CrosswordCell
import com.example.poetrycrossword.model.LineSolveState
import com.example.poetrycrossword.model.Orientation
import com.example.poetrycrossword.model.Poem
import com.example.poetrycrossword.model.PoemPlacement
import com.example.poetrycrossword.model.SolveDialogState
import com.example.poetrycrossword.ui.theme.PoetryCrosswordTheme

@Composable
fun PoetryCrosswordApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val poems = remember { com.example.poetrycrossword.data.PoetryRepository.loadPoems(context) }
    val placements = remember(poems) { CrosswordEngine.createPlacements(poems) }
    var grid by rememberSaveable(stateSaver = GridSaver()) {
        mutableStateOf(CrosswordEngine.buildGrid(placements))
    }
    var shownDialogPoemIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    val lineStates = remember(grid, placements) { CrosswordEngine.lineStates(grid, placements) }
    var dialogState by remember { mutableStateOf<SolveDialogState?>(null) }

    LaunchedEffect(lineStates, shownDialogPoemIds) {
        val nextDialog = CrosswordEngine.nextDialog(poems, lineStates, shownDialogPoemIds)
        if (nextDialog != null) {
            dialogState = nextDialog
            shownDialogPoemIds = shownDialogPoemIds + nextDialog.poem.id
        }
    }

    PoetryCrosswordScreen(
        poems = poems,
        placements = placements,
        grid = grid,
        lineStates = lineStates,
        onCellInput = { row, column, input ->
            grid = CrosswordEngine.updateCell(grid, row, column, input)
        },
        onReset = {
            grid = CrosswordEngine.buildGrid(placements)
            shownDialogPoemIds = emptySet()
            dialogState = null
        },
        dialogState = dialogState,
        onDismissDialog = { dialogState = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PoetryCrosswordScreen(
    poems: List<Poem>,
    placements: List<PoemPlacement>,
    grid: List<List<CrosswordCell>>,
    lineStates: List<LineSolveState>,
    onCellInput: (row: Int, column: Int, value: String) -> Unit,
    onReset: () -> Unit,
    dialogState: SolveDialogState?,
    onDismissDialog: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("诗词填字 · Material 3 Expressive") },
                actions = {
                    AssistChip(
                        onClick = onReset,
                        label = { Text("重置棋盘") },
                        leadingIcon = {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderCard(poems = poems, lineStates = lineStates)
            }
            item {
                LegendCard(poems = poems, placements = placements)
            }
            item {
                GridCard(grid = grid, onCellInput = onCellInput)
            }
            item {
                ClueCard(placements = placements, lineStates = lineStates)
            }
            item {
                PoemJsonCard(poems = poems)
            }
        }
    }

    if (dialogState != null) {
        SolveDialog(dialogState = dialogState, onDismiss = onDismissDialog)
    }
}

@Composable
private fun HeaderCard(poems: List<Poem>, lineStates: List<LineSolveState>) {
    val solvedLines = lineStates.count { it.isSolved }
    val progress by animateFloatAsState(
        targetValue = if (lineStates.isEmpty()) 0f else solvedLines.toFloat() / lineStates.size,
        label = "boardProgress"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "24 × 20 格诗词填字",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "支持横向/纵向填入经典诗句、交叉共字验证、空白留白与完成后弹窗展示诗词详情。当前示例加载 ${poems.size} 首诗。",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text(
                text = "已完成 $solvedLines / ${lineStates.size} 条诗句",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun LegendCard(poems: List<Poem>, placements: List<PoemPlacement>) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.GridOn, contentDescription = null)
                Text("颜色图例", style = MaterialTheme.typography.titleLarge)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                placements.groupBy { it.poemId }.values.forEach { poemPlacements ->
                    val placement = poemPlacements.first()
                    val poemTitle = poems.firstOrNull { it.id == placement.poemId }?.title ?: placement.poemId
                    AssistChip(
                        onClick = {},
                        label = { Text("$poemTitle · ${poemPlacements.size} 句") },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(placement.color)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GridCard(
    grid: List<List<CrosswordCell>>,
    onCellInput: (row: Int, column: Int, value: String) -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("棋盘", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "空白格保持留白；同一格若被横竖诗句共享，输入字符必须同时满足两边诗句。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                grid.forEachIndexed { rowIndex, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        row.forEachIndexed { columnIndex, cell ->
                            CrosswordInputCell(
                                cell = cell,
                                onValueChange = { onCellInput(rowIndex, columnIndex, it) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CrosswordInputCell(
    cell: CrosswordCell,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = when {
        !cell.isEditable -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        cell.hasConflict -> MaterialTheme.colorScheme.errorContainer
        cell.isCrossPoint -> cell.contributions.first().color.copy(alpha = 0.55f)
        else -> cell.contributions.first().color.copy(alpha = 0.25f)
    }
    val borderColor = when {
        !cell.isEditable -> Color.Transparent
        cell.input.singleOrNull() == cell.expectedChar -> MaterialTheme.colorScheme.primary
        cell.input.isNotEmpty() -> MaterialTheme.colorScheme.tertiary
        else -> baseColor.copy(alpha = 0.9f)
    }

    Surface(
        modifier = modifier.aspectRatio(1f),
        tonalElevation = if (cell.isEditable) 2.dp else 0.dp,
        color = baseColor,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, borderColor)
    ) {
        if (!cell.isEditable) {
            Spacer(modifier = Modifier.fillMaxSize())
        } else {
            BasicTextField(
                value = cell.input,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 6.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (cell.input.isEmpty()) {
                            Text(
                                text = cell.expectedChar?.toString() ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun ClueCard(placements: List<PoemPlacement>, lineStates: List<LineSolveState>) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("诗句列表与验证状态", style = MaterialTheme.typography.titleLarge)
            placements.zip(lineStates).forEach { (placement, state) ->
                val orientationText = if (placement.orientation == Orientation.Horizontal) "横向" else "纵向"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(placement.color.copy(alpha = 0.14f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                        Text("${placement.clueLabel} · $orientationText", fontWeight = FontWeight.SemiBold)
                        Text(placement.lineText, style = MaterialTheme.typography.bodyLarge)
                    }
                    AnimatedVisibility(visible = state.isSolved) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    if (!state.isSolved) {
                        Text("${(state.completionRatio * 100).toInt()}%")
                    }
                }
            }
        }
    }
}

@Composable
private fun PoemJsonCard(poems: List<Poem>) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.Info, contentDescription = null)
                Text("示例 JSON 数据", style = MaterialTheme.typography.titleLarge)
            }
            val jsonPreview = poems.joinToString(
                prefix = "[\n",
                postfix = "\n]",
                separator = ",\n"
            ) { poem ->
                "  {\n" +
                    "    \"诗词\": \"${poem.title}\",\n" +
                    "    \"要的诗词\": ${poem.selectedLines.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }},\n" +
                    "    \"作者\": \"${poem.author}\"\n" +
                    "  }"
            }
            Text(jsonPreview, style = TextStyle(fontSize = 12.sp, lineHeight = 18.sp))
        }
    }
}

@Composable
private fun SolveDialog(dialogState: SolveDialogState, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("继续挑战")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        },
        icon = {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        title = {
            Text("完成《${dialogState.poem.title}》")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("作者：${dialogState.poem.dynasty} · ${dialogState.poem.author}")
                Text("已完成 ${dialogState.solvedLineCount}/${dialogState.totalLineCount} 条关联诗句。")
                Text("全诗：${dialogState.poem.fullText}")
                Text(dialogState.poem.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

private fun GridSaver() = androidx.compose.runtime.saveable.listSaver(
    save = { grid ->
        grid.flatten().map { cell -> cell.input }
    },
    restore = { inputs ->
        val previewPoems = previewPoems()
        val placements = CrosswordEngine.createPlacements(previewPoems)
        val baseGrid = CrosswordEngine.buildGrid(placements)
        baseGrid.mapIndexed { row, rowCells ->
            rowCells.mapIndexed { column, cell ->
                val index = row * CrosswordEngine.ColumnCount + column
                cell.copy(input = inputs.getOrElse(index) { "" })
            }
        }
    }
)

private fun previewPoems() = listOf(
    Poem("quiet-night-thought", "静夜思", "李白", "唐", "床前明月光，疑是地上霜。举头望明月，低头思故乡。", listOf("床前明月光", "举头望明月"), ""),
    Poem("mutual-longing", "相思", "王维", "唐", "红豆生南国，春来发几枝。愿君多采撷，此物最相思。", listOf("红豆生南国", "春来发几枝"), ""),
    Poem("on-the-stork-tower", "登鹳雀楼", "王之涣", "唐", "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。", listOf("白日依山尽", "黄河入海流"), ""),
    Poem("waterfall", "望庐山瀑布", "李白", "唐", "日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。", listOf("飞流直下三千尺", "疑是银河落九天"), "")
)

@Preview(showBackground = true, widthDp = 1280, heightDp = 2400)
@Composable
private fun PoetryCrosswordScreenPreview() {
    val poems = previewPoems()
    val placements = CrosswordEngine.createPlacements(poems)
    val grid = CrosswordEngine.buildGrid(placements)
    val states = CrosswordEngine.lineStates(grid, placements)
    PoetryCrosswordTheme {
        PoetryCrosswordScreen(
            poems = poems,
            placements = placements,
            grid = grid,
            lineStates = states,
            onCellInput = { _, _, _ -> },
            onReset = {},
            dialogState = null,
            onDismissDialog = {}
        )
    }
}
