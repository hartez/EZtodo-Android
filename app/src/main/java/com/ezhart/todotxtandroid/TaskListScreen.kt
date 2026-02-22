package com.ezhart.todotxtandroid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ezhart.todotxtandroid.data.Task
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme

@Composable
fun TaskListScreen(onNavigateToSettings: () -> Unit) {
    val t = generateFakeTasks(100)

    var showFilterSheet by remember { mutableStateOf(false) }
    var showNavSheet by remember { mutableStateOf(false) }

    TodotxtAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { AppBar({showFilterSheet = true}, {showNavSheet = true}) }
        ) { innerPadding ->
            TaskList(
                t, { t -> Unit },
                modifier = Modifier.padding(innerPadding)
            )

            FiltersSheet(showFilterSheet, {showFilterSheet = false})
            NavSheet(showNavSheet, {showNavSheet = false}, onNavigateToSettings)
        }
    }
}

fun generateFakeTasks(count: Int) : List<Task>{
    val x = mutableListOf<Task>()
    for (n in 0..count){
        x.add(Task("Task $n"))
    }

    return x
}