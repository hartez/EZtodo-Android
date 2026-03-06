package com.ezhart.todotxtandroid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import com.ezhart.todotxtandroid.viewmodels.SettingsViewModel


@Composable
fun SettingsScreen() {

    val lifecycleOwner = LocalLifecycleOwner.current

    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val context = LocalContext.current

    val isSignedIn = settingsViewModel.isSignedIn
    val accountName by settingsViewModel.accountName.collectAsStateWithLifecycle("")
    val accountEmail by settingsViewModel.accountEmail.collectAsStateWithLifecycle("")
    val todoPath by settingsViewModel.todoPath.collectAsStateWithLifecycle("")

    TodotxtAndroidTheme {
        Scaffold(
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

                    Button(onClick = { settingsViewModel.signOut() }) {
                        Text("Sign Out")
                    }

                } else {
                    Button(onClick = { settingsViewModel.beginSignIn(context) }) {
                        Text("Sign In")
                    }
                }

                SectionTitle("Data/Sync")

                SettingDialog(
                    "todo.txt file path in Dropbox",
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
                    title = "TODO.txt Android",
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
        Text(text = title)
        value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SettingDialog(title: String, value: String? = null, content: @Composable (() -> Unit) -> Unit) {

    val openDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clickable() {
                openDialog.value = true
            }
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
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

@Composable
fun PathDialog(
    onDismissRequest: () -> Unit,
    path: String,
    onConfirmation: (String) -> Unit
) {
    // Draw a rectangle shape with rounded corners inside the dialog
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(375.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "Set todo file path in Dropbox"
            )

            val updated = remember { mutableStateOf(path) }
            TextField(value = updated.value, { updated.value = it })


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Dismiss")
                }
                TextButton(
                    onClick = {
                        onConfirmation(updated.value)
                        onDismissRequest()
                    },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}