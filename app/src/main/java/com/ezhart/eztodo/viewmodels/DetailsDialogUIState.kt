package com.ezhart.eztodo.viewmodels

import com.ezhart.eztodo.data.Task

data class DetailsDialogUIState(
    val task: Task? = null,
    val nextTask: Task? = null,
    val previousTask: Task? = null,
    val onUpdateSelectedTask: (Task) -> Unit = {},
    val onEditRequest: () -> Unit = {},
    val onToggleCompleted: () -> Unit = {}
)