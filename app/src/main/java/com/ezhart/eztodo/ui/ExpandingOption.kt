package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.ui.theme.Dimensions

// TODO If the selected option is out of view when the filter sheet is opened, the sheet should scroll it into view

@Composable
fun ExpandingOption(
    text: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    options: List<String>,
    selectedOption: String? = null,
    onSelected: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onToggle()
                }
                .padding(Dimensions.MenuOptionPadding)
        )
        {
            val expansionIcon = when (expanded) {
                true -> Icons.Outlined.KeyboardArrowUp
                false -> Icons.Outlined.KeyboardArrowDown
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Label,
                contentDescription = text
            )
            Spacer(Modifier.width(16.dp))
            Text(text = text)
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = expansionIcon,
                contentDescription = text
            )
        }

        if (expanded) {
            Column {
                for (option in options) {
                    MenuOption(
                        text = option,
                        null,
                        selectedOption == option
                    ) { onSelected(option) }
                }
            }
        }
    }
}

@Preview(name = "Expanding Option Light")
@Preview("Expanding Option Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpandingOptionPreview() {
    AppTheme {
        Surface {
            ExpandingOption(
                "Contexts",
                false, onToggle = {}, listOf(), onSelected = {})
        }
    }
}

@Preview(name = "Expanding Option Open Light")
@Preview("Expanding Option Open Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpandingOptionOpenPreview() {
    AppTheme {
        Surface {
            ExpandingOption(
                "Projects",
                true, onToggle = {}, listOf("+shopping", "+paint", "+fixDrain"), onSelected = {})
        }
    }
}
