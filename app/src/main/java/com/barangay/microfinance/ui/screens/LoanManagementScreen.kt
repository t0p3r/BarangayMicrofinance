package com.barangay.microfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barangay.microfinance.domain.calculator.InterestCalculationType
import com.barangay.microfinance.domain.calculator.LoanCalculationResult
import com.barangay.microfinance.domain.calculator.LoanCalculator
import com.barangay.microfinance.domain.calculator.PaymentFrequency
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DisplayLoanItem(
    val id: String,
    val loanNumber: String,
    val borrowerName: String,
    val barangayPurok: String,
    val principalAmount: Double,
    val balanceRemaining: Double,
    val frequency: String,
    val termCount: Int,
    val amortization: Double,
    var status: String // "ACTIVE", "PENDING_APPROVAL", "PAID", "REJECTED"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanManagementScreen(
    onNavigateToBorrowers: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active Portfolio, 1: Pending Approvals, 2: Calculator
    val pesoFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "PH")) }

    val loansList = remember {
        mutableStateListOf(
            DisplayLoanItem(
                id = "L1",
                loanNumber = "LN-DAILY-2026-0042",
                borrowerName = "Elena Santos (Aling Nena)",
                barangayPurok = "Purok 1 - Sampaguita",
                principalAmount = 10000.0,
                balanceRemaining = 4500.0,
                frequency = "DAILY",
                termCount = 60,
                amortization = 180.0,
                status = "ACTIVE"
            ),
            DisplayLoanItem(
                id = "L2",
                loanNumber = "LN-DAILY-2026-0081",
                borrowerName = "Roberto Cruz (Mang Bert)",
                barangayPurok = "Purok 2 - Masagana",
                principalAmount = 15000.0,
                balanceRemaining = 12000.0,
                frequency = "DAILY",
                termCount = 60,
                amortization = 270.0,
                status = "ACTIVE"
            ),
            DisplayLoanItem(
                id = "L3",
                loanNumber = "LN-WEEKLY-2026-0105",
                borrowerName = "Maria Clara Reyes",
                barangayPurok = "Purok 3 - Maharlika",
                principalAmount = 25000.0,
                balanceRemaining = 18500.0,
                frequency = "WEEKLY",
                termCount = 16,
                amortization = 1750.0,
                status = "ACTIVE"
            ),
            DisplayLoanItem(
                id = "L4",
                loanNumber = "LN-DAILY-2026-0112",
                borrowerName = "Juanito Del Rosario",
                barangayPurok = "Purok 1 - Sampaguita",
                principalAmount = 5000.0,
                balanceRemaining = 5000.0,
                frequency = "DAILY",
                termCount = 30,
                amortization = 180.0,
                status = "PENDING_APPROVAL"
            ),
            DisplayLoanItem(
                id = "L5",
                loanNumber = "LN-MONTHLY-2026-0129",
                borrowerName = "Teresa Villanueva",
                barangayPurok = "Purok 4 - Maligaya",
                principalAmount = 30000.0,
                balanceRemaining = 30000.0,
                frequency = "MONTHLY",
                termCount = 6,
                amortization = 5450.0,
                status = "PENDING_APPROVAL"
            )
        )
    }

    var showNewLoanDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Loan Portfolio & Calculator", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("SEC & BSP Standard Microfinance Engine", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { showNewLoanDialog = true }) {
                        Icon(imageVector = Icons.Default.AddCard, contentDescription = "New Loan")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewLoanDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Loan Application") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Selector Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active Portfolio (${loansList.count { it.status == "ACTIVE" }})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Pending Approvals (${loansList.count { it.status == "PENDING_APPROVAL" }})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Calculator & Schedule") }
                )
            }

            when (selectedTab) {
                0 -> ActiveLoansTab(
                    loans = loansList.filter { it.status == "ACTIVE" },
                    pesoFormat = pesoFormat
                )
                1 -> PendingApprovalsTab(
                    loans = loansList.filter { it.status == "PENDING_APPROVAL" },
                    pesoFormat = pesoFormat,
                    onApprove = { loan ->
                        loan.status = "ACTIVE"
                    },
                    onReject = { loan ->
                        loan.status = "REJECTED"
                    }
                )
                2 -> LoanCalculatorTab(pesoFormat = pesoFormat)
            }
        }
    }

    if (showNewLoanDialog) {
        NewLoanApplicationDialog(
            pesoFormat = pesoFormat,
            onDismiss = { showNewLoanDialog = false },
            onApplyLoan = { newLoan ->
                loansList.add(0, newLoan)
                showNewLoanDialog = false
            }
        )
    }
}

@Composable
fun ActiveLoansTab(
    loans: List<DisplayLoanItem>,
    pesoFormat: NumberFormat
) {
    if (loans.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active loans found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(loans, key = { it.id }) { loan ->
                LoanItemCard(loan = loan, pesoFormat = pesoFormat)
            }
        }
    }
}

@Composable
fun PendingApprovalsTab(
    loans: List<DisplayLoanItem>,
    pesoFormat: NumberFormat,
    onApprove: (DisplayLoanItem) -> Unit,
    onReject: (DisplayLoanItem) -> Unit
) {
    if (loans.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending loan applications for authorization.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(loans, key = { it.id }) { loan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = loan.borrowerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "PENDING APPROVAL",
                                    color = Color(0xFF92400E),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${loan.loanNumber} • ${loan.barangayPurok}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Requested Amount", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(pesoFormat.format(loan.principalAmount), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Amortization (${loan.frequency})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${pesoFormat.format(loan.amortization)} / period", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReject(loan) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                            ) {
                                Text("Reject")
                            }
                            Button(
                                onClick = { onApprove(loan) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                            ) {
                                Text("Approve Loan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoanItemCard(loan: DisplayLoanItem, pesoFormat: NumberFormat) {
    val progress = ((loan.principalAmount - loan.balanceRemaining) / loan.principalAmount).toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = loan.borrowerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${loan.loanNumber} • ${loan.frequency}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "ACTIVE",
                        color = Color(0xFF065F46),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Remaining Balance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(pesoFormat.format(loan.balanceRemaining), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Original Principal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(pesoFormat.format(loan.principalAmount), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF10B981),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun LoanCalculatorTab(pesoFormat: NumberFormat) {
    var principalInput by remember { mutableStateOf("10000") }
    var interestRateInput by remember { mutableStateOf("3.0") }
    var termCountInput by remember { mutableStateOf("60") }
    var selectedFrequency by remember { mutableStateOf(PaymentFrequency.DAILY) }
    var selectedType by remember { mutableStateOf(InterestCalculationType.FLAT) }

    val principal = principalInput.toDoubleOrNull() ?: 10000.0
    val rate = interestRateInput.toDoubleOrNull() ?: 3.0
    val terms = termCountInput.toIntOrNull() ?: 60

    val calcResult = remember(principal, rate, terms, selectedFrequency, selectedType) {
        LoanCalculator.calculateLoan(
            principalAmount = principal,
            monthlyInterestRatePercent = rate,
            termCount = terms,
            frequency = selectedFrequency,
            calculationType = selectedType
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Inputs Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Loan Terms Configuration", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = principalInput,
                    onValueChange = { principalInput = it },
                    label = { Text("Principal Amount (PHP)") },
                    prefix = { Text("₱ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = interestRateInput,
                        onValueChange = { interestRateInput = it },
                        label = { Text("Monthly Interest %") },
                        suffix = { Text("% / mo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = termCountInput,
                        onValueChange = { termCountInput = it },
                        label = { Text("Term Count") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Payment Frequency", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    PaymentFrequency.values().forEach { freq ->
                        FilterChip(
                            selected = (selectedFrequency == freq),
                            onClick = { selectedFrequency = freq },
                            label = { Text(freq.name) }
                        )
                    }
                }

                Text("Interest Calculation Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InterestCalculationType.values().forEach { type ->
                        FilterChip(
                            selected = (selectedType == type),
                            onClick = { selectedType = type },
                            label = { Text(type.name) }
                        )
                    }
                }
            }
        }

        // Output Results Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Repayment Summary Breakdown", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Installment per Period:", fontSize = 13.sp)
                    Text(pesoFormat.format(calcResult.installmentAmount), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Interest Amount:", fontSize = 13.sp)
                    Text(pesoFormat.format(calcResult.totalInterest), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Repayment Payable:", fontSize = 13.sp)
                    Text(pesoFormat.format(calcResult.totalRepayment), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Amortization Schedule Preview Table
        Text("Amortization Schedule Preview", fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                    Text("Due Date", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    Text("Principal", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Interest", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Due", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                }

                val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

                calcResult.schedules.take(10).forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.installmentNumber.toString(), fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                        Text(item.dueDate.format(dateFormatter), fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                        Text("₱${item.principalAmount}", fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                        Text("₱${item.interestAmount}", fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                        Text("₱${item.totalInstallment}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }

                if (calcResult.schedules.size > 10) {
                    Text(
                        text = "+ ${calcResult.schedules.size - 10} more installments in full schedule",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLoanApplicationDialog(
    pesoFormat: NumberFormat,
    onDismiss: () -> Unit,
    onApplyLoan: (DisplayLoanItem) -> Unit
) {
    var borrowerName by remember { mutableStateOf("") }
    var principalInput by remember { mutableStateOf("10000") }
    var frequencyInput by remember { mutableStateOf("DAILY") }
    var termInput by remember { mutableStateOf("60") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Loan Application", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = borrowerName,
                    onValueChange = { borrowerName = it },
                    label = { Text("Borrower Name / Code *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = principalInput,
                    onValueChange = { principalInput = it },
                    label = { Text("Principal Loan Amount (PHP)") },
                    prefix = { Text("₱ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = termInput,
                    onValueChange = { termInput = it },
                    label = { Text("Term Count (e.g. 60 Days / 16 Weeks)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (borrowerName.isNotBlank()) {
                        val principal = principalInput.toDoubleOrNull() ?: 10000.0
                        val terms = termInput.toIntOrNull() ?: 60
                        val calc = LoanCalculator.calculateLoan(
                            principalAmount = principal,
                            monthlyInterestRatePercent = 3.0,
                            termCount = terms,
                            frequency = PaymentFrequency.DAILY
                        )

                        val newLoan = DisplayLoanItem(
                            id = "L${(100..999).random()}",
                            loanNumber = "LN-DAILY-2026-${(1000..9999).random()}",
                            borrowerName = borrowerName.trim(),
                            barangayPurok = "Purok 1 - Sampaguita",
                            principalAmount = principal,
                            balanceRemaining = calc.totalRepayment.toDouble(),
                            frequency = "DAILY",
                            termCount = terms,
                            amortization = calc.installmentAmount.toDouble(),
                            status = "PENDING_APPROVAL"
                        )
                        onApplyLoan(newLoan)
                    }
                }
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
