package com.ezhart.eztodo.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TagAutocompleteMenu(
    options: List<String>,
    onClose: () -> Unit,
    onSelect: (String) -> Unit,
    maxHeight: Dp,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Log.d("dbg TaskEditor", "Max height for for menu is  ${maxHeight}")

    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current

    val paddingStartOffset = with(density) {
        padding.calculateStartPadding(layoutDirection).roundToPx()
    }

    val paddingBottomOffset = with(density) {
        padding.calculateBottomPadding().roundToPx()
    }

    Popup(
        onDismissRequest = { onClose() },
        properties = PopupProperties(focusable = false),
        popupPositionProvider = object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                Log.d(
                    "dbg TaskEditor",
                    "calculatePosition ${anchorBounds}, ${windowSize}, ${layoutDirection}, ${popupContentSize}"
                )

                return IntOffset(
                    x = paddingStartOffset,
                    y = anchorBounds.top - popupContentSize.height - paddingBottomOffset
                )
            }
        }) {
        Box(
            modifier = Modifier
                .heightIn(max = maxHeight)
                .width(IntrinsicSize.Min)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                for (option in options) {
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        onClose()
                        onSelect(option)
                    })
                }
            }
        }
    }
}

