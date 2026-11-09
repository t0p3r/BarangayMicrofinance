package com.barangay.microfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class SavingsAccountItem(
    val id: String,
    val accountNumber: String, // e.g. "SAV-2026-0012"
    val memberName: String,
    val purok: String,
    var currentBalance: Double,
    val interestRateApy: Double = 2.5, // 2.5% per annum
    var lastTransactionDate: String
)

data class SavingsTransactionItem(
    val id: String,
    val receiptNumber: String,
    val memberName: String,
    val type: String, // "DEPOSIT" or "WITHDRAWAL"
    val amount: Double,
    val paymentMethod: String,
    val timestamp: String
)

data class ExpenseEntry(
    val category: String,
    val description: String,
    val amount: Double,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Member Savings Accounts, 1: Accounting & Statements
    val pesoFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "PH")) }

    val savingsAccounts = remember {
        mutableStateListOf(
            SavingsAccountItem(
                id = "S1",
                accountNumber = "SAV-2026-0012",
                memberName = "Elena Santos (Aling Nena)",
                purok = "Purok 1 - Sampaguita",
                currentBalance = 18500.0,
                lastTransactionDate = "Nov 08, 2026"
            ),
            SavingsAccountItem(
                id = "S2",
                accountNumber = "SAV-2026-0018",
                memberName = "Maria Clara Reyes",
                purok = "Purok 3 - Maharlika",
                currentBalance = 42000.0,
                lastTransactionDate = "Nov 07, 2026"
            ),
            SavingsAccountItem(
                id = "S3",
                accountNumber = "SAV-2026-0025",
                memberName = "Roberto Cruz (Mang Bert)",
                purok = "Purok 2 - Masagana",
                currentBalance = 9800.0,
                lastTransactionDate = "Nov 05, 2026"
            ),
            SavingsAccountItem(
                id = "S4",
                accountNumber = "SAV-2026-0031",
                memberName = "Juanito Del Rosario",
                purok = "Purok 1 - Sampaguita",
                currentBalance = 5400.0,
                lastTransactionDate = "Nov 08, 2026"
            ),
            SavingsAccountItem(
                id = "S5",
                accountNumber = "SAV-2026-0040",
                memberName = "Teresa Villanueva",
                purok = "Purok 4 - Maligaya",
                currentBalance = 12600.0,
                lastTransactionDate = "Nov 02, 2026"
            )
        )
    }

    val savingsTransactions = remember {
        mutableStateListOf(
            SavingsTransactionItem(
                id = "ST1",
                receiptNumber = "SAV-REC-20261108-0012",
                memberName = "Elena Santos",
                type = "DEPOSIT",
                amount = 500.0,
                paymentMethod = "CASH",
                timestamp = "Nov 08, 2026 10:30 AM"
            )
        )
    }

    var showTransactionDialog by remember { mutableStateOf(false) }

    val totalSavingsPool = savingsAccounts.sumOf { it.currentBalance }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Savings & Accounting Ledger", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Barangay Microfinance Vault", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { showTransactionDialog = true }) {
                        Icon(imageVector = Icons.Default.Savings, contentDescription = "Deposit / Withdraw")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showTransactionDialog = true },
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                text = { Text("Deposit / Withdraw") },
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
            // Screen Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Savings (${savingsAccounts.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Accounting & Statements") }
                )
            }

            if (selectedTab == 0) {
                // Hero Savings Pool Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL MEMBER SAVINGS POOL", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Surface(color = Color(0xFF059669), shape = RoundedCornerShape(12.dp)) {
                                Text("2.5% P.A. Interest", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(pesoFormat.format(totalSavingsPool), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showTransactionDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Deposit")
                            }
                            OutlinedButton(
                                onClick = { showTransactionDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Withdraw")
                            }
                        }
                    }
                }

                // Member Savings Accounts List
                Text("Member Savings Accounts", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savingsAccounts, key = { it.id }) { acc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECFDF5)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(acc.memberName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${acc.accountNumber} • ${acc.purok}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(pesoFormat.format(acc.currentBalance), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("Updated ${acc.lastTransactionDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                // Accounting & Statements Tab
                AccountingStatementsView(pesoFormat = pesoFormat)
            }
        }
    }

    if (showTransactionDialog) {
        SavingsTransactionDialog(
            accounts = savingsAccounts,
            pesoFormat = pesoFormat,
            onDismiss = { showTransactionDialog = false },
            onConfirmTransaction = { acc, type, amount, method ->
                if (type == "DEPOSIT") {
                    acc.currentBalance += amount
                } else {
                    acc.currentBalance = (acc.currentBalance - amount).coerceAtLeast(0.0)
                }
                acc.lastTransactionDate = "Nov 08, 2026"

                savingsTransactions.add(
                    0,
                    SavingsTransactionItem(
                        id = "ST${(100..999).random()}",
                        receiptNumber = "SAV-REC-20261108-${(1000..9999).random()}",
                        memberName = acc.memberName,
                        type = type,
                        amount = amount,
                        paymentMethod = method,
                        timestamp = "Nov 08, 2026 11:00 AM"
                    )
                )

                showTransactionDialog = false
            }
        )
    }
}

@Composable
fun AccountingStatementsView(pesoFormat: NumberFormat) {
    val scrollState = rememberScrollState()

    var showExpenseDialog by remember { mutableStateOf(false) }
    val expenseList = remember {
        mutableStateListOf(
            ExpenseEntry("SUPPLIES", "Official Receipt Booklet Printing", 1200.0, "Nov 05, 2026"),
            ExpenseEntry("TRANSPORT", "Field Collector Fuel Allowance", 800.0, "Nov 06, 2026")
        )
    }

    val totalExpenses = expenseList.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cash Liquidity & Vault Breakdown
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
                    Text("Cash Liquidity & Vault Ledger", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(8.dp)) {
                        Text("Reconciled", fontSize = 10.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                AccountingRow("Cash in Barangay Vault:", pesoFormat.format(125000.00))
                AccountingRow("Field Collections in Transit:", pesoFormat.format(18450.00))
                AccountingRow("LandBank Partner Account:", pesoFormat.format(341800.00))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AccountingRow("Total Liquidity:", pesoFormat.format(485250.00), isBold = true)
            }
        }

        // Monthly Income & Expenses
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
                    Text("Monthly Financial Income & Expense", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showExpenseDialog = true }) {
                        Text("+ Record Expense", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                AccountingRow("Loan Interest Income:", pesoFormat.format(14500.00))
                AccountingRow("Processing & Fee Income:", pesoFormat.format(2800.00))
                AccountingRow("Operating Expenses:", "-${pesoFormat.format(totalExpenses)}")
                AccountingRow("Member Savings Interest Expense:", "-${pesoFormat.format(850.00)}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AccountingRow("Net Operating Surplus:", pesoFormat.format(17300.00 - totalExpenses - 850.00), isBold = true)
            }
        }

        // Cash Flow Statement Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cash Flow Statement (Current Month)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(10.dp))
                AccountingRow("Operating Inflow (Collections):", pesoFormat.format(45200.00))
                AccountingRow("Operating Outflow (Disbursements):", "-${pesoFormat.format(38000.00)}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AccountingRow("Net Cash Flow Position:", pesoFormat.format(7200.00), isBold = true)
            }
        }

        // Export Statement Action Button
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Export Monthly Financial Statement (PDF / Excel)")
        }
    }

    if (showExpenseDialog) {
        RecordExpenseDialog(
            onDismiss = { showExpenseDialog = false },
            onSaveExpense = { category, desc, amount ->
                expenseList.add(0, ExpenseEntry(category, desc, amount, "Nov 08, 2026"))
                showExpenseDialog = false
            }
        )
    }
}

@Composable
fun AccountingRow(label: String, value: String, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 13.sp, color = if (isBold) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, fontSize = 13.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold, color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordExpenseDialog(
    onDismiss: () -> Unit,
    onSaveExpense: (category: String, desc: String, amount: Double) -> Unit
) {
    var category by remember { mutableStateOf("SUPPLIES") }
    var desc by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Operating Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (SUPPLIES/TRANSPORT/SYSTEM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Expense Description *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (PHP)") },
                    prefix = { Text("₱ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (desc.isNotBlank()) {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        onSaveExpense(category, desc.trim(), amount)
                    }
                }
            ) { Text("Save Expense") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsTransactionDialog(
    accounts: List<SavingsAccountItem>,
    pesoFormat: NumberFormat,
    onDismiss: () -> Unit,
    onConfirmTransaction: (acc: SavingsAccountItem, type: String, amount: Double, method: String) -> Unit
) {
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
    var transactionType by remember { mutableStateOf("DEPOSIT") }
    var amountInput by remember { mutableStateOf("500") }
    var selectedMethod by remember { mutableStateOf("CASH") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Savings Deposit / Withdrawal", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select Member Account", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                LazyColumn(modifier = Modifier.height(100.dp)) {
                    items(accounts) { acc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAccount = acc }
                                .background(
                                    if (selectedAccount == acc) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(acc.memberName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(pesoFormat.format(acc.currentBalance), fontSize = 12.sp)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("DEPOSIT", "WITHDRAWAL").forEach { type ->
                        FilterChip(
                            selected = (transactionType == type),
                            onClick = { transactionType = type },
                            label = { Text(type) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (PHP)") },
                    prefix = { Text("₱ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Channel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("CASH", "GCASH", "MAYA").forEach { method ->
                        FilterChip(
                            selected = (selectedMethod == method),
                            onClick = { selectedMethod = method },
                            label = { Text(method) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedAccount?.let { acc ->
                        val amount = amountInput.toDoubleOrNull() ?: 500.0
                        onConfirmTransaction(acc, transactionType, amount, selectedMethod)
                    }
                }
            ) {
                Text("Submit Transaction")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
