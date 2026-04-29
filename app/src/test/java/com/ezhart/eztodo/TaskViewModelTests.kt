package com.ezhart.eztodo

import com.ezhart.eztodo.data.AllTasksFilter
import com.ezhart.eztodo.data.ReadTaskListResult
import com.ezhart.eztodo.data.SettingsRepository
import com.ezhart.eztodo.data.Task
import com.ezhart.eztodo.data.TaskFileService
import com.ezhart.eztodo.dropbox.DropboxService
import com.ezhart.eztodo.viewmodels.TasksViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskViewModelTests {
    @Test
    fun empty_tasks_always_filtered() = runTest {
        val dropboxService = mockk<DropboxService>()
        val settingsRepository = mockk<SettingsRepository>()

        val fileService = mockk<TaskFileService>()
        val tasks = listOf(Task("task 1"), Task(""), Task("task 2"))

        coEvery { fileService.loadTasksFromStorage() } returns ReadTaskListResult.Success(tasks)

        val viewModel = TasksViewModel(fileService, dropboxService, settingsRepository)
        viewModel.loadTasks()

        // We skip the initial state in the flow because it will always be empty
        val taskListUIState = viewModel.taskListUIState.drop(1).first()
        val filteredTasks = taskListUIState.filteredTasks

        // "All Tasks" is the default filter
        assertEquals(AllTasksFilter, taskListUIState.filter)

        // The empty task should not be in the filtered list, even if the filter is "All"
        assertEquals(2, filteredTasks.count())

        assertEquals("task 1", filteredTasks[0].body)
        assertEquals("task 2", filteredTasks[1].body)
    }
}