package com.ezhart.eztodo.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ezhart.eztodo.TAG
import com.ezhart.eztodo.EZtodoApplication
import com.ezhart.eztodo.dropbox.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val dropboxService = (applicationContext as EZtodoApplication).dropboxService

        when (val syncResult = dropboxService.sync()) {
            is SyncResult.Error -> {
                Log.e(TAG,  "${syncResult.message} ${syncResult.e.toString()}")
                Result.failure()
            }
            else -> {
                Log.i(TAG, syncResult.message)
                Result.success()
            }
        }
    }
}