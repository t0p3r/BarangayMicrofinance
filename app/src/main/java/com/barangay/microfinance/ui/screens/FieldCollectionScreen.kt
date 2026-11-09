package com.barangay.microfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CollectionRouteItem(
    val borrowerId: String,
    val loanId: String,
    val borrowerName: String,
    val alias: String?,
    val purok: String,
    val livelihood: String,
    val loanNumber: String,
    val dueAmount: Double,
    var status: String, // "PENDING", "COLLECTED", "OVERDUE"
    var collectedAmount: Double = 0.0,
    var penaltyFee: Double = 0.0,
    var paymentMethod: String = "CASH",
    var receiptNumber: String? = null,
    var paymentTimestamp: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldCollectionScreen(
    collectorName: String,
    barangay: String,
    isOffline: Boolean,
    pendingSyncCount: Int,
    onCollectPaymentClick: (borrowerId: String, loanId: String) -> Unit,
    onTriggerSyncClick: () -> Unit
) {
    var selectedScreenTab by remember { mutableIntStateOf(0) } // 0: Today's Route, 1: Payment History Ledger
    val pesoFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "PH")) }

    // Initial sample route data for Barangay San Isidro
    val routeItems = remember {
        mutableStateListOf(
            CollectionRouteItem(
                borrowerId = "B001",
                loanId = "LN-2026-001",
                borrowerName = "Elena Santos",
                alias = "Aling Nena",
                purok = "Purok 1 - Sampaguita",
                livelihood = "Sari-sari Store",
                loanNumber = "LN-DAILY-0042",
                dueAmount = 150.0,
                status = "PENDING"
            ),
            CollectionRouteItem(
                borrowerId = "B002",
                loanId = "LN-2026-002",
                borrowerName = "Roberto Cruz",
                alias = "Mang Bert",
                purok = "Purok 2 - Masagana",
                livelihood = "Tricycle Driver",
                loanNumber = "LN-DAILY-0081",
                dueAmount = 200.0,
                status = "PENDING"
            ),
            CollectionRouteItem(
                borrowerId = "B003",
                loanId = "LN-2026-003",
                borrowerName = "Maria Clara Reyes",
                alias = null,
                purok = "Purok 3 - Maharlika",
                livelihood = "Carinderia",
                loanNumber = "LN-DAILY-0105",
                dueAmount = 350.0,
                status = "COLLECTED",
                collectedAmount = 350.0,
                paymentMethod = "GCASH",
                receiptNumber = "OR-20261108-0041",
                paymentTimestamp = "Nov 08, 2026 09:15 AM"
            ),
            CollectionRouteItem(
                borrowerId = "B004",
                loanId = "LN-2026-004",
                borrowerName = "Juanito Del Rosario",
                alias = "Ka Nito",
                purok = "Purok 1 - Sampaguita",
                livelihood = "Vegetable Vendor",
                loanNumber = "LN-DAILY-0112",
                dueAmount = 100.0,
                status = "PENDING"
            ),
            CollectionRouteItem(
                borrowerId = "B005",
                loanId = "LN-2026-005",
                borrowerName = "Teresa Villanueva",
                alias = "Tessie",
                purok = "Purok 4 - Maligaya",
                livelihood = "Dressmaking",
                loanNumber = "LN-DAILY-0129",
                dueAmount = 250.0,
                penaltyFee = 25.0,
                status = "OVERDUE"
            )
        )
    }

    var selectedItemForPayment by remember { mutableStateOf<CollectionRouteItem?>(null) }
    var itemForReceiptView by remember { mutableStateOf<CollectionRouteItem?>(null) }

    // Summary calculation
    val totalTarget = routeItems.sumOf { it.dueAmount }
    val totalCollected = routeItems.filter { it.status == "COLLECTED" }.sumOf { it.collectedAmount }
    val collectedCount = routeItems.count { it.status == "COLLECTED" }
    val progressFraction = if (totalTarget > 0) (totalCollected / totalTarget).toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = barangay,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Collector: $collectorName",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onTriggerSyncClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isOffline) Color(0xFFF59E0B) else Color(0xFF10B981),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                            contentDescription = "Sync Status",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOffline) "Offline ($pendingSyncCount)" else "Synced",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Screen Section Tabs
            TabRow(selectedTabIndex = selectedScreenTab) {
                Tab(
                    selected = selectedScreenTab == 0,
                    onClick = { selectedScreenTab = 0 },
                    text = { Text("Daily Route (${routeItems.size})") }
                )
                Tab(
                    selected = selectedScreenTab == 1,
                    onClick = { selectedScreenTab = 1 },
                    text = { Text("Receipt Ledger (${routeItems.count { it.status == "COLLECTED" }})") }
                )
            }

            if (selectedScreenTab == 0) {
                // Summary Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Today's Collection Progress",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${pesoFormat.format(totalCollected)} / ${pesoFormat.format(totalTarget)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = "$collectedCount / ${routeItems.size} Paid",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progressFraction.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF10B981),
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                        )
                    }
                }

                // Collection Route List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(routeItems, key = { it.loanId }) { item ->
                        BorrowerCollectionCard(
                            item = item,
                            pesoFormat = pesoFormat,
                            onCollectClick = {
                                selectedItemForPayment = item
                                onCollectPaymentClick(item.borrowerId, item.loanId)
                            },
                            onViewReceiptClick = {
                                itemForReceiptView = item
                            }
                        )
                    }
                }
            } else {
                // Receipt Ledger View
                val paidItems = routeItems.filter { it.status == "COLLECTED" }
                if (paidItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No payment receipts generated today yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(paidItems) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { itemForReceiptView = item },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.receiptNumber ?: "OR-20261108-0000", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${item.borrowerName} • ${item.loanNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(item.paymentTimestamp ?: "", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(pesoFormat.format(item.collectedAmount), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF059669))
                                        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(6.dp)) {
                                            Text(item.paymentMethod, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Dialog / Bottom Sheet
    selectedItemForPayment?.let { item ->
        PaymentCollectionDialog(
            item = item,
            onDismiss = { selectedItemForPayment = null },
            onConfirmPayment = { amount, penalty, method ->
                val newReceiptNo = "OR-20261108-${(1000..9999).random()}"
                val timestampStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))

                item.status = "COLLECTED"
                item.collectedAmount = amount + penalty
                item.penaltyFee = penalty
                item.paymentMethod = method
                item.receiptNumber = newReceiptNo
                item.paymentTimestamp = timestampStr

                selectedItemForPayment = null
                itemForReceiptView = item // Immediately display receipt preview
            }
        )
    }

    // Digital Official Receipt Modal Dialog
    itemForReceiptView?.let { item ->
        OfficialReceiptDialog(
            item = item,
            collectorName = collectorName,
            barangay = barangay,
            pesoFormat = pesoFormat,
            onDismiss = { itemForReceiptView = null }
        )
    }
}

@Composable
fun BorrowerCollectionCard(
    item: CollectionRouteItem,
    pesoFormat: NumberFormat,
    onCollectClick: () -> Unit,
    onViewReceiptClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (item.alias != null) "${item.borrowerName} (\"${item.alias}\")" else item.borrowerName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = item.purok, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Livelihood: ${item.livelihood}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Daily Due (${item.loanNumber})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = pesoFormat.format(item.dueAmount + item.penaltyFee),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (item.penaltyFee > 0 && item.status != "COLLECTED") {
                        Text(text = "Includes ₱${item.penaltyFee} Late Fee", fontSize = 10.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                }

                if (item.status == "COLLECTED") {
                    OutlinedButton(onClick = onViewReceiptClick) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Receipt", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onCollectClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.status == "OVERDUE") Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Collect", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "COLLECTED" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "Collected")
        "OVERDUE" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Overdue")
        else -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "Pending")
    }

    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCollectionDialog(
    item: CollectionRouteItem,
    onDismiss: () -> Unit,
    onConfirmPayment: (amount: Double, penalty: Double, method: String) -> Unit
) {
    var amountInput by remember { mutableStateOf(item.dueAmount.toString()) }
    var penaltyInput by remember { mutableStateOf(if (item.status == "OVERDUE") "25.0" else "0.0") }
    var selectedMethod by remember { mutableStateOf("CASH") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Record Borrower Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Borrower: ${item.borrowerName}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Loan No: ${item.loanNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Base Payment Amount (PHP)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("₱ ") }
                )

                OutlinedTextField(
                    value = penaltyInput,
                    onValueChange = { penaltyInput = it },
                    label = { Text("Late Fee / Penalty (PHP)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("₱ ") }
                )

                Text(text = "Payment Method Channel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                    val amount = amountInput.toDoubleOrNull() ?: item.dueAmount
                    val penalty = penaltyInput.toDoubleOrNull() ?: 0.0
                    onConfirmPayment(amount, penalty, selectedMethod)
                }
            ) {
                Text("Confirm & Issue Receipt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun OfficialReceiptDialog(
    item: CollectionRouteItem,
    collectorName: String,
    barangay: String,
    pesoFormat: NumberFormat,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = barangay.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(text = "OFFICIAL PAYMENT RECEIPT", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = item.receiptNumber ?: "OR-20261108-0000", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                ReceiptDetailRow(label = "Date & Time", value = item.paymentTimestamp ?: "Nov 08, 2026")
                ReceiptDetailRow(label = "Borrower", value = item.borrowerName)
                ReceiptDetailRow(label = "Loan Contract", value = item.loanNumber)
                ReceiptDetailRow(label = "Payment Channel", value = item.paymentMethod)
                ReceiptDetailRow(label = "Base Amortization", value = pesoFormat.format(item.dueAmount))
                if (item.penaltyFee > 0) {
                    ReceiptDetailRow(label = "Late Penalty Fee", value = pesoFormat.format(item.penaltyFee))
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Total Collected:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = pesoFormat.format(item.collectedAmount.takeIf { it > 0 } ?: (item.dueAmount + item.penaltyFee)), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Collector: $collectorName", fontSize = 11.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Print / Share Receipt")
            }
        }
    )
}

@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
