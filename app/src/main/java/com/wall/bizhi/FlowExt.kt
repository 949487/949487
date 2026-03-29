package com.wall.bizhi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.collectAsStateCompat(initial: T): State<T> = collectAsState(initial)
