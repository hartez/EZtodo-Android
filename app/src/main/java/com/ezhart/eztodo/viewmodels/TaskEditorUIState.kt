package com.ezhart.eztodo.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.ezhart.eztodo.data.Task
import com.ezhart.eztodo.data.TaskPriority

enum class TaskEditorMode {
    Create,
    Edit
}

data class TaskEditorUIState(
    val mode: TaskEditorMode,
    val textEditorState: TextFieldState,
    // TODO autocomplete options from the flow go here
){
    fun setTags(tags: Map<String, Boolean>){
        textEditorState.setTextAndPlaceCursorAtEnd(
            Task.editTags(
                textEditorState.text.toString(),
                tags.filter { selection -> selection.value }
                    .map { selection -> selection.key })
        )
    }

    fun setPriority(taskPriority: TaskPriority){
        textEditorState.setTextAndPlaceCursorAtEnd(
            Task.editPriority(
                textEditorState.text.toString(),
                taskPriority
            )
        )
    }

    // TODO This is where the method that updates the text with the autocomplete selection goes
}


