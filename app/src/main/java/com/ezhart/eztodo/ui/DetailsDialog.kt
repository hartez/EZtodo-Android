package com.ezhart.eztodo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.ezhart.eztodo.data.Task
import com.ezhart.eztodo.ui.theme.AppTheme
import com.ezhart.eztodo.viewmodels.DetailsDialogUIState
import kotlin.math.roundToInt

@Composable
fun DetailsDialog(uiState: DetailsDialogUIState) {
    var horizontalDragState by remember { mutableStateOf(AnchoredDraggableState(HorizontalSwipeValue.Current)) }
    var verticalDragState by remember { mutableStateOf(AnchoredDraggableState(VerticalSwipeValue.Current)) }

    var screenWidthOffset = 0f;

    LaunchedEffect(horizontalDragState.settledValue, verticalDragState.settledValue) {
        when (horizontalDragState.settledValue) {
            HorizontalSwipeValue.Previous -> {
                if (uiState.previousTask != null) {
                    uiState.onUpdateSelectedTask(uiState.previousTask)
                }
                horizontalDragState = AnchoredDraggableState(
                    HorizontalSwipeValue.Current,
                    horizontalDragState.anchors
                )
            }

            HorizontalSwipeValue.Current -> {}

            HorizontalSwipeValue.Next -> {
                if (uiState.nextTask != null) {
                    uiState.onUpdateSelectedTask(uiState.nextTask)
                }
                horizontalDragState = AnchoredDraggableState(
                    HorizontalSwipeValue.Current,
                    horizontalDragState.anchors
                )
            }
        }

        when(verticalDragState.settledValue){
            VerticalSwipeValue.Dismiss -> {
                uiState.onDismissRequest()
            }
            VerticalSwipeValue.Current -> { }
            VerticalSwipeValue.Edit -> {
                uiState.onEditRequest()
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { layoutSize ->
                screenWidthOffset = layoutSize.width.toFloat()
                horizontalDragState.updateAnchors(
                    DraggableAnchors {
                        HorizontalSwipeValue.Next at -(layoutSize.width.toFloat())
                        HorizontalSwipeValue.Current at 0f
                        HorizontalSwipeValue.Previous at (layoutSize.width.toFloat())
                    })

                verticalDragState.updateAnchors(
                    DraggableAnchors {
                        VerticalSwipeValue.Dismiss at (-layoutSize.height.toFloat())
                        VerticalSwipeValue.Current at 0f
                        VerticalSwipeValue.Edit at (layoutSize.height.toFloat())
                    }
                )
            }

    ) {

        val baseModifier = Modifier.fillMaxSize(0.90f)

        if (uiState.task != null) {
            DetailsCard(
                uiState.task,
                onEditRequest = uiState.onEditRequest,
                onToggleCompleted = uiState.onToggleCompleted,
                modifier = baseModifier
                    .offset {
                        IntOffset(
                            horizontalDragState.requireOffset().roundToInt(),
                            verticalDragState.requireOffset().roundToInt()
                        )
                    }
                    .anchoredDraggable(
                        horizontalDragState,
                        orientation = Orientation.Horizontal
                    )
                    .anchoredDraggable(
                        verticalDragState,
                        orientation = Orientation.Vertical
                    )
            )
        }

        if (uiState.nextTask != null) {
            DetailsCard(
                uiState.nextTask,
                baseModifier
                    .offset {
                        IntOffset(
                            (horizontalDragState.requireOffset() + screenWidthOffset).roundToInt(),
                            0
                        )
                    }
            )
        }

        if (uiState.previousTask != null) {
            DetailsCard(
                uiState.previousTask,
                baseModifier
                    .offset {
                        IntOffset(
                            (horizontalDragState.requireOffset() - screenWidthOffset).roundToInt(),
                            0
                        )
                    }
            )
        }
    }
}

enum class HorizontalSwipeValue { Previous, Current, Next }
enum class VerticalSwipeValue { Dismiss, Current, Edit }

@Preview(name = "Details Dialog Light")
@Preview("Details Dialog Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DetailsDialogPreview() {

    val uiState = DetailsDialogUIState(
        true,
        Task("2025-06-04 Buy apples @shopping +pie due:2025-06-06")
    )

    AppTheme {
        Surface {
            DetailsDialog(uiState)
        }
    }
}
