package com.ezhart.eztodo.viewmodels

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ezhart.eztodo.EZtodoApplication
import com.ezhart.eztodo.TAG
import com.ezhart.eztodo.data.AllTasksFilter
import com.ezhart.eztodo.data.CompletedFilter
import com.ezhart.eztodo.data.ContextFilter
import com.ezhart.eztodo.data.DueFilter
import com.ezhart.eztodo.data.Filter
import com.ezhart.eztodo.data.PendingFilter
import com.ezhart.eztodo.data.ProjectFilter
import com.ezhart.eztodo.data.ReadTaskListResult
import com.ezhart.eztodo.data.SettingsRepository
import com.ezhart.eztodo.data.Task
import com.ezhart.eztodo.data.TaskFileService
import com.ezhart.eztodo.dropbox.DropboxService
import com.ezhart.eztodo.dropbox.SyncResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.time.LocalDate

class TasksViewModel(
    private val taskFileService: TaskFileService,
    private val dropboxService: DropboxService,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    var startupLoaded = false

    var isRefreshing by mutableStateOf(false)
        private set

    private val selectedTask = MutableStateFlow<Task?>(null)

    var messageUIState by mutableStateOf(MessageUIState())
        private set

    val textFilterEditor = TextFieldState()

    private var tasks: MutableStateFlow<MutableList<Task>> = MutableStateFlow(mutableStateListOf())
    private val filter = MutableStateFlow<Filter>(AllTasksFilter)
    private val textFilter = snapshotFlow { textFilterEditor.text }

    val taskListUIState: StateFlow<TaskListUIState> =
        combine(filter, tasks, textFilter) { filter, tasks, textFilter ->
            TaskListUIState(
                filterTasks(tasks, filter, textFilter),
                filter,
                textFilter
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListUIState()
        )

    val filterSheetUIState: StateFlow<FilterSheetUIState> =
        combine(filter, tasks) { filter, tasks ->
            FilterSheetUIState(
                listContexts(),
                listProjects(),
                filter,
                this::updateFilter
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = FilterSheetUIState()
        )

    private val taskCreator = TextFieldState()
    private val taskEditor = TextFieldState()

    private val taskCreatorText = snapshotFlow { taskCreator.text }
    private val taskCreatorSelection = snapshotFlow { taskCreator.selection }

    private val taskEditorText = snapshotFlow { taskEditor.text }
    private val taskEditorSelection = snapshotFlow { taskEditor.selection }

    val taskEditorUIState: StateFlow<TaskEditorUIState> =
        combine(taskEditorText, taskEditorSelection) {
            getEditorWithSuggestions(taskEditor)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = TaskEditorUIState(
                taskEditor
            )
        )

    val taskCreatorUIState: StateFlow<TaskEditorUIState> =
        combine(taskCreatorText, taskCreatorSelection) {
            getEditorWithSuggestions(taskCreator)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = TaskEditorUIState(
                taskCreator
            )
        )

    val detailsDialogUIState: StateFlow<DetailsDialogUIState> =
        selectedTask.map { selectedTask ->
            DetailsDialogUIState(
                selectedTask,
                getNextTask(),
                getPreviousTask(),
                onUpdateSelectedTask = ::selectTask,
                { toggleCompleted(selectedTask) }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = DetailsDialogUIState()
        )

    fun listTagSelections(task: String): Map<String, Boolean> {
        val selectedContexts = Task.parseContexts(task)
        val selectedProjects = Task.parseProjects(task)

        val all = listProjects() + listContexts()

        return all.associateWith { tag ->
            (selectedContexts.contains(tag) || selectedProjects.contains(tag))
        }
    }

    fun selectTask(task: Task) {
        selectedTask.value = task
        if (selectedTask.value != null) {
            val taskText = Task.removeCreatedDate(selectedTask.value!!.task)
            taskEditor.setTextAndPlaceCursorAtEnd(taskText)
        }
    }

    private fun getNextTask(): Task? {
        val currentTask = selectedTask.value ?: return null
        val tasks = taskListUIState.value.filteredTasks

        val currentIndex = tasks.indexOf(currentTask)

        if (currentIndex >= tasks.count() - 1) {
            return tasks.first()
        }

        return tasks[currentIndex + 1]
    }

    private fun getPreviousTask(): Task? {
        val currentTask = selectedTask.value ?: return null
        val tasks = taskListUIState.value.filteredTasks

        val currentIndex = tasks.indexOf(currentTask)

        if (currentIndex == 0) {
            return tasks.last()
        }

        return tasks[currentIndex - 1]
    }

    private fun clearTaskSelection() {
        selectedTask.value = null
    }

    fun updateFilter(newFilter: Filter) {
        // If there's a selected task, we should clear that
        clearTaskSelection()

        // If there's still a pre-filled context or project in the new task editor, clean that up
        // as long as that's all there is. If there's other data from an unsubmitted Task, just
        // leave it alone
        if (taskCreator.text.trim() == getNewTaskPrefill()) {
            taskCreator.clearText()
            applyNewTaskPrefill()
        }

        filter.value = newFilter
    }

    fun back() {
        unwindFilter()
    }

    private fun getNewTaskPrefill(): String {
        return when (val filter = filter.value) {
            is ContextFilter -> filter.context
            is ProjectFilter -> filter.project
            else -> ""
        }
    }

    private fun applyNewTaskPrefill() {
        val preset = getNewTaskPrefill()

        if (preset.isNotEmpty()) {
            taskCreator.edit {
                this.insert(0, " $preset")
                this.placeCursorBeforeCharAt(0)
            }
        }
    }

    fun toggleCompleted(task: Task?) {
        if (task == null) {
            return
        }

        val wasSelected = task == selectedTask.value

        val message = when (task.completed) {
            true -> "Task marked pending"
            false -> "Task marked complete"
        }

        val undoTaskText = task.task

        val updateTaskText =
            if (task.completed) {
                Task.markPending(task.task)
            } else {
                Task.markCompleted(task.task, LocalDate.now())
            }

        val updatedTask = editTask(task, updateTaskText)

        if (wasSelected) {
            selectTask(updatedTask)
        }

        showActionAlert(message, "Undo") { editTask(updatedTask, undoTaskText) }
    }

    /* For the moment, we're just ignoring blank task updates entirely.
    In the future it may make sense to consider them invalid input in the UI
    or to use "blanking" a task as a way to delete it. But for now I don't have a
    string opinion on which it should be, since it's not part of my usage pattern. */

    fun submitTask() {
        val toAdd = taskCreator.text.toString()
        taskCreator.clearText()
        applyNewTaskPrefill()

        if (toAdd.isBlank()) {
            return
        }

        addTask(toAdd)
    }

    fun commitTaskChanges() {
        if (selectedTask.value == null) {
            Log.e(
                TAG,
                "commitTaskChanges was called, but the selected task was null. This... shouldn't happen."
            )
            return;
        }

        val oldTask = selectedTask.value!!
        val updatedTask = taskEditor.text.toString()

        taskEditor.clearText()

        clearTaskSelection()

        if (updatedTask.isBlank()) {
            return
        }

        editTask(oldTask, updatedTask)
    }

    fun showAlert(message: String) {
        messageUIState = MessageUIState(
            pending = true,
            message,
            { clearAlert() }
        )
    }

    fun showError(message: String) {
        messageUIState = MessageUIState(
            pending = true,
            message,
            { clearAlert() },
            duration = SnackbarDuration.Indefinite
        )
    }

    fun showActionAlert(message: String, actionLabel: String, action: () -> Unit) {
        messageUIState = MessageUIState(
            pending = true,
            text = message,
            actionLabel = actionLabel,
            action = action,
            onDismiss = { clearAlert() }
        )
    }

    fun clearAlert() {
        messageUIState = MessageUIState(pending = false)
    }

    fun loadTasksAtStartup() {
        viewModelScope.launch {
            // Load from local first, then check for remote (otherwise we have a blank task list
            // while we wait)
            loadTasks()

            if (!startupLoaded) {
                startupLoaded = true
                refreshTasks(settingsRepository.syncOnStart.first())
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            when (val result = taskFileService.loadTasksFromStorage()) {
                is ReadTaskListResult.Success -> tasks.value = result.tasks.toMutableList()
                is ReadTaskListResult.Error -> {
                    tasks.value = mutableListOf()

                    when (result.e) {
                        is FileNotFoundException -> Log.e(TAG, result.e.toString())
                        else -> showError("Error reading tasks from local storage: ${result.e.message}")
                    }
                }
            }
        }
    }

    fun refreshTasks(shouldSync: Boolean = true) {
        viewModelScope.launch {
            isRefreshing = true

            if (shouldSync) {
                when (val syncResult = dropboxService.sync()) {
                    is SyncResult.NotConnected -> {
                        Log.i(TAG, syncResult.message)
                        showAlert(syncResult.message)
                    }

                    is SyncResult.NotAuthenticated -> {
                        Log.i(TAG, syncResult.message)
                        showAlert(syncResult.message)
                    }

                    is SyncResult.Success -> {
                        Log.i(TAG, syncResult.message)
                    }

                    is SyncResult.Conflict -> showAlert(syncResult.message)
                    is SyncResult.Error -> showError(syncResult.e.message.toString())
                }
            }

            loadTasks()

            // TODO this is a hack, got to figure out how to fix this
            // if the update is too fast, the refreshing state will get stuck
            delay(100)

            isRefreshing = false
        }
    }

    private fun unwindFilter() {
        if (!textFilterEditor.text.isEmpty()) {
            textFilterEditor.clearText()
            return
        }

        if (filter != AllTasksFilter) {
            updateFilter(AllTasksFilter)
        }
    }

    private fun filterTasks(
        tasks: List<Task>,
        filter: Filter,
        textFilter: CharSequence
    ): List<Task> {
        var result = when (filter) {
            is ProjectFilter -> tasks.filter { t -> t.projects.contains(filter.project) }
            is ContextFilter -> tasks.filter { t -> t.contexts.contains(filter.context) }
            is DueFilter -> tasks.filter { t -> t.dueDate != null }
            is PendingFilter -> tasks.filter { t -> !t.completed }
            is CompletedFilter -> tasks.filter { t -> t.completed }
            else -> tasks
        }

        if (!textFilter.isBlank()) {
            result = result.filter { t -> t.body.contains(textFilter, ignoreCase = true) }
        }

        // TODO Filter out blank lines (is that a totally blank Task? There might accidentally be
        // blank lines in the source task file; instead of crashing, we could have a property on
        // Task like "isEmpty" and then ignore it here. Probably ignore it when we write the task
        // file back, too.

        return result.distinct().sortedWith(compareBy(Task::taskPriority, Task::completed))
    }

    private fun listProjects(): List<String> {
        return tasks.value.flatMap { t -> t.projects }.distinct().sorted()
    }

    private fun listContexts(): List<String> {
        return tasks.value.flatMap { t -> t.contexts }.distinct().sorted()
    }

    private fun getEditorWithSuggestions(textFieldState: TextFieldState): TaskEditorUIState {
        // If text is selected then don't try to suggest completions
        if (!textFieldState.selection.collapsed) {
            return TaskEditorUIState(textFieldState)
        }

        val text = textFieldState.text.toString()
        val index = textFieldState.selection.end

        val partialTag = findPartialTag(text, index)

        if (partialTag.isBlank()) {
            return TaskEditorUIState(textFieldState)
        }

        val currentContexts = Task.parseContexts(text)
        val currentProjects = Task.parseProjects(text)

        val suggestions = getPartialTagMatches(partialTag).filter {
            !currentProjects.contains(it) && !currentContexts.contains(it)
        }

        if (!suggestions.any()) {
            return TaskEditorUIState(textFieldState)
        }

        val range = IntRange(index - partialTag.length, index - 1)

        return TaskEditorUIState(textFieldState, suggestions, range)
    }

    private fun findPartialTag(text: String, index: Int): String {
        if (text.isEmpty() || index <= 0) {
            return ""
        }

        val iterator = StringCharacterIterator(text)
        iterator.index = index - 1
        var current = iterator.current()

        val partial = StringBuilder()

        while (current != CharacterIterator.DONE && !current.isWhitespace()) {
            partial.insert(0, current)
            current = iterator.previous()
        }

        return if (partial.isEmpty()) {
            ""
        } else if (partial[0] == '@' || partial[0] == '+') {
            partial.toString()
        } else {
            ""
        }
    }

    private fun getPartialTagMatches(partialTag: String): List<String> {
        return (listProjects() + listContexts()).filter { it.startsWith(partialTag) }
    }

    private fun addTask(task: String) {
        // Make sure the created date is in the task
        val taskText = Task.insertCreatedDate(task, LocalDate.now())

        tasks.update {
            tasks.value.toMutableList().apply {
                this.add(Task(taskText))
            }
        }

        viewModelScope.launch {
            taskFileService.writeTasksToStorage(tasks.value)
        }

        showAlert("Task created")
    }

    private fun editTask(task: Task, updated: String): Task {
        val taskText =
            when (val created = task.createdDate) {
                null -> updated
                else -> Task.insertCreatedDate(updated, created)
            }

        val updatedTask = Task(taskText)

        tasks.update {
            tasks.value.toMutableList().apply {
                this[this.indexOf(task)] = updatedTask
            }
        }

        viewModelScope.launch {
            taskFileService.writeTasksToStorage(tasks.value)
        }

        return updatedTask
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val dropboxService =
                    (this[APPLICATION_KEY] as EZtodoApplication).dropboxService
                val taskFileService =
                    (this[APPLICATION_KEY] as EZtodoApplication).taskFileService
                val settingsRepository =
                    (this[APPLICATION_KEY] as EZtodoApplication).settingsRepository
                TasksViewModel(
                    taskFileService = taskFileService,
                    dropboxService = dropboxService,
                    settingsRepository = settingsRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}