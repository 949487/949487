package com.example.poetrycrossword.data

import androidx.compose.ui.graphics.Color
import com.example.poetrycrossword.model.CellContribution
import com.example.poetrycrossword.model.CrosswordCell
import com.example.poetrycrossword.model.GridPosition
import com.example.poetrycrossword.model.LineSolveState
import com.example.poetrycrossword.model.Orientation
import com.example.poetrycrossword.model.Poem
import com.example.poetrycrossword.model.PoemPlacement
import com.example.poetrycrossword.model.SolveDialogState

object CrosswordEngine {
    const val RowCount = 24
    const val ColumnCount = 20

    private val palette = listOf(
        Color(0xFFE57373),
        Color(0xFF64B5F6),
        Color(0xFF81C784),
        Color(0xFFFFB74D),
        Color(0xFFBA68C8),
        Color(0xFF4DB6AC),
        Color(0xFFFF8A65),
        Color(0xFFA1887F)
    )

    fun createPlacements(poems: List<Poem>): List<PoemPlacement> {
        require(poems.size >= 4) { "At least four poems are required." }
        return listOf(
            PoemPlacement(poems[0].id, poems[0].selectedLines[0], Orientation.Horizontal, GridPosition(6, 2), palette[0], "A1"),
            PoemPlacement(poems[0].id, poems[0].selectedLines[1], Orientation.Vertical, GridPosition(3, 4), palette[0], "D1"),
            PoemPlacement(poems[1].id, poems[1].selectedLines[0], Orientation.Horizontal, GridPosition(10, 10), palette[1], "A2"),
            PoemPlacement(poems[1].id, poems[1].selectedLines[1], Orientation.Vertical, GridPosition(13, 15), palette[1], "D2"),
            PoemPlacement(poems[2].id, poems[2].selectedLines[0], Orientation.Horizontal, GridPosition(16, 1), palette[2], "A3"),
            PoemPlacement(poems[2].id, poems[2].selectedLines[1], Orientation.Vertical, GridPosition(12, 18), palette[2], "D3"),
            PoemPlacement(poems[3].id, poems[3].selectedLines[0], Orientation.Horizontal, GridPosition(20, 2), palette[3], "A4"),
            PoemPlacement(poems[3].id, poems[3].selectedLines[1], Orientation.Vertical, GridPosition(4, 0), palette[3], "D4")
        )
    }

    fun buildGrid(placements: List<PoemPlacement>): List<List<CrosswordCell>> {
        val contributionMap = mutableMapOf<GridPosition, MutableList<CellContribution>>()
        placements.forEach { placement ->
            placement.lineText.forEachIndexed { index, char ->
                val position = when (placement.orientation) {
                    Orientation.Horizontal -> GridPosition(placement.start.row, placement.start.column + index)
                    Orientation.Vertical -> GridPosition(placement.start.row + index, placement.start.column)
                }
                require(position.row in 0 until RowCount && position.column in 0 until ColumnCount) {
                    "Placement ${placement.clueLabel} exceeds board bounds."
                }
                contributionMap.getOrPut(position) { mutableListOf() }
                    .add(CellContribution(placement.poemId, char, placement.orientation, placement.color))
            }
        }
        return List(RowCount) { row ->
            List(ColumnCount) { column ->
                val position = GridPosition(row, column)
                CrosswordCell(position = position, contributions = contributionMap[position].orEmpty())
            }
        }
    }

    fun updateCell(
        grid: List<List<CrosswordCell>>,
        row: Int,
        column: Int,
        rawInput: String
    ): List<List<CrosswordCell>> {
        val normalized = rawInput.takeLast(1)
        return grid.mapIndexed { r, rowCells ->
            rowCells.mapIndexed { c, cell ->
                if (r == row && c == column && cell.isEditable) {
                    cell.copy(input = normalized)
                } else {
                    cell
                }
            }
        }
    }

    fun lineStates(
        grid: List<List<CrosswordCell>>,
        placements: List<PoemPlacement>
    ): List<LineSolveState> {
        return placements.map { placement ->
            val cells = cellsForPlacement(grid, placement)
            val correctCount = cells.count { cell -> cell.input.singleOrNull() == cell.expectedChar }
            LineSolveState(
                placement = placement,
                isSolved = correctCount == placement.lineText.length,
                completionRatio = correctCount.toFloat() / placement.lineText.length.toFloat()
            )
        }
    }

    fun nextDialog(
        poems: List<Poem>,
        states: List<LineSolveState>,
        shownPoemIds: Set<String>
    ): SolveDialogState? {
        val grouped = states.groupBy { it.placement.poemId }
        val solvedPoemId = grouped.entries.firstOrNull { (poemId, lineStates) ->
            poemId !in shownPoemIds && lineStates.all { it.isSolved }
        }?.key ?: return null

        val poem = poems.firstOrNull { it.id == solvedPoemId } ?: return null
        return SolveDialogState(poem, grouped.getValue(solvedPoemId).count { it.isSolved }, grouped.getValue(solvedPoemId).size)
    }

    private fun cellsForPlacement(
        grid: List<List<CrosswordCell>>,
        placement: PoemPlacement
    ): List<CrosswordCell> {
        return placement.lineText.indices.map { index ->
            val row = if (placement.orientation == Orientation.Horizontal) placement.start.row else placement.start.row + index
            val column = if (placement.orientation == Orientation.Horizontal) placement.start.column + index else placement.start.column
            grid[row][column]
        }
    }
}
