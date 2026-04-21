package com.ezhart.eztodo.viewmodels

import com.ezhart.eztodo.data.AllTasksFilter
import com.ezhart.eztodo.data.Filter

data class FilterSheetUIState(
    val contexts: List<String> = listOf(),
    val projects: List<String> = listOf(),
    val selectedFilter: Filter = AllTasksFilter,
    val onUpdateFilter: (Filter) -> Unit = {}
)