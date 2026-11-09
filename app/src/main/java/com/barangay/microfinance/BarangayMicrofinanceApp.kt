package com.barangay.microfinance

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.barangay.microfinance.data.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import net.sqlcipher.database.SQLiteDatabase
import javax.inject.Inject

@HiltAndroidApp
class BarangayMicrofinanceApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize SQLCipher native encryption binaries
        SQLiteDatabase.loadLibs(this)

        // 2. Schedule periodic background sync worker (every 15 min when network connected)
        val workManager = WorkManager.getInstance(this)
        SyncWorker.enqueuePeriodicSync(workManager)
    }
}