package com.ezhart.eztodo.viewmodels

import com.ezhart.eztodo.data.AllTasksFilter
import com.ezhart.eztodo.data.Filter
import com.ezhart.eztodo.data.Task

data class TaskListUIState(
    val filteredTasks: List<Task> = listOf(),
    val filter: Filter = AllTasksFilter,
    val textFilter: CharSequence = ""
) {
    val headerText = when(textFilter.isEmpty()){
        true -> filter.display()
        else -> "Searching in ${filter.display().lowercase()}"
    }

    val subHeaderText = when(textFilter.isEmpty()){
        true -> countText()
        else -> "$textFilter … ${countText()}"
    }

    fun countText(): String {
        return when(val count = filteredTasks.count()){
            1 -> "1 Task"
            else -> "$count Tasks"
        }
    }

    val shouldHandleBackNavigation = (!textFilter.isEmpty() || filter != AllTasksFilter)
}

