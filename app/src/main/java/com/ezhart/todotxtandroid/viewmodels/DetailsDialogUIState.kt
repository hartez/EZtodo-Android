package com.ezhart.todotxtandroid.viewmodels

import com.ezhart.todotxtandroid.data.Task

data class DetailsDialogUIState(
    val isOpen: Boolean = false,
    val task: Task? = null,
    val nextTask: Task? = null,
    val previousTask: Task? = null,
    val onDismissRequest: () -> Unit = {},
    val onUpdateSelectedTask: (Task) -> Unit = {},
    val onEditRequest: () -> Unit = {},
    val onToggleCompleted: () -> Unit = {}
)