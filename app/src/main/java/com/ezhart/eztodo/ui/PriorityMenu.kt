package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.ezhart.eztodo.data.NoPriority
import com.ezhart.eztodo.data.TaskPriority
import com.ezhart.eztodo.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PriorityMenu(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenuPopup(
        expanded = true,
        properties = PopupProperties(focusable = false),
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)

    ) {
        Row {
            Text(
                text = "Select Priority",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(
                        16.dp,
                        8.dp
                    ) // TODO Put this padding and the tags dialog padding into dimensions
            )
        }

        HorizontalDivider()

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            val selectedContainerColor =
                MenuDefaults.selectableItemColors().selectedContainerColor
            val selectedTextColor = MenuDefaults.selectableItemColors().selectedTextColor

            for (priority in TaskPriority.options) {
                DropdownMenuItem(
                    text = {
                        Text(
                            priority.display("None"),
                            color =
                                when (priority) {
                                    selectedPriority -> {
                                        selectedTextColor
                                    }

                                    else -> {
                                        Color.Unspecified
                                    }
                                }
                        )
                    },
                    onClick = { onPrioritySelected(priority) },
                    modifier = Modifier.background(
                        color =
                            when (priority) {
                                selectedPriority -> {
                                    selectedContainerColor
                                }

                                else -> {
                                    Color.Unspecified
                                }
                            }
                    )
                )
            }
        }
    }
}

@Preview(name = "Priority Dialog Light")
@Preview("Priority Dialog Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PriorityMenuPreview() {
    AppTheme {
        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                PriorityMenu(
                    NoPriority,
                    {}, {}
                )
            }
        }
    }
}