package com.example.poetrycrossword

import com.example.poetrycrossword.data.CrosswordEngine
import com.example.poetrycrossword.model.Poem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CrosswordEngineTest {
    private val poems = listOf(
        Poem("quiet-night-thought", "静夜思", "李白", "唐", "床前明月光，疑是地上霜。举头望明月，低头思故乡。", listOf("床前明月光", "举头望明月"), ""),
        Poem("mutual-longing", "相思", "王维", "唐", "红豆生南国，春来发几枝。愿君多采撷，此物最相思。", listOf("红豆生南国", "春来发几枝"), ""),
        Poem("on-the-stork-tower", "登鹳雀楼", "王之涣", "唐", "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。", listOf("白日依山尽", "黄河入海流"), ""),
        Poem("waterfall", "望庐山瀑布", "李白", "唐", "日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。", listOf("飞流直下三千尺", "疑是银河落九天"), "")
    )

    @Test
    fun boardHasConfiguredCrossPointWithoutConflicts() {
        val grid = CrosswordEngine.buildGrid(CrosswordEngine.createPlacements(poems))
        val crossCell = grid[6][4]
        assertTrue(crossCell.isEditable)
        assertTrue(crossCell.isCrossPoint)
        assertFalse(crossCell.hasConflict)
        assertEquals('明', crossCell.expectedChar)
    }

    @Test
    fun fullySolvedPoemProducesDialogState() {
        val placements = CrosswordEngine.createPlacements(poems)
        var grid = CrosswordEngine.buildGrid(placements)
        placements.take(2).forEach { placement ->
            placement.lineText.forEachIndexed { index, char ->
                val row = if (placement.orientation == com.example.poetrycrossword.model.Orientation.Horizontal) placement.start.row else placement.start.row + index
                val column = if (placement.orientation == com.example.poetrycrossword.model.Orientation.Horizontal) placement.start.column + index else placement.start.column
                grid = CrosswordEngine.updateCell(grid, row, column, char.toString())
            }
        }

        val dialog = CrosswordEngine.nextDialog(poems, CrosswordEngine.lineStates(grid, placements), emptySet())
        assertNotNull(dialog)
        assertEquals("静夜思", dialog?.poem?.title)
        assertEquals(2, dialog?.solvedLineCount)
    }
}
