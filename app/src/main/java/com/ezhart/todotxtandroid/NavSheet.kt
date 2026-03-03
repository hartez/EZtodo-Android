package com.ezhart.todotxtandroid

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezhart.todotxtandroid.data.PendingFilter
import com.ezhart.todotxtandroid.ui.theme.Dimensions
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavSheet(open: Boolean, onClose: () -> Unit, onNavigateToSettings: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (open) {
        ModalBottomSheet(
            onDismissRequest = { onClose() },
            sheetState = sheetState
        ) {
            // Sheet content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimensions.SheetCornerRadius,
                            topEnd = Dimensions.SheetCornerRadius
                        )
                    )
                    .padding(0.dp, 16.dp, 0.dp, 0.dp)
            ) {
                MenuOption(
                    "Settings",
                    Icons.Outlined.Settings,
                    false
                ) {
                    onClose()
                    onNavigateToSettings()
                }

            }
        }
    }
}

@Preview(name = "Filter Sheet Light")
@Preview("Filter Sheet Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NavSheetPreview() {
    TodotxtAndroidTheme {
        Surface {
            NavSheet(
                true,
                { },
                { })
        }
    }
}