package com.ezhart.todotxtandroid

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Our minimum API is 28 right now, so although SOFT_INPUT_ADJUST_RESIZE
        // is deprecated, we still have to use it because the alternative isn't available
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        (this.application as TodotxtAndroidApplication).dropboxService.onResume()
    }
}




