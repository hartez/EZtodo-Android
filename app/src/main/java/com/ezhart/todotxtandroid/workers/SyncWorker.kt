package com.ezhart.todotxtandroid.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ezhart.todotxtandroid.TAG
import com.ezhart.todotxtandroid.TodotxtAndroidApplication
import com.ezhart.todotxtandroid.dropbox.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val dropboxService = (applicationContext as TodotxtAndroidApplication).dropboxService

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