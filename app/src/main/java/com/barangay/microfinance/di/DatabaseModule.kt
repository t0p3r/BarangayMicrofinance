package com.barangay.microfinance.di

import android.content.Context
import com.barangay.microfinance.data.local.db.*
import com.barangay.microfinance.data.security.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCryptoManager(
        @ApplicationContext context: Context
    ): CryptoManager = CryptoManager(context)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): AppDatabase {
        val dbPassphrase = cryptoManager.getOrCreateDatabasePassphrase()
        return AppDatabase.getInstance(context, dbPassphrase)
    }

    @Provides
    fun provideBorrowerDao(database: AppDatabase): BorrowerDao = database.borrowerDao()

    @Provides
    fun provideLoanDao(database: AppDatabase): LoanDao = database.loanDao()

    @Provides
    fun providePaymentDao(database: AppDatabase): PaymentDao = database.paymentDao()

    @Provides
    fun provideSyncQueueDao(database: AppDatabase): SyncQueueDao = database.syncQueueDao()
}
