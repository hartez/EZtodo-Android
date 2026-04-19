package com.ezhart.eztodo.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ezhart.eztodo.data.Task

data class SwipeOption(
    val label: String,
    val backgroundColor: Color,
    val foregroundColor: Color,
    val icon: ImageVector,
    val onSwipe: (Task) -> Unit
)

data class TaskSwipeOptions(
    val startToEndOption: SwipeOption? = null,
    val endToStartOption: SwipeOption? = null
)


