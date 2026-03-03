package com.ezhart.todotxtandroid.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ezhart.todotxtandroid.TodotxtAndroidApplication
import com.ezhart.todotxtandroid.data.AllTasksFilter
import com.ezhart.todotxtandroid.data.CompletedFilter
import com.ezhart.todotxtandroid.data.ContextFilter
import com.ezhart.todotxtandroid.data.DueFilter
import com.ezhart.todotxtandroid.data.PendingFilter
import com.ezhart.todotxtandroid.data.ProjectFilter
import com.ezhart.todotxtandroid.data.Task
import com.ezhart.todotxtandroid.dropbox.DropboxService

class TasksViewModel(private val dropboxService: DropboxService, private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    val tasks: List<Task> = generateFakeTasks(100)

    var filter by mutableStateOf<Any>(AllTasksFilter)
    var filteredTasks by mutableStateOf(filterTasks(tasks, filter))
    var filterLabel by mutableStateOf(formatFilterLabel(filter))
    var allContexts by mutableStateOf(value = allContexts(tasks))
    var allProjects by mutableStateOf(value = allProjects(tasks))

    fun updateFilter(newFilter: Any) {
        filter = newFilter
        filteredTasks = filterTasks(tasks, filter)
        filterLabel = formatFilterLabel(filter)
    }

    private fun filterTasks(tasks: List<Task>, filter: Any): List<Task> {
        val result = when (filter) {
            is ProjectFilter -> tasks.filter { t -> t.projects.contains(filter.project) }
            is ContextFilter -> tasks.filter { t -> t.contexts.contains(filter.context) }
            is DueFilter -> tasks.filter { t -> t.dueDate != null }
            is PendingFilter -> tasks.filter { t -> !t.completed }
            is CompletedFilter -> tasks.filter { t -> t.completed }
            else -> tasks
        }

        return result
    }

    private fun formatFilterLabel(filter: Any): String {
        return when (filter) {
            is ProjectFilter -> "Project ${filter.project}"
            is DueFilter -> "Due Tasks"
            is ContextFilter -> "Context ${filter.context}"
            is PendingFilter -> "Pending Tasks"
            is CompletedFilter -> "Completed Tasks"
            else -> "All Tasks"
        }
    }

    fun allProjects(tasks: List<Task>): List<String> {
        return tasks.flatMap { t -> t.projects }.distinct().sorted()
    }

    fun allContexts(tasks: List<Task>): List<String> {
        return tasks.flatMap { t -> t.contexts }.distinct().sorted()
    }

    fun generateFakeTasks(count: Int): List<Task> {
        val x = mutableListOf<Task>()
        for (n in 0..count) {
            if (n % 9 == 0) {
                x.add(Task("x 2026-02-01 Task $n +shopping"))
            } else if (n % 5 == 0) {
                x.add(Task("Task $n @testContext"))
            } else if (n % 4 == 0) {
                x.add(Task("Task @testContext2 +project2"))
            } else {
                x.add(Task("Task $n"))
            }
        }

        return x
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val dropboxService =
                    (this[APPLICATION_KEY] as TodotxtAndroidApplication).dropboxService
                TasksViewModel(
                    dropboxService = dropboxService,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}