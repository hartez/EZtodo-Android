package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.ui.theme.Dimensions
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DueDateDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    var isDatePickerDialogOpen by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
            ) {
                Column {
                    Row {
                        Text(
                            text = "Select Due Date",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .padding(Dimensions.DialogHeadingPadding)
                        )
                    }

                    Column {

                        DropdownMenuItem(
                            text = { Text("Today") },
                            onClick = {
                                onDateSelected(LocalDate.now())
                                onDismissRequest()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Tomorrow") },
                            onClick = {
                                onDateSelected(LocalDate.now().plusDays(1))
                                onDismissRequest()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("This Weekend") },
                            onClick = {
                                onDateSelected(nextSaturday())
                                onDismissRequest()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Next Week") },
                            onClick = {
                                onDateSelected(nextMonday())
                                onDismissRequest()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Choose Date") },
                            onClick = {
                                isDatePickerDialogOpen = true
                            }
                        )
                    }
                }
            }

            if (isDatePickerDialogOpen) {

                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember {
                    derivedStateOf { datePickerState.selectedDateMillis != null }
                }

                DatePickerDialog(
                    onDismissRequest = {
                        isDatePickerDialogOpen = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                isDatePickerDialogOpen = false
                                val date = datePickerState.getSelectedDate()
                                if (date != null) {
                                    onDateSelected(date)
                                }
                                onDismissRequest()
                            },
                            enabled = confirmEnabled.value,
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { isDatePickerDialogOpen = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(
                        state = datePickerState
                    )
                }
            }
        }
    }
}

private fun nextMonday(): LocalDate {
    return nextDayOfWeek(DayOfWeek.MONDAY)
}

fun nextSaturday(): LocalDate {
    return nextDayOfWeek(DayOfWeek.SATURDAY)
}

fun nextDayOfWeek(day: DayOfWeek): LocalDate {
    var date = LocalDate.now()
    while (date.dayOfWeek != day) {
        date = date.plusDays(1)
    }

    return date
}

@Preview(name = "Due Date Menu Light")
@Preview("Due Date Menu Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DueDateDialogPreview() {
    AppTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                DueDateDialog(
                    {},
                    {},
                )
            }
        }
    }
}