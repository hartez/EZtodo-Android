package com.ezhart.eztodo.ui

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TagAutocompleteMenu(
    options: List<String>, onClose: () -> Unit, onSelect: () -> Unit
) {
    DropdownMenuPopup(
        expanded = true,
        properties = PopupProperties(focusable = false),
        onDismissRequest = { onClose() },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        for (option in options) {
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onClose()
                    onSelect()
                }
            )
        }
    }
}