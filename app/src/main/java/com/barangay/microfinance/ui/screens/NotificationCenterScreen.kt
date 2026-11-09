package com.barangay.microfinance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val category: String, // "OVERDUE", "REMINDER", "APPROVAL", "MATURITY"
    val timestamp: String,
    val recipientPhone: String?,
    var isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val notificationsList = remember {
        mutableStateListOf(
            AppNotification(
                id = "N1",
                title = "Overdue Payment Alert",
                message = "Teresa Villanueva (Purok 4) is 14 days overdue for LN-DAILY-0129. Balance: ₱250.00 + ₱25.00 late fee.",
                category = "OVERDUE",
                timestamp = "15 mins ago",
                recipientPhone = "+639255550111",
                isRead = false
            ),
            AppNotification(
                id = "N2",
                title = "Loan Application Approved",
                message = "Loan LN-DAILY-0112 for Juanito Del Rosario (₱5,000.00) was approved by Admin.",
                category = "APPROVAL",
                timestamp = "1 hour ago",
                recipientPhone = "+639235550999",
                isRead = false
            ),
            AppNotification(
                id = "N3",
                title = "Upcoming Daily Installment",
                message = "Elena Santos (Aling Nena) daily due of ₱150.00 scheduled for collection today at Purok 1.",
                category = "REMINDER",
                timestamp = "2 hours ago",
                recipientPhone = "+639175550123",
                isRead = true
            ),
            AppNotification(
                id = "N4",
                title = "Loan Maturity Reminder",
                message = "Loan LN-WEEKLY-0105 for Maria Clara Reyes matures in 7 days. Remaining: ₱18,500.00.",
                category = "MATURITY",
                timestamp = "Yesterday",
                recipientPhone = "+639215550789",
                isRead = true
            )
        )
    }

    val categories = listOf("ALL", "OVERDUE", "REMINDER", "APPROVAL", "MATURITY")

    val filteredNotifications = notificationsList.filter { notif ->
        if (selectedCategoryFilter == "ALL") true
        else notif.category == selectedCategoryFilter
    }

    val unreadCount = notificationsList.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Notification Center", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("$unreadCount Unread System Alerts & Reminders", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            notificationsList.forEach { it.isRead = true }
                        }
                    ) {
                        Text("Mark All Read", fontSize = 12.sp)
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
            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = (selectedCategoryFilter == cat),
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notifications List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notifications in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        NotificationCard(
                            notif = notif,
                            onSendSms = {
                                notif.recipientPhone?.let { phone ->
                                    Toast.makeText(context, "Sending Automated SMS to $phone...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onMarkRead = {
                                notif.isRead = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: AppNotification,
    onSendSms: () -> Unit,
    onMarkRead: () -> Unit
) {
    val (icon, color, bgColor) = when (notif.category) {
        "OVERDUE" -> Triple(Icons.Default.Warning, Color(0xFFDC2626), Color(0xFFFEF2F2))
        "APPROVAL" -> Triple(Icons.Default.CheckCircle, Color(0xFF059669), Color(0xFFECFDF5))
        "MATURITY" -> Triple(Icons.Default.Schedule, Color(0xFFD97706), Color(0xFFFFFBEB))
        else -> Triple(Icons.Default.Notifications, Color(0xFF2563EB), Color(0xFFEFF6FF))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarkRead() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notif.isRead) 1.dp else 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notif.title,
                        fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(text = notif.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notif.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    notif.recipientPhone?.let {
                        OutlinedButton(
                            onClick = onSendSms,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send SMS Alert", fontSize = 11.sp)
                        }
                    }

                    if (!notif.isRead) {
                        TextButton(
                            onClick = onMarkRead,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Mark Read", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
