package com.barangay.microfinance.domain.calculator

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

enum class PaymentFrequency {
    DAILY,
    WEEKLY,
    MONTHLY
}

enum class InterestCalculationType {
    FLAT,
    DIMINISHING
}

data class AmortizationScheduleItem(
    val installmentNumber: Int,
    val dueDate: LocalDate,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val totalInstallment: BigDecimal,
    val remainingBalance: BigDecimal
)

data class LoanCalculationResult(
    val principal: BigDecimal,
    val totalInterest: BigDecimal,
    val totalRepayment: BigDecimal,
    val installmentAmount: BigDecimal,
    val termCount: Int,
    val schedules: List<AmortizationScheduleItem>
)

/**
 * Philippine Microfinance Loan Calculation Engine
 * Complies with SEC and BSP micro-lending disclosure standards.
 */
object LoanCalculator {

    fun calculateLoan(
        principalAmount: Double,
        monthlyInterestRatePercent: Double, // e.g. 3.0 for 3%/month
        termCount: Int,
        frequency: PaymentFrequency,
        calculationType: InterestCalculationType = InterestCalculationType.FLAT,
        startDate: LocalDate = LocalDate.now()
    ): LoanCalculationResult {
        val principal = BigDecimal(principalAmount).setScale(2, RoundingMode.HALF_UP)
        val monthlyRate = BigDecimal(monthlyInterestRatePercent)
            .divide(BigDecimal(100), 6, RoundingMode.HALF_UP)

        return when (calculationType) {
            InterestCalculationType.FLAT -> calculateFlatSchedule(
                principal,
                monthlyRate,
                termCount,
                frequency,
                startDate
            )
            InterestCalculationType.DIMINISHING -> calculateDiminishingSchedule(
                principal,
                monthlyRate,
                termCount,
                frequency,
                startDate
            )
        }
    }

    private fun calculateFlatSchedule(
        principal: BigDecimal,
        monthlyRate: BigDecimal,
        termCount: Int,
        frequency: PaymentFrequency,
        startDate: LocalDate
    ): LoanCalculationResult {
        // Convert terms to months equivalent for interest
        val termInMonths = when (frequency) {
            PaymentFrequency.DAILY -> BigDecimal(termCount).divide(BigDecimal(30), 4, RoundingMode.HALF_UP)
            PaymentFrequency.WEEKLY -> BigDecimal(termCount).divide(BigDecimal(4), 4, RoundingMode.HALF_UP)
            PaymentFrequency.MONTHLY -> BigDecimal(termCount)
        }

        val totalInterest = principal
            .multiply(monthlyRate)
            .multiply(termInMonths)
            .setScale(2, RoundingMode.HALF_UP)

        val totalRepayment = principal.add(totalInterest)
        val installmentAmount = totalRepayment
            .divide(BigDecimal(termCount), 2, RoundingMode.HALF_UP)

        val principalPerPeriod = principal.divide(BigDecimal(termCount), 2, RoundingMode.HALF_UP)
        val interestPerPeriod = totalInterest.divide(BigDecimal(termCount), 2, RoundingMode.HALF_UP)

        val schedules = mutableListOf<AmortizationScheduleItem>()
        var balance = totalRepayment

        for (i in 1..termCount) {
            val dueDate = when (frequency) {
                PaymentFrequency.DAILY -> startDate.plusDays(i.toLong())
                PaymentFrequency.WEEKLY -> startDate.plusWeeks(i.toLong())
                PaymentFrequency.MONTHLY -> startDate.plusMonths(i.toLong())
            }

            balance = balance.subtract(installmentAmount).max(BigDecimal.ZERO)

            schedules.add(
                AmortizationScheduleItem(
                    installmentNumber = i,
                    dueDate = dueDate,
                    principalAmount = principalPerPeriod,
                    interestAmount = interestPerPeriod,
                    totalInstallment = installmentAmount,
                    remainingBalance = balance
                )
            )
        }

        return LoanCalculationResult(
            principal = principal,
            totalInterest = totalInterest,
            totalRepayment = totalRepayment,
            installmentAmount = installmentAmount,
            termCount = termCount,
            schedules = schedules
        )
    }

    private fun calculateDiminishingSchedule(
        principal: BigDecimal,
        monthlyRate: BigDecimal,
        termCount: Int,
        frequency: PaymentFrequency,
        startDate: LocalDate
    ): LoanCalculationResult {
        val ratePerPeriod = when (frequency) {
            PaymentFrequency.DAILY -> monthlyRate.divide(BigDecimal(30), 6, RoundingMode.HALF_UP)
            PaymentFrequency.WEEKLY -> monthlyRate.divide(BigDecimal(4), 6, RoundingMode.HALF_UP)
            PaymentFrequency.MONTHLY -> monthlyRate
        }

        // Standard EMI formula: P * r * (1+r)^n / ((1+r)^n - 1)
        val onePlusR = BigDecimal.ONE.add(ratePerPeriod)
        val pow = onePlusR.pow(termCount)
        val numerator = principal.multiply(ratePerPeriod).multiply(pow)
        val denominator = pow.subtract(BigDecimal.ONE)
        val installment = numerator.divide(denominator, 2, RoundingMode.HALF_UP)

        val schedules = mutableListOf<AmortizationScheduleItem>()
        var remainingPrincipal = principal
        var totalInterestAccumulator = BigDecimal.ZERO

        for (i in 1..termCount) {
            val dueDate = when (frequency) {
                PaymentFrequency.DAILY -> startDate.plusDays(i.toLong())
                PaymentFrequency.WEEKLY -> startDate.plusWeeks(i.toLong())
                PaymentFrequency.MONTHLY -> startDate.plusMonths(i.toLong())
            }

            val interestPart = remainingPrincipal.multiply(ratePerPeriod).setScale(2, RoundingMode.HALF_UP)
            val principalPart = installment.subtract(interestPart)
            remainingPrincipal = remainingPrincipal.subtract(principalPart).max(BigDecimal.ZERO)
            totalInterestAccumulator = totalInterestAccumulator.add(interestPart)

            schedules.add(
                AmortizationScheduleItem(
                    installmentNumber = i,
                    dueDate = dueDate,
                    principalAmount = principalPart,
                    interestAmount = interestPart,
                    totalInstallment = installment,
                    remainingBalance = remainingPrincipal
                )
            )
        }

        return LoanCalculationResult(
            principal = principal,
            totalInterest = totalInterestAccumulator,
            totalRepayment = principal.add(totalInterestAccumulator),
            installmentAmount = installment,
            termCount = termCount,
            schedules = schedules
        )
    }
}