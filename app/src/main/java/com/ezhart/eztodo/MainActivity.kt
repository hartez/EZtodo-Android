package com.ezhart.eztodo

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.ezhart.eztodo.workers.SyncWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        setContent {
            App()
        }

        lifecycleScope.launch {
            val settingsRepository =
                (applicationContext as EZtodoApplication).settingsRepository

            val initial = settingsRepository.syncInterval.first()

            settingsRepository.syncInterval.map { interval -> interval }
                .stateIn(
                    lifecycleScope,
                    SharingStarted.WhileSubscribed(5000),
                    initialValue = initial
                ).collect {interval ->
                    val workManager = WorkManager.getInstance(applicationContext)

                    Log.i(TAG, "Canceling existing sync schedule")
                    workManager.cancelAllWorkByTag("sync")

                    if (interval > 0) {

                        Log.i(TAG, "Rescheduling sync on $interval minute interval")

                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .setRequiresDeviceIdle(true)
                            .build()

                        val syncWorkRequest: WorkRequest =
                            PeriodicWorkRequestBuilder<SyncWorker>(interval.toLong(), TimeUnit.MINUTES)
                                .setConstraints(constraints)
                                .addTag("sync")
                                .build()

                        workManager.enqueue(syncWorkRequest)
                    }
                }

        }
    }

    override fun onResume() {
        super.onResume()
        (this.application as EZtodoApplication).dropboxService.onResume()
    }
}




