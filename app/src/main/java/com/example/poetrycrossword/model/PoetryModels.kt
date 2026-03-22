package com.example.poetrycrossword.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class Poem(
    val id: String,
    val title: String,
    val author: String,
    val dynasty: String,
    val fullText: String,
    val selectedLines: List<String>,
    val description: String
)

enum class Orientation {
    Horizontal,
    Vertical
}

data class GridPosition(
    val row: Int,
    val column: Int
)

data class PoemPlacement(
    val poemId: String,
    val lineText: String,
    val orientation: Orientation,
    val start: GridPosition,
    val color: Color,
    val clueLabel: String
)

data class CellContribution(
    val poemId: String,
    val expectedChar: Char,
    val orientation: Orientation,
    val color: Color
)

data class CrosswordCell(
    val position: GridPosition,
    val contributions: List<CellContribution> = emptyList(),
    val input: String = "",
    val isEditable: Boolean = contributions.isNotEmpty()
) {
    val expectedChar: Char?
        get() = contributions.firstOrNull()?.expectedChar

    val hasConflict: Boolean
        get() = contributions.map { it.expectedChar }.distinct().size > 1

    val isCrossPoint: Boolean
        get() = contributions.map { it.orientation }.distinct().size > 1
}

data class LineSolveState(
    val placement: PoemPlacement,
    val isSolved: Boolean,
    val completionRatio: Float
)

data class SolveDialogState(
    val poem: Poem,
    val solvedLineCount: Int,
    val totalLineCount: Int
)
