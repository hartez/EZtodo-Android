package com.ezhart.todotxtandroid

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.ezhart.todotxtandroid.workers.SyncWorker
import java.util.concurrent.TimeUnit

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

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(true)
            .build()

        // TODO Get the interval from settings
        // TODO figure out how to update/cancel the schedule when the settings value changes
        val syncWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(this)
            .enqueue(syncWorkRequest)
    }

    override fun onResume() {
        super.onResume()
        (this.application as TodotxtAndroidApplication).dropboxService.onResume()
    }
}




