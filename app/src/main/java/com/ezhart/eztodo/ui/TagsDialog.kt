package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.ui.theme.Dimensions

@Composable
fun TagsDialog(
    onDismissRequest: () -> Unit,
    options: Map<String, Boolean>,
    onSubmit: (Map<String, Boolean>) -> Unit
) {
    val selections =
        remember { mutableStateMapOf(*(options.map { (k, v) -> k to v }.toTypedArray())) }

    Dialog(onDismissRequest = onDismissRequest) {
        Box {
            Card(
                modifier = Modifier
                    .fillMaxSize(0.90f)
            ) {
                Column {

                    Row {
                        Text(
                            text = "Projects & Contexts",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .padding(Dimensions.DialogHeadingPadding)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            for (selection in selections.toSortedMap()) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = selection.key,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .padding(16.dp, 8.dp)
                                        )
                                    },
                                    onClick = {
                                        selections[selection.key] = !selection.value
                                    },
                                    modifier = Modifier.background(
                                        color =
                                            if (selection.value) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surface
                                            }
                                    )
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface),
                        horizontalArrangement = Arrangement.End,
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
                                onSubmit(selections)
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
    }
}

@Preview(name = "Tags Dialog Light")
@Preview("Tags Dialog Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TagsDialogPreview() {

    val options = mapOf(
        "+paintHouse" to true,
        "+renewLicense" to false,
        "@home" to true,
        "@shopping" to false
    )

    AppTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                TagsDialog(
                    {},
                    options,
                    {}
                )
            }
        }
    }
}