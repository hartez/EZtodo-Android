package com.ezhart.todotxtandroid.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezhart.todotxtandroid.ui.theme.AppTheme
import com.ezhart.todotxtandroid.ui.theme.Dimensions

@Composable
fun MenuOption(text: String, icon: ImageVector? = null, selected: Boolean, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when (selected) {
                    true -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .clickable {
                if (!selected) {
                    onSelected()
                }
            }
            .padding(Dimensions.MenuOptionPadding)

    )
    {

        if(icon != null){
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        } else{
            Spacer(Modifier.width(32.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(text = text)
    }
}

@Preview(name = "Filter Option Light")
@Preview(name = "Filter Option Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MenuOptionPreview() {
    AppTheme {
        Surface {
            Column {
                MenuOption(
                    "All Tasks", Icons.Outlined.Inbox, true
                ) { }

                MenuOption(
                    "All Tasks", Icons.Outlined.Inbox, false
                ) { }

                MenuOption(
                    "+paintShed", null, false
                ) { }
            }

        }
    }
}
