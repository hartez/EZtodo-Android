package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ezhart.eztodo.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToolbarMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onRefresh: () -> Unit
) {
    if (isOpen) {
        DropdownMenuPopup(
            expanded = true,
            onDismissRequest = { onClose() },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {

            DropdownMenuItem(

                text = { Text("Refresh") },
                onClick = {
                    onClose()
                    onRefresh()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh",
                    )
                },

                )
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {
                    onClose()
                    onNavigateToSettings()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                    )
                }
            )
        }
    }
}

@Preview(name = "Toolbar Menu Light")
@Preview("Toolbar Menu Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ToolbarMenuPreview() {
    AppTheme {
        Surface {
            ToolbarMenu(
                isOpen = true,
                { },
                { },
                onRefresh = {}
            )
        }
    }
}