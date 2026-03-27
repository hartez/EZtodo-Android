package com.ezhart.todotxtandroid.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezhart.todotxtandroid.ui.theme.AppTheme

@Composable
fun TextFilterBar(
    filterTextState: TextFieldState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    // BottomAppBar is deprecated, will replace this when Jetpack has better support for
    // docked toolbars or possible use floating toolbars

    BottomAppBar(
        contentPadding = PaddingValues(0.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,

        // This does not adjust the position of the bar on older Android versions (like 9)
        // But it does work on more modern versions (e.g., 16), and it's not a dealbreaker on 9
        // - the keyboard covers the search text input, but it's still usable
        modifier = modifier.imePadding(),
        actions = {

            IconButton(onClick = {
                filterTextState.clearText()
                onDismiss()
            }
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }

            TextField(
                state = filterTextState,
                placeholder = {
                    Text(
                        "Search...", color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                lineLimits = TextFieldLineLimits.SingleLine,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),

                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { filterTextState.clearText() }
            ) {
                Icon(Icons.Outlined.Clear, contentDescription = "Clear")
            }
        }
    )
}

@Preview(name = "FilterBar Light", showBackground = true)
@Preview("FilterBar Dark", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SearchBarPreviewText() {
    AppTheme {
        TextFilterBar(TextFieldState(),
            {}, Modifier)
    }
}