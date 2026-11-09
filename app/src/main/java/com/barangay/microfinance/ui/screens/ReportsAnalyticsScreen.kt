package com.barangay.microfinance.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class DelinquentBorrowerReportItem(
    val borrowerName: String,
    val loanNumber: String,
    val purok: String,
    val overdueAmount: Double,
    val daysOverdue: Int,
    val agingBucket: String // "1-30 Days", "31-60 Days", "60+ Days"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAnalyticsScreen(
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val pesoFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "PH")) }
    val scrollState = rememberScrollState()

    val delinquentList = remember {
        listOf(
            DelinquentBorrowerReportItem(
                borrowerName = "Teresa Villanueva",
                loanNumber = "LN-DAILY-0129",
                purok = "Purok 4 - Maligaya",
                overdueAmount = 250.0,
                daysOverdue = 14,
                agingBucket = "1-30 Days"
            ),
            DelinquentBorrowerReportItem(
                borrowerName = "Juanito Del Rosario",
                loanNumber = "LN-DAILY-0112",
                purok = "Purok 1 - Sampaguita",
                overdueAmount = 100.0,
                daysOverdue = 5,
                agingBucket = "1-30 Days"
            ),
            DelinquentBorrowerReportItem(
                borrowerName = "Mario Santos",
                loanNumber = "LN-WEEKLY-0088",
                purok = "Purok 2 - Masagana",
                overdueAmount = 3500.0,
                daysOverdue = 45,
                agingBucket = "31-60 Days"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Barangay Analytics & Reports", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Portfolio Aging & Collection Efficiency", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Exporting Master Report to Excel & PDF...", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export All")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Collection Efficiency Gauge Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("MONTHLY COLLECTION EFFICIENCY", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("94.2%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
                                Text("PAR 30 (Risk Rate)", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text("3.8%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 0.942f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF334155)
                    )
                }
            }

            // Loan Portfolio Aging Analysis Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Portfolio Loan Aging Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    AgingRow("Current / On-Time:", pesoFormat.format(465000.00), "96.2%", Color(0xFF059669))
                    AgingRow("1 - 30 Days Overdue:", pesoFormat.format(12400.00), "2.5%", Color(0xFFD97706))
                    AgingRow("31 - 60 Days Overdue:", pesoFormat.format(5000.00), "1.0%", Color(0xFFEA580C))
                    AgingRow("60+ Days Default:", pesoFormat.format(2850.00), "0.3%", Color(0xFFDC2626))
                }
            }

            // Delinquent Borrower Report
            Text("Delinquent Borrower Master List", fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                delinquentList.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.borrowerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${item.loanNumber} • ${item.purok}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${item.daysOverdue} days late", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(pesoFormat.format(item.overdueAmount), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFDC2626))
                                Surface(color = Color(0xFFFEF2F2), shape = RoundedCornerShape(6.dp)) {
                                    Text(item.agingBucket, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Export Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Generating Excel Spreadsheet (.xlsx)...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Excel", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Generating PDF Report...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export PDF", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AgingRow(label: String, amountStr: String, percentageStr: String, badgeColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(badgeColor, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = amountStr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(color = badgeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                Text(text = percentageStr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}
