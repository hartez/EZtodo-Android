package com.ezhart.eztodo.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.ezhart.eztodo.data.Task
import com.ezhart.eztodo.data.TaskPriority

data class TaskEditorUIState(
    val textEditorState: TextFieldState,
    val tagSuggestions: List<String> = listOf(),
    val tagSuggestionTarget: IntRange = IntRange(0,0),
    //val tagSuggestionsDismissed: Boolean = false
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

    fun acceptSuggestion(suggestion:String){
        val old = textEditorState.text
        val new = old.replaceRange(tagSuggestionTarget, "$suggestion ").toString()
        textEditorState.setTextAndPlaceCursorAtEnd(new)
    }
}




