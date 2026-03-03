package com.ezhart.todotxtandroid

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.todotxtandroid.data.Task
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import com.ezhart.todotxtandroid.viewmodels.TasksViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskListScreen(onNavigateToSettings: () -> Unit) {

    val tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory)

    val tasks = tasksViewModel.filteredTasks
    val filter = tasksViewModel.filter
    val filterLabel = tasksViewModel.filterLabel

    // TODO hoist this into a separate class for maintaining sheet state
    // with open and close methods
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var isNavSheetOpen by remember { mutableStateOf(false) }

    TodotxtAndroidTheme {
        Scaffold(
            contentWindowInsets = WindowInsets.statusBars,
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                AppBar(
                    { isFilterSheetOpen = true },
                    { isNavSheetOpen = true }
                )
            }
        ) { innerPadding ->
            TaskList(
                tasks, filterLabel,
                { },
                modifier = Modifier
                    .padding(innerPadding)
            )

            FiltersSheet(
                tasksViewModel.allProjects,
                tasksViewModel.allContexts,
                isFilterSheetOpen,
                { isFilterSheetOpen = false },
                onUpdateFilter = tasksViewModel::updateFilter,
                filter
            )

            NavSheet(isNavSheetOpen, { isNavSheetOpen = false }, onNavigateToSettings)
        }
    }
}