package com.barangay.microfinance.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class DashboardKpi(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val containerColor: Color,
    val iconColor: Color
)

data class DashboardTransaction(
    val id: String,
    val borrowerName: String,
    val amount: Double,
    val type: String, // "PAYMENT", "DISBURSEMENT", "SAVINGS_DEPOSIT"
    val timeAgo: String,
    val paymentMethod: String,
    val receiptNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    collectorName: String = "Christoper Datu",
    barangay: String = "Barangay San Isidro",
    isOffline: Boolean = false,
    pendingSyncCount: Int = 0,
    onNavigateToNewLoan: () -> Unit = {},
    onNavigateToBorrowers: () -> Unit = {},
    onNavigateToCollections: () -> Unit = {},
    onTriggerSync: () -> Unit = {}
) {
    val pesoFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "PH")) }
    val scrollState = rememberScrollState()

    // Sample data for dashboard metrics
    val totalOutstanding = 485250.00
    val monthlyIncome = 45200.00
    val monthlyExpenses = 28500.00
    val activeBorrowersCount = 42
    val todayCollection = 4250.00
    val todayTarget = 6500.00
    val pendingLoansCount = 3
    val overdueAmount = 12400.00

    val kpiList = listOf(
        DashboardKpi(
            title = "Active Borrowers",
            value = "$activeBorrowersCount Residents",
            subtitle = "3 Pending Approval",
            icon = Icons.Default.Group,
            containerColor = Color(0xFFEFF6FF),
            iconColor = Color(0xFF2563EB)
        ),
        DashboardKpi(
            title = "Today's Collection",
            value = pesoFormat.format(todayCollection),
            subtitle = "Target: ${pesoFormat.format(todayTarget)}",
            icon = Icons.Default.Payments,
            containerColor = Color(0xFFECFDF5),
            iconColor = Color(0xFF059669)
        ),
        DashboardKpi(
            title = "Overdue Accounts",
            value = pesoFormat.format(overdueAmount),
            subtitle = "5 Default Risk Loans",
            icon = Icons.Default.Warning,
            containerColor = Color(0xFFFEF2F2),
            iconColor = Color(0xFFDC2626)
        ),
        DashboardKpi(
            title = "Pending Loans",
            value = "$pendingLoansCount Applications",
            subtitle = "Requires Authorization",
            icon = Icons.Default.AssignmentLate,
            containerColor = Color(0xFFFFFBEB),
            iconColor = Color(0xFFD97706)
        )
    )

    val recentTransactions = listOf(
        DashboardTransaction(
            id = "1",
            borrowerName = "Elena Santos (Aling Nena)",
            amount = 150.00,
            type = "PAYMENT",
            timeAgo = "10 mins ago",
            paymentMethod = "GCASH",
            receiptNumber = "OR-20261108-0042"
        ),
        DashboardTransaction(
            id = "2",
            borrowerName = "Maria Clara Reyes",
            amount = 350.00,
            type = "PAYMENT",
            timeAgo = "25 mins ago",
            paymentMethod = "CASH",
            receiptNumber = "OR-20261108-0041"
        ),
        DashboardTransaction(
            id = "3",
            borrowerName = "Roberto Cruz (Mang Bert)",
            amount = 15000.00,
            type = "DISBURSEMENT",
            timeAgo = "1 hour ago",
            paymentMethod = "BANK_TRANSFER",
            receiptNumber = "DISB-20261108-0012"
        ),
        DashboardTransaction(
            id = "4",
            borrowerName = "Juanito Del Rosario",
            amount = 500.00,
            type = "SAVINGS_DEPOSIT",
            timeAgo = "2 hours ago",
            paymentMethod = "CASH",
            receiptNumber = "SAV-20261108-0005"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = collectorName.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Good Day, $collectorName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = barangay,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Sync Badge
                FilledTonalIconButton(
                    onClick = onTriggerSync,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isOffline) Color(0xFFFEF3C7) else Color(0xFFD1FAE5),
                        contentColor = if (isOffline) Color(0xFFD97706) else Color(0xFF059669)
                    )
                ) {
                    Icon(
                        imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                        contentDescription = "Sync Status"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Card: Total Outstanding Portfolio Balance
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B) // Dark Navy
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL OUTSTANDING LOAN PORTFOLIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.5.sp
                    )
                    Surface(
                        color = Color(0xFF334155),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "AES-256 Encrypted",
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pesoFormat.format(totalOutstanding),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Income vs Expense Summary Strip inside Hero Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Monthly Collections", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(pesoFormat.format(monthlyIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color(0xFF334155))
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Disbursements", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(pesoFormat.format(monthlyExpenses), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Row
        Text(
            text = "Quick Actions",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                label = "New Loan",
                icon = Icons.Default.AddCard,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToNewLoan
            )
            QuickActionButton(
                label = "Collect",
                icon = Icons.Default.QrCodeScanner,
                color = Color(0xFF059669),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToCollections
            )
            QuickActionButton(
                label = "Borrowers",
                icon = Icons.Default.PersonAdd,
                color = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToBorrowers
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Key Performance Indicators (Grid)
        Text(
            text = "Barangay Portfolio Overview",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(kpi = kpiList[0], modifier = Modifier.weight(1f))
                KpiCard(kpi = kpiList[1], modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(kpi = kpiList[2], modifier = Modifier.weight(1f))
                KpiCard(kpi = kpiList[3], modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Income vs Expenses Chart Card (Reflects design reference 2)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Income vs Disbursements", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Past 6 Months Performance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { }) {
                        Text("Details", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Bar Chart Composable
                IncomeExpenseBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF059669), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Collections", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFF43F5E), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Disbursements", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Activity / Transactions Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Transactions", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { }) {
                Text("View All", fontSize = 12.sp)
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recentTransactions.forEach { tx ->
                TransactionRow(tx = tx, pesoFormat = pesoFormat)
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun KpiCard(kpi: DashboardKpi, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = kpi.containerColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = kpi.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                Icon(imageVector = kpi.icon, contentDescription = null, tint = kpi.iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = kpi.value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = kpi.subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun IncomeExpenseBarChart(modifier: Modifier = Modifier) {
    val months = listOf("Jun", "Jul", "Aug", "Sep", "Oct", "Nov")
    val collectionsData = listOf(0.6f, 0.75f, 0.5f, 0.85f, 0.9f, 0.7f)
    val expensesData = listOf(0.4f, 0.5f, 0.35f, 0.6f, 0.55f, 0.45f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barWidth = 14.dp.toPx()
        val spacing = width / months.size

        months.forEachIndexed { index, month ->
            val xCenter = spacing * index + spacing / 2

            // Collection Bar (Green)
            val collectionHeight = height * collectionsData[index] * 0.8f
            drawRect(
                color = Color(0xFF059669),
                topLeft = Offset(xCenter - barWidth - 2.dp.toPx(), height - collectionHeight),
                size = Size(barWidth, collectionHeight)
            )

            // Expense Bar (Rose)
            val expenseHeight = height * expensesData[index] * 0.8f
            drawRect(
                color = Color(0xFFF43F5E),
                topLeft = Offset(xCenter + 2.dp.toPx(), height - expenseHeight),
                size = Size(barWidth, expenseHeight)
            )
        }
    }
}

@Composable
fun TransactionRow(tx: DashboardTransaction, pesoFormat: NumberFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when (tx.type) {
                                "PAYMENT" -> Color(0xFFECFDF5)
                                "DISBURSEMENT" -> Color(0xFFFEF2F2)
                                else -> Color(0xFFEFF6FF)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            "PAYMENT" -> Icons.Default.ArrowDownward
                            "DISBURSEMENT" -> Icons.Default.ArrowUpward
                            else -> Icons.Default.Savings
                        },
                        contentDescription = null,
                        tint = when (tx.type) {
                            "PAYMENT" -> Color(0xFF059669)
                            "DISBURSEMENT" -> Color(0xFFDC2626)
                            else -> Color(0xFF2563EB)
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = tx.borrowerName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${tx.paymentMethod} • ${tx.timeAgo}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (tx.type == "DISBURSEMENT") "-" else "+") + pesoFormat.format(tx.amount),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (tx.type == "DISBURSEMENT") Color(0xFFDC2626) else Color(0xFF059669)
                )
                Text(text = tx.receiptNumber, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
