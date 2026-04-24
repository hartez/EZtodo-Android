package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.ColorUtils
import com.ezhart.eztodo.data.Task
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.viewmodels.TaskEditorUIState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditor(
    editorState: TaskEditorUIState,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
    listTagsSelections: (String) -> Map<String, Boolean>
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val focusRequester = remember { FocusRequester() }

    var isPriorityDialogOpen by remember { mutableStateOf(false) }
    var isTagDialogOpen by remember { mutableStateOf(false) }

    //var tagSuggestionsDismissed : Boolean = false

    LaunchedEffect(sheetState.currentValue) {
        when (sheetState.currentValue) {
            SheetValue.Hidden -> focusRequester.freeFocus()
            SheetValue.Expanded -> focusRequester.requestFocus()
            SheetValue.PartiallyExpanded -> focusRequester.requestFocus()
        }
    }

    val containerColor = MaterialTheme.colorScheme.tertiaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiaryContainer
    val textEditorState = editorState.textEditorState

    val placeholderColor = Color(
        ColorUtils.blendARGB(
            MaterialTheme.colorScheme.onTertiaryContainer.toArgb(),
            Color.White.toArgb(),
            0.2f
        )
    )



    ModalBottomSheet(
        onDismissRequest = { onClose() },
        sheetState = sheetState,
        dragHandle = {},
        containerColor = containerColor,
    ) {



        Column(modifier = Modifier.padding(top = 16.dp)) {

            Row {

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    TextField(
                        state = textEditorState,
                        placeholder = {
                            Text(
                                "enter task",
                                color = placeholderColor
                            )
                        },
                        lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),

                        modifier = Modifier.focusRequester(focusRequester)
                    )

//                    if (editorState.tagSuggestions.any()) {
//                        TagAutocompleteMenu(
//                            editorState.tagSuggestions,
//                            {  },
//                            onSelect = {
//                                editorState.acceptSuggestion(it)
//                            }
//                        )
//                    }
                }

                IconButton(
                    onClick = { onSubmit() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PostAdd, contentDescription = "Create"
                    )
                }
            }

            HorizontalDivider()

            Row {

                IconButton(
                    onClick = { isPriorityDialogOpen = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = "Priority"
                    )
                }

                IconButton(
                    onClick = { isTagDialogOpen = true },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = "Projects/Contexts"
                    )
                }

                IconButton(
                    onClick = {
                        textEditorState.setTextAndPlaceCursorAtEnd(
                            Task.editDueDate(
                                textEditorState.text.toString(),
                                LocalDate.now()
                            )
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Today, contentDescription = "Due"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isPriorityDialogOpen) {
                    Dialog(onDismissRequest = { isPriorityDialogOpen = false }) {
                        PriorityDialog(
                            Task.parsePriority(textEditorState.text.toString()),
                            onPrioritySelected = { taskPriority ->
                                editorState.setPriority(taskPriority)
                                isPriorityDialogOpen = false
                            }
                        )
                    }
                }

                if (isTagDialogOpen) {
                    Dialog(onDismissRequest = { isTagDialogOpen = false }) {
                        TagsDialog(
                            onDismissRequest = { isTagDialogOpen = false },
                            options = listTagsSelections(textEditorState.text.toString()),
                            onSubmit = {
                                editorState.setTags(it)
                                isPriorityDialogOpen = false
                            }
                        )
                    }
                }

            }
        }
    }
}

@Preview(name = "New Task Light")
@Preview("New Task Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NewTaskPreview() {

    val state = TaskEditorUIState(

        TextFieldState()
    )

    AppTheme {
        Surface {
            TaskEditor(
                state, {}, {}, { mapOf() }
            )
        }
    }
}

@Preview(name = "Edit Task Light")
@Preview("Edit Task Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EditTaskPreview() {

    val state = TaskEditorUIState(

        TextFieldState()
    )

    AppTheme {
        Surface {
            TaskEditor(
                state, {}, {}, { mapOf() }
            )
        }
    }
}