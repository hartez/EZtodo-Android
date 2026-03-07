package com.ezhart.todotxtandroid.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import com.ezhart.todotxtandroid.viewmodels.SettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    // TODO We navigated to this, why isn't there a header of Settings and a back button?

    val lifecycleOwner = LocalLifecycleOwner.current

    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val context = LocalContext.current

    val isSignedIn = settingsViewModel.isSignedIn
    val accountName by settingsViewModel.accountName.collectAsStateWithLifecycle("")
    val accountEmail by settingsViewModel.accountEmail.collectAsStateWithLifecycle("")
    val todoPath by settingsViewModel.todoPath.collectAsStateWithLifecycle("")

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher


    TodotxtAndroidTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        // Back button icon wrapped in IconButton
                        IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back" // Accessibility label
                            )
                        }
                    })},
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) { innerPadding ->

            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                SectionTitle("Account")

                if (isSignedIn) {
                    Text(accountName)
                    Text(accountEmail)

                    // TODO these buttons are hideous, find a nicer style
                    Button(onClick = { settingsViewModel.signOut() }) {
                        Text("Sign Out")
                    }

                } else {
                    Button(onClick = { settingsViewModel.beginSignIn(context) }) {
                        Text("Sign In")
                    }
                }

                SectionTitle("Data/Sync")

                // TODO Background sync frequency
                // TODO Sync when opened

                SettingDialog(
                    "Task File",
                    value = todoPath
                ) {
                    PathDialog(
                        onDismissRequest = it,
                        path = todoPath,
                        onConfirmation = { t ->
                            settingsViewModel.updateTodoPath(t)
                        })
                }

                SectionTitle("About")

                SettingItem(
                    title = "Version",
                    value = "0.1.0"
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    value: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title) // TODO use theme color and text size
        value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SettingDialog(title: String, value: String? = null, content: @Composable (() -> Unit) -> Unit) {

    val openDialog = remember { mutableStateOf(false) }

    // TODO Change this to be two rows
    Row(
        modifier = Modifier
            .clickable() {
                openDialog.value = true
            }
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title) // TODO use theme color and text size
        value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
    }

    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            content { openDialog.value = false }
        }
    }
}

// TODO need a dialog for selecting sync frequencies