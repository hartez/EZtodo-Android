package com.ezhart.eztodo.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.eztodo.dropbox.friendlyInterval
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.ui.theme.Dimensions
import com.ezhart.eztodo.ui.theme.DynamicTheme
import com.ezhart.eztodo.ui.theme.ThemeMode
import com.ezhart.eztodo.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen() {
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val isSignedIn = settingsViewModel.isSignedIn
    val accountName by settingsViewModel.accountName.collectAsStateWithLifecycle("")
    val accountEmail by settingsViewModel.accountEmail.collectAsStateWithLifecycle("")
    val todoPath by settingsViewModel.todoPath.collectAsStateWithLifecycle("")
    val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle(ThemeMode.System)
    val useDynamicColor by settingsViewModel.useDynamicColor.collectAsStateWithLifecycle(false)
    val syncOnStart by settingsViewModel.syncOnStart.collectAsStateWithLifecycle(false)
    val syncInterval by settingsViewModel.syncInterval.collectAsStateWithLifecycle(0)
    val context = LocalContext.current

    DynamicTheme {
        SettingsContent(
            isSignedIn,
            accountName,
            accountEmail,
            todoPath,
            themeMode,
            useDynamicColor,
            syncOnStart,
            syncInterval,
            settingsViewModel::signOut,
            onBeginSignIn = { settingsViewModel.beginSignIn(context) },
            settingsViewModel::updateTodoPath,
            onUpdateThemeMode = settingsViewModel::updateThemeMode,
            onUpdateDynamicColor = settingsViewModel::updateUseDynamicColor,
            onUpdateSyncOnStart = settingsViewModel::updateSyncOnStart,
            onUpdateSyncInterval = settingsViewModel::updateSyncInterval
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    isSignedIn: Boolean,
    accountName: String,
    accountEmail: String,
    todoPath: String,
    themeMode: ThemeMode,
    useDynamicColor: Boolean,
    syncOnStart: Boolean,
    syncInterval: Int,
    onSignOut: () -> Unit,
    onBeginSignIn: () -> Unit,
    onUpdateTodoPath: (String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateDynamicColor: (Boolean) -> Unit,
    onUpdateSyncOnStart: (Boolean) -> Unit,
    onUpdateSyncInterval: (Int) -> Unit
) {

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val scrollState = rememberScrollState()

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
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.SettingSpacing),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {

            Section("Account") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SettingSpacing)) {
                    if (isSignedIn) {
                        InfoItem(
                            title = accountName,
                            value = accountEmail
                        )

                        SettingButton("Sign Out") {
                            onSignOut()
                        }
                    } else {
                        SettingButton("Sign In") { onBeginSignIn() }
                    }
                }
            }

            Section("Data/Sync") {

                SettingDialog(
                    "Task File",
                    value = todoPath
                ) {
                    PathDialog(
                        onDismissRequest = it,
                        path = todoPath,
                        onConfirmation = { path ->
                            onUpdateTodoPath(path)
                        })
                }

                SettingSwitch(
                    "Sync on application start",
                    onToggle = { onUpdateSyncOnStart(it) },
                    syncOnStart
                )

                SettingDialog(
                    "Sync Interval",
                    value = friendlyInterval(syncInterval)
                ) {
                    IntervalDialog(
                        onDismissRequest = it,
                        syncInterval = syncInterval,
                        onConfirmation = { syncInterval ->
                            onUpdateSyncInterval(syncInterval)
                        },
                        { minutes -> friendlyInterval(minutes) })
                }
            }

            Section(title = "Appearance") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.SettingSpacing)
                ) {
                    SettingSelection(
                        "Theme",
                        { onUpdateThemeMode(enumValueOf<ThemeMode>(it)) },
                        themeMode.toString(),
                        enumValues<ThemeMode>().map { t -> t.toString() }
                    )

                    SettingSwitch(
                        "Use Dynamic Color",
                        onToggle = { onUpdateDynamicColor(it) },
                        useDynamicColor
                    )
                }
            }

            Section("About") {
                InfoItem(
                    title = "Version",
                    value = "0.1.0"
                )
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    HorizontalDivider()

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.SettingSpacing),
        modifier = modifier.padding(Dimensions.SettingSectionPadding)
    ) {

        Row {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        content()
    }
}

@Composable
fun SettingTitle(title: String) {
    Row {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun InfoItem(
    title: String,
    value: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {

        SettingTitle(title)

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
fun SettingSelection(
    title: String,
    onSelect: (String) -> Unit,
    selection: String,
    options: Iterable<String>
) {
    Column {

        SettingTitle(title)

        SingleChoiceSegmentedButtonRow {

            for (option in options.withIndex()) {
                SegmentedButton(
                    option.value == selection,
                    onClick = { onSelect(option.value) },
                    label = { Text(option.value) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = option.index,
                        count = options.count()
                    )
                )
            }
        }
    }
}

@Composable
fun SettingSwitch(title: String, onToggle: (Boolean) -> Unit, selection: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SettingSpacing)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Switch(checked = selection, onCheckedChange = onToggle)
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
    )
    {
        InfoItem(title, value)
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
) {
    Text(
        text,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()

    )
}

@Preview(name = "Setting Dialog Light")
@Preview("Setting Dialog Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingDialogPreview() {
    AppTheme {
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
    AppTheme {
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
    AppTheme {
        Surface {
            SettingButton(text = "Sign Out", onClick = {})
        }
    }
}

@Preview(name = "Settings Content Light")
@Preview("Settings Content Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsContentPreview() {
    AppTheme {
        Surface {
            SettingsContent(
                true,
                "Chuck Finley",
                "cfinley@miami.org",
                "/todo/todo.txt",
                ThemeMode.Light,
                false,
                syncOnStart = false,
                syncInterval = 60,
                { },
                onBeginSignIn = { },
                { },
                { },
                { },
                { },
                { }
            )
        }
    }
}