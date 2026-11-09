package com.barangay.microfinance.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowerDao {
    @Query("SELECT * FROM borrowers ORDER BY purok, lastName ASC")
    fun getAllBorrowersFlow(): Flow<List<BorrowerEntity>>

    @Query("SELECT * FROM borrowers WHERE purok = :purok ORDER BY lastName ASC")
    fun getBorrowersByPurokFlow(purok: String): Flow<List<BorrowerEntity>>

    @Query("SELECT * FROM borrowers WHERE id = :id LIMIT 1")
    suspend fun getBorrowerById(id: String): BorrowerEntity?

    @Query("SELECT * FROM borrowers WHERE borrowerCode = :code LIMIT 1")
    suspend fun getBorrowerByCode(code: String): BorrowerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrower(borrower: BorrowerEntity)

    @Update
    suspend fun updateBorrower(borrower: BorrowerEntity)
}

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans WHERE status = 'ACTIVE' ORDER BY disbursementDate DESC")
    fun getActiveLoansFlow(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE borrowerId = :borrowerId ORDER BY disbursementDate DESC")
    fun getLoansForBorrowerFlow(borrowerId: String): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :id LIMIT 1")
    suspend fun getLoanById(id: String): LoanEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLoan(loan: LoanEntity)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("UPDATE loans SET balanceRemaining = balanceRemaining - :paymentAmount WHERE id = :loanId")
    suspend fun deductLoanBalance(loanId: String, paymentAmount: Double)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPaymentsFlow(): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payments WHERE syncStatus = 'PENDING' ORDER BY paymentDate ASC")
    suspend fun getPendingPayments(): List<PaymentTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: PaymentTransactionEntity)

    @Update
    suspend fun updatePayment(payment: PaymentTransactionEntity)

    @Query("UPDATE payments SET syncStatus = :status, syncAttempts = syncAttempts + 1 WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)

    /**
     * Atomic transaction: records payment, reduces loan balance, and adds to sync queue
     */
    @Transaction
    suspend fun recordPaymentTransaction(
        payment: PaymentTransactionEntity,
        loanDao: LoanDao,
        syncQueueDao: SyncQueueDao,
        payloadJson: String
    ) {
        insertPayment(payment)
        loanDao.deductLoanBalance(payment.loanId, payment.amountPaid)
        
        syncQueueDao.insert(
            SyncQueueEntity(
                id = payment.id, // Use payment UUID as idempotency key
                operation = "CREATE_PAYMENT",
                payloadType = "PaymentTransactionEntity",
                payloadJson = payloadJson,
                status = "PENDING"
            )
        )
    }
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueEntity)

    @Update
    suspend fun update(item: SyncQueueEntity)

    @Delete
    suspend fun delete(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = 'FAILED', retryCount = retryCount + 1, errorMessage = :error WHERE id = :id")
    suspend fun markFailed(id: String, error: String)

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>
}