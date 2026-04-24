package com.ezhart.eztodo.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TagAutocompleteMenu(
    options: List<String>, onClose: () -> Unit, onSelect: (String) -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = { onClose() },
        properties = PopupProperties(focusable = false),
        offset = DpOffset(0.dp, (-300).dp),
        modifier = Modifier.imePadding().height(300.dp)
    ) {
        for (option in options) {
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onClose()
                    onSelect(option)
                }
            )
        }
    }
}