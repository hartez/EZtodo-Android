package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezhart.eztodo.data.AllTasksFilter
import com.ezhart.eztodo.data.CompletedFilter
import com.ezhart.eztodo.data.ContextFilter
import com.ezhart.eztodo.data.DueFilter
import com.ezhart.eztodo.data.PendingFilter
import com.ezhart.eztodo.data.ProjectFilter
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.viewmodels.FilterSheetUIState
import kotlinx.coroutines.launch

enum class ExpandedOption {
    None, Projects, Contexts
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSheet(
    uiState: FilterSheetUIState, onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var expandedOption by remember { mutableStateOf(ExpandedOption.None) }

    val (contexts, projects, selectedFilter, onUpdateFilter) = uiState

    ModalBottomSheet(
        onDismissRequest = { onClose() }, sheetState = sheetState
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {

            MenuOption("All Tasks", Icons.Outlined.Inbox, selectedFilter is AllTasksFilter) {
                scope.launch {
                    sheetState.hide()
                    onUpdateFilter(AllTasksFilter)
                    expandedOption = ExpandedOption.None
                    onClose()
                }
            }

            MenuOption("Due", Icons.Outlined.Timer, selectedFilter is DueFilter) {
                scope.launch {
                    sheetState.hide()
                    onUpdateFilter(DueFilter)
                    expandedOption = ExpandedOption.None
                    onClose()
                }
            }

            MenuOption(
                "Pending",
                Icons.Outlined.CheckBoxOutlineBlank,
                selectedFilter is PendingFilter
            ) {
                scope.launch {
                    sheetState.hide()
                    onUpdateFilter(PendingFilter)
                    expandedOption = ExpandedOption.None
                    onClose()
                }
            }

            MenuOption("Completed", Icons.Outlined.Check, selectedFilter is CompletedFilter) {
                scope.launch {
                    sheetState.hide()
                    onUpdateFilter(CompletedFilter)
                    expandedOption = ExpandedOption.None
                    onClose()
                }
            }

            HorizontalDivider()

            ExpandingOption(
                "Projects", expandedOption == ExpandedOption.Projects, {
                    expandedOption = if (expandedOption == ExpandedOption.Projects) {
                        ExpandedOption.None
                    } else {
                        ExpandedOption.Projects
                    }
                }, projects, selectedOption(uiState.selectedFilter)
            ) {

                scope.launch {
                    sheetState.hide()
                    onUpdateFilter(ProjectFilter(it))
                    onClose()
                }

            }

            ExpandingOption(
                "Contexts", expandedOption == ExpandedOption.Contexts, {
                    expandedOption = if (expandedOption == ExpandedOption.Contexts) {
                        ExpandedOption.None
                    } else {
                        ExpandedOption.Contexts
                    }
                }, contexts, selectedOption(selectedFilter)
            ) {

                scope.launch {
                    sheetState.hide()
                    onUpdateFilter(ContextFilter(it))
                    onClose()
                }

            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

fun selectedOption(filter: Any): String? {
    return when (filter) {
        is ProjectFilter -> filter.project
        is ContextFilter -> filter.context
        else -> null
    }
}

@Preview(name = "Filter Sheet Light")
@Preview("Filter Sheet Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun FilterSheetPreview() {

    val uiState = FilterSheetUIState()

    AppTheme {
        Surface {
            FiltersSheet(
                uiState, {})
        }
    }
}

