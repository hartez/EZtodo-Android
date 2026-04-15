package com.ezhart.todotxtandroid.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun BarTest(){
    HorizontalFloatingToolbar(
        expanded = true,
        content = {}
    )
}