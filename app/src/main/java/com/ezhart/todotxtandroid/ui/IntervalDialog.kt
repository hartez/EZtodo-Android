package com.ezhart.todotxtandroid.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezhart.todotxtandroid.dropbox.friendlyInterval
import com.ezhart.todotxtandroid.ui.theme.AppTheme

@Composable
fun IntervalDialog(
    onDismissRequest: () -> Unit,
    syncInterval: Int,
    onConfirmation: (Int) -> Unit,
    onFormat: (Int) -> String
) {

    val radioOptions = listOf(0, 15, 60, 180, 360)
    val (selectedOption, onOptionSelected) = remember {
        mutableIntStateOf(
            radioOptions[radioOptions.indexOf(
                syncInterval
            )]
        )
    }

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {

            Row {
                Text(
                    text = "Background sync interval",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Row {
                Column(Modifier.selectableGroup()) {
                    radioOptions.forEach { interval ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (interval == selectedOption),
                                    onClick = { onOptionSelected(interval) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (interval == selectedOption),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = onFormat(interval),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Dismiss")
                }
                TextButton(
                    onClick = {
                        onConfirmation(selectedOption)
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

@Preview(name = "Interval Dialog Light")
@Preview("Interval Dialog Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun IntervalDialogPreview() {
    AppTheme {
        Surface {
            IntervalDialog(
                { },
                60,
                { }, { friendlyInterval(it) }
            )
        }
    }
}
