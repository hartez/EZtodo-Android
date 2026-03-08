package com.ezhart.todotxtandroid.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import com.ezhart.todotxtandroid.viewmodels.TasksViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(onNavigateToSettings: () -> Unit) {

    val tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory)

    val uiState by tasksViewModel.uiState.collectAsStateWithLifecycle()

    // TODO hoist this into a separate class for maintaining sheet state
    // with open and close methods
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var isMenuSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tasksViewModel.loadTasks()
    }

    TodotxtAndroidTheme {
        Scaffold(
            contentWindowInsets = WindowInsets.statusBars,
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                AppBar(
                    { isFilterSheetOpen = true },
                    { isMenuSheetOpen = true }
                )
            }
        ) { innerPadding ->

            PullToRefreshBox(
                isRefreshing = tasksViewModel.isRefreshing,
                onRefresh = {
                    tasksViewModel.loadTasks()
                }, modifier = Modifier.padding(innerPadding)
            ) {
                TaskList(
                    uiState.filteredTasks, uiState.filterLabel,
                    { },
                    modifier = Modifier
                        //.padding(32.dp)
                )
            }

            FiltersSheet(
                uiState.allProjects,
                uiState.allContexts,
                isFilterSheetOpen,
                { isFilterSheetOpen = false },
                onUpdateFilter = tasksViewModel::updateFilter,
                uiState.filter
            )

            MenuSheet(
                isMenuSheetOpen,
                { isMenuSheetOpen = false },
                onNavigateToSettings,
                { tasksViewModel.loadTasks() })

            if (tasksViewModel.alert != null) {
                BasicAlertDialog({ tasksViewModel.clearAlert() }) {
                    Text(tasksViewModel.alert ?: "")
                }
            }
        }
    }
}