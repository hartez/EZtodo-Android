package com.ezhart.eztodo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.eztodo.ui.theme.Dimensions.ToolBarSafeBottomPadding
import com.ezhart.eztodo.ui.theme.DynamicTheme
import com.ezhart.eztodo.viewmodels.TasksViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(onNavigateToSettings: () -> Unit) {
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory)
    val scope = rememberCoroutineScope()

    val uiState by viewModel.taskListUIState.collectAsStateWithLifecycle()
    val editorUIState by viewModel.editorUIState.collectAsStateWithLifecycle()
    val detailsDialogUIState by viewModel.detailsDialogUIState.collectAsStateWithLifecycle()
    val messageUIState = viewModel.messageUIState

    var isFilterSheetOpen by remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadTasksAtStartup()
    }

    LaunchedEffect(messageUIState) {
        if (messageUIState.pending) {
            scope.launch {
                val result = snackBarHostState
                    .showSnackbar(
                        message = messageUIState.text,
                        actionLabel = messageUIState.actionLabel,
                        duration = messageUIState.duration,
                        withDismissAction = messageUIState.duration == SnackbarDuration.Indefinite
                    )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        messageUIState.action?.invoke()
                        messageUIState.onDismiss()
                    }

                    SnackbarResult.Dismissed -> {
                        messageUIState.onDismiss()
                    }
                }
            }
        }
    }

    BackHandler(!WindowInsets.isImeVisible && uiState.shouldHandleBackNavigation) {
        // TODO Hitting the back button while the text filter editor is open should switch back to
        // the default app bar. But the viewModel doesn't know about the search bar state, so we'd need
        // to add that to the taskListUIState
        viewModel.back()
    }

    DynamicTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.padding(bottom = ToolBarSafeBottomPadding).imePadding(),
                    snackbar = {
                        Snackbar(it, modifier = Modifier.padding(horizontal = 32.dp))
                })
            }
        ) { scaffoldPadding ->

            Box(
                modifier = Modifier.fillMaxSize().consumeWindowInsets(scaffoldPadding)
                    .padding(scaffoldPadding)
            ) {
                PullToRefreshBox(
                    isRefreshing = viewModel.isRefreshing,
                    onRefresh = {
                        viewModel.refreshTasks()
                    }
                ) {
                    TaskList(
                        uiState.filteredTasks,
                        uiState.headerText,
                        subHeaderText = uiState.subHeaderText,
                        { viewModel.selectTask(it) },
                        onToggleCompleted = {
                            viewModel.toggleCompleted(it)
                        },
                        onEdit = {
                            viewModel.selectTask(it, false)
                            viewModel.editSelectedTask()
                        }
                    )
                }

                if(!editorUIState.isOpen) {
                    // TODO a nicer way to handle this would be to animate the toolbar offscreen while editing
                    TaskListToolbar(
                        showFilters = { isFilterSheetOpen = true },
                        onNavigateToSettings = onNavigateToSettings,
                        onRefresh = { viewModel.refreshTasks() },
                        onCreateTask = { viewModel.editNewTask() },
                        viewModel.textFilterEditor,
                        modifier = Modifier.align(Alignment.BottomCenter).imePadding()
                    )
                }
            }

            FiltersSheet(
                uiState.allProjects,
                uiState.allContexts,
                isFilterSheetOpen,
                { isFilterSheetOpen = false },
                onUpdateFilter = viewModel::updateFilter,
                uiState.filter
            )

            TaskEditor(
                editorUIState,
                {
                    viewModel.closeEditor()
                },
                {
                    viewModel.commitTaskChanges(it)
                },
                viewModel::listTagsSelections
            )

            if (detailsDialogUIState.isOpen) {
                Dialog(detailsDialogUIState.onDismissRequest) {
                    DetailsDialog(detailsDialogUIState)
                }
            }
        }
    }
}
