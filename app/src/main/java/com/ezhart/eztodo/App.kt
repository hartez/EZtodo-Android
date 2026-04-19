package com.ezhart.eztodo

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ezhart.eztodo.data.SettingsRepository
import com.ezhart.eztodo.data.TaskFileService
import com.ezhart.eztodo.dropbox.DropboxService
import com.ezhart.eztodo.dropbox.SyncDataStorage
import com.ezhart.eztodo.ui.SettingsScreen
import com.ezhart.eztodo.ui.TaskListScreen
import kotlinx.serialization.Serializable

@Serializable
data object Tasks

@Serializable
object Settings

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Tasks) {
        composable<Tasks> {
            TaskListScreen { navController.navigate(route = Settings) }
        }

        composable<Settings> {
            SettingsScreen()
        }
    }
}

class EZtodoApplication : Application() {
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val syncData: SyncDataStorage by lazy { SyncDataStorage(this) }
    val taskFileService: TaskFileService by lazy {
        TaskFileService(
            this,
            settingsRepository,
            syncData
        )
    }
    val dropboxService: DropboxService by lazy {
        DropboxService(
            this,
            settingsRepository,
            syncData
        )
    }
}