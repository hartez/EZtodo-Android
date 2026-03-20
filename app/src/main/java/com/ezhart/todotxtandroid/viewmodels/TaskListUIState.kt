package com.ezhart.todotxtandroid.viewmodels

import com.ezhart.todotxtandroid.data.AllTasksFilter
import com.ezhart.todotxtandroid.data.Filter
import com.ezhart.todotxtandroid.data.Task

data class TaskListUIState(
    val filteredTasks: List<Task> = listOf(),
    val filter: Filter = AllTasksFilter,
    val textFilter: CharSequence = "",
    val allContexts: List<String> = listOf(),
    val allProjects: List<String> = listOf()
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

