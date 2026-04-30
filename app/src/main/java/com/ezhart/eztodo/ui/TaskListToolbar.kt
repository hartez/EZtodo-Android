package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults.VibrantFloatingActionButton
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.ezhart.eztodo.ui.theme.AppTheme

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun TaskListToolbar(
    showFilters: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onRefresh: () -> Unit,
    onCreateTask: () -> Unit,
    filterTextState: TextFieldState,
    modifier: Modifier = Modifier,
    inFilterMode: Boolean = false
) {
    var isInTextFilterMode by remember { mutableStateOf(inFilterMode) }
    val filterBarFocusRequester = remember { FocusRequester() }
    var isToolbarMenuOpen by remember { mutableStateOf(false) }

    HorizontalFloatingToolbar(
        expanded = true,
        floatingActionButtonPosition = FloatingToolbarHorizontalFabPosition.Start,
        floatingActionButton = {

            VibrantFloatingActionButton(
                onClick = {
                    onCreateTask()
                }
            ) {
                Icon(Icons.Outlined.Add, "Add Task")
            }

        },
        modifier = modifier
    ) {

        if (isInTextFilterMode) {

            TextField(

                state = filterTextState,
                placeholder = { Text("Search…") },
                lineLimits = TextFieldLineLimits.SingleLine,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),

                leadingIcon = {
                    IconButton(onClick = {
                        filterTextState.clearText()
                        isInTextFilterMode = false
                    }
                    ) {
                        Icon(Icons.Outlined.SearchOff, contentDescription = "Close Search")
                    }
                },

                trailingIcon = {
                    IconButton(onClick = { filterTextState.clearText() }
                    ) {
                        Icon(Icons.Outlined.Clear, contentDescription = "Clear")
                    }
                },
                modifier = Modifier.focusRequester(filterBarFocusRequester)
            )

            LaunchedEffect(isInTextFilterMode) {
                if (isInTextFilterMode) {
                    filterBarFocusRequester.requestFocus()
                } else {
                    filterBarFocusRequester.freeFocus()
                }
            }

        } else {
            IconButton(onClick = { showFilters() }) {
                Icon(Icons.Outlined.FilterAlt, contentDescription = "Filters")
            }

            IconButton(
                onClick = { isInTextFilterMode = true },
            ) {
                Icon(Icons.Outlined.Search, contentDescription = "Search")
            }

            IconButton(
                onClick = {
                    isToolbarMenuOpen = !isToolbarMenuOpen
                },
            ) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More")

                ToolbarMenu(
                    isToolbarMenuOpen,
                    { isToolbarMenuOpen = false },
                    onNavigateToSettings = onNavigateToSettings,
                    onRefresh = onRefresh
                )
            }
        }
    }
}

@Preview(name = "TaskListToolbar Light", showBackground = true)
@Preview("TaskListToolbar Dark", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AppBarPreview() {
    val textFilterEditor = TextFieldState()

    AppTheme {
        TaskListToolbar(
            {},
            {},
            {},
            {},
            textFilterEditor,
        )
    }
}

@Preview(name = "TaskListToolbar Light ShowTextFilter", showBackground = true)
@Preview("TaskListToolbar Dark ShowTextFilter", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TextFilterContentPreview() {
    val textFilterEditor = TextFieldState()

    AppTheme {
        TaskListToolbar(
            {},
            {},
            {},
            {},
            textFilterEditor,
            inFilterMode = true
        )
    }
}

