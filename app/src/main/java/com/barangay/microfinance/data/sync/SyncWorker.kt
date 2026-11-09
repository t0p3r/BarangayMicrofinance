package com.barangay.microfinance.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.barangay.microfinance.data.local.db.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * SyncWorker: Seamless background synchronization for Barangay Microfinance.
 * Automatically triggered when network connection returns.
 * Processes outbound payment transactions, loan applications, and updates.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME_PERIODIC = "barangay_sync_periodic"
        const val WORK_NAME_ONE_TIME = "barangay_sync_expedited"

        /**
         * Schedules periodic background sync (every 15 minutes when online)
         */
        fun enqueuePeriodicSync(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES // Flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }

        /**
         * Triggers immediate expedited sync when officer manually taps Sync or internet reconnects
         */
        fun triggerImmediateSync(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val oneTimeRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            workManager.enqueueUniqueWork(
                WORK_NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val syncQueueDao = database.syncQueueDao()
        val paymentDao = database.paymentDao()

        val pendingItems = syncQueueDao.getPendingItems()
        if (pendingItems.isEmpty()) {
            return@withContext Result.success()
        }

        var hasFailures = false

        for (item in pendingItems) {
            try {
                // In production, invoke your remote REST/gRPC client:
                // val response = remoteApiClient.postTransaction(item.payloadJson)
                // Simulated atomic HTTP push with remote server idempotency:
                val isSuccess = simulateRemotePush(item.operation, item.payloadJson)

                if (isSuccess) {
                    // Success: mark local payment as SYNCED and remove from queue
                    if (item.operation == "CREATE_PAYMENT") {
                        paymentDao.updateSyncStatus(item.id, "SYNCED")
                    }
                    syncQueueDao.delete(item)
                } else {
                    hasFailures = true
                    syncQueueDao.markFailed(item.id, "Remote server 503: Temporarily Unavailable")
                }
            } catch (e: Exception) {
                hasFailures = true
                syncQueueDao.markFailed(item.id, e.localizedMessage ?: "Network I/O Error")
            }
        }

        if (hasFailures) {
            Result.retry() // WorkManager handles exponential backoff
        } else {
            Result.success()
        }
    }

    private fun simulateRemotePush(operation: String, json: String): Boolean {
        // Simulates server acknowledgement.
        return true
    }
}