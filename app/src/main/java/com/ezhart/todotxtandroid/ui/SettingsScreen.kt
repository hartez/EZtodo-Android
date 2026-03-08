package com.ezhart.todotxtandroid.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.todotxtandroid.ui.theme.Dimensions
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import com.ezhart.todotxtandroid.viewmodels.SettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

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
                        IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    })
            },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                .fillMaxSize()
                .safeContentPadding()
        ) { innerPadding ->

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                SectionTitle("Account")

                if (isSignedIn) {
                    InfoItem(
                        title = accountName,
                        value = accountEmail
                    )

                    SettingButton("Sign Out") {
                        settingsViewModel.signOut()
                    }
                } else {
                    SettingButton("Sign In") {
                        settingsViewModel.beginSignIn(context)
                    }
                }

                HorizontalDivider()

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

                HorizontalDivider()

                SectionTitle("About")

                InfoItem(
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
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.SettingPadding)
    )
}

@Composable
fun InfoItem(
    title: String,
    value: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.SettingPadding)
    ) {
        Row {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row {
            value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingDialog(title: String, value: String? = null, content: @Composable (() -> Unit) -> Unit) {

    val openDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clickable {
                openDialog.value = true
            }
            .fillMaxWidth()
            .padding(Dimensions.SettingPadding)
    )
    {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row {
            value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            content { openDialog.value = false }
        }
    }
}

@Composable
fun SettingButton(
    text: String,
    onClick: () -> Unit
){
    Text(
        text,
        modifier = Modifier
            .clickable{onClick()}
            .fillMaxWidth()
            .padding(Dimensions.SettingPadding)
    )
}

// TODO need a dialog for selecting sync frequencies

@Preview(name = "Section Title Light")
@Preview("Section Title Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SectionTitlePreview() {
    TodotxtAndroidTheme {
        Surface {
            SectionTitle(
                "Data/Sync"
            )
        }
    }
}

@Preview(name = "Setting Dialog Light")
@Preview("Setting Dialog Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingDialogPreview() {
    TodotxtAndroidTheme {
        Surface {
            SettingDialog(
                "Change Task File",
                value = "/todo/todo.txt"
            ) {}
        }
    }
}

@Preview(name = "Info Item Light")
@Preview("Info Item Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun InfoItemPreview() {
    TodotxtAndroidTheme {
        Surface {
            InfoItem(
                "Version",
                value = "0.1.0"
            )
        }
    }
}

@Preview(name = "Setting Button Light")
@Preview("Setting Button Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingButtonPreview() {
    TodotxtAndroidTheme {
        Surface {
            SettingButton(text = "Sign Out", onClick = {})
        }
    }
}