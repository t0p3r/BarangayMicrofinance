package com.barangay.microfinance.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        BorrowerEntity::class,
        LoanEntity::class,
        RepaymentScheduleEntity::class,
        PaymentTransactionEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun borrowerDao(): BorrowerDao
    abstract fun loanDao(): LoanDao
    abstract fun paymentDao(): PaymentDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        private const val DB_NAME = "barangay_microfinance_encrypted.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Builds encrypted Room instance using SQLCipher and hardware-protected passphrase
         */
        fun getInstance(context: Context, dbPassphraseBytes: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Initialize SQLCipher native binaries
                SQLiteDatabase.loadLibs(context)

                // SQLCipher OpenHelper Factory for Room
                val factory = SupportFactory(dbPassphraseBytes)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration() // In production, provide safe Migration(1, 2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}