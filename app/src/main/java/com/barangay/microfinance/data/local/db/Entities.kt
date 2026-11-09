package com.barangay.microfinance.data.local.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Borrower Entity: Stores barangay resident borrower profile.
 * Sensitive PII fields (govIdNumber, contactNumber, coMakerContact) are encrypted using AES-256-GCM.
 */
@Entity(
    tableName = "borrowers",
    indices = [
        Index(value = ["borrowerCode"], unique = true),
        Index(value = ["purok"]),
        Index(value = ["syncStatus"])
    ]
)
data class BorrowerEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val borrowerCode: String, // e.g. "BRGY-2026-0042"
    val firstName: String,
    val lastName: String,
    val alias: String?, // Philippine moniker ("Aling Nena")
    val purok: String, // e.g. "Purok 1 - Sampaguita"
    val barangay: String,
    val municipality: String,
    val livelihood: String, // e.g. "Sari-sari Store", "Tricycle Driver"
    val encryptedContactNumber: String, // AES-256-GCM ciphertext
    val govIdType: String, // "PHILSYS", "SSS", "PHILHEALTH", "VOTERS_ID"
    val encryptedGovIdNumber: String, // AES-256-GCM ciphertext
    val coMakerName: String,
    val encryptedCoMakerContact: String, // AES-256-GCM ciphertext
    val creditScore: Int, // 300 - 850
    val riskTier: String, // "LOW", "MEDIUM", "HIGH"
    val syncStatus: String = "SYNCED", // "PENDING", "SYNCING", "SYNCED"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Loan Entity: Represents loan contract (Daily, Weekly, or Monthly).
 */
@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = BorrowerEntity::class,
            parentColumns = ["id"],
            childColumns = ["borrowerId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["loanNumber"], unique = true),
        Index(value = ["borrowerId"]),
        Index(value = ["frequency"]),
        Index(value = ["status"])
    ]
)
data class LoanEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val loanNumber: String, // e.g. "LN-DAILY-2026-0081"
    val borrowerId: String,
    val principalAmount: Double, // Philippine Peso
    val interestRate: Double, // e.g. 3.0%
    val interestType: String, // "FLAT" or "DIMINISHING"
    val frequency: String, // "DAILY", "WEEKLY", "MONTHLY"
    val termCount: Int, // Number of installments (e.g. 60 days, 16 weeks, 6 months)
    val amortizationPerPeriod: Double,
    val totalInterest: Double,
    val totalAmountDue: Double,
    val balanceRemaining: Double,
    val disbursementDate: Long,
    val maturityDate: Long,
    val status: String = "ACTIVE", // "ACTIVE", "PAID", "DEFAULTED"
    val penaltyPerMissedPayment: Double = 25.0,
    val gracePeriodDays: Int = 1,
    val syncStatus: String = "SYNCED",
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Repayment Schedule Entity: Individual installment records.
 */
@Entity(
    tableName = "repayment_schedules",
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["loanId"]),
        Index(value = ["dueDate"]),
        Index(value = ["status"])
    ]
)
data class RepaymentScheduleEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val loanId: String,
    val installmentNumber: Int,
    val dueDate: Long,
    val expectedAmount: Double,
    val paidAmount: Double = 0.0,
    val principalComponent: Double,
    val interestComponent: Double,
    val penaltyAmount: Double = 0.0,
    val status: String = "PENDING", // "PENDING", "PAID", "PARTIAL", "OVERDUE"
    val paidDate: Long? = null
)

/**
 * Payment Transaction Entity: Immutable ledger entry for cash or digital collections.
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["receiptNumber"], unique = true),
        Index(value = ["loanId"]),
        Index(value = ["paymentDate"]),
        Index(value = ["syncStatus"])
    ]
)
data class PaymentTransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), // Client-generated idempotency key
    val receiptNumber: String, // e.g. "OR-20261108-0042"
    val loanId: String,
    val borrowerId: String,
    val amountPaid: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val collectorId: String,
    val collectorName: String,
    val paymentMethod: String = "CASH", // "CASH", "GCASH", "MAYA"
    val latitude: Double? = null,
    val longitude: Double? = null,
    val syncStatus: String = "PENDING", // "PENDING", "SYNCING", "SYNCED", "FAILED"
    val syncAttempts: Int = 0,
    val remarks: String? = null,
    val offlineCreated: Boolean = true
)

/**
 * Offline Sync Queue Entity: Tracks outbound mutations to be sent to remote server.
 */
@Entity(
    tableName = "sync_queue",
    indices = [
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class SyncQueueEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val operation: String, // "CREATE_PAYMENT", "CREATE_LOAN", "UPDATE_BORROWER"
    val payloadType: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "SYNCING", "FAILED"
    val retryCount: Int = 0,
    val lastAttemptAt: Long? = null,
    val errorMessage: String? = null
)