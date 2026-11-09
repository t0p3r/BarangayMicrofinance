package com.barangay.microfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class EmployeeUser(
    val id: String,
    val employeeCode: String,
    val fullName: String,
    val username: String,
    val role: String, // "ADMINISTRATOR", "LOAN_OFFICER", "CASHIER", "COLLECTOR", "ACCOUNTANT"
    val approvalLimit: Double,
    val status: String = "ACTIVE"
)

data class ActivityAuditLog(
    val id: String,
    val employeeName: String,
    val role: String,
    val actionText: String,
    val timestamp: String,
    val ipDevice: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onDismiss: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Employees & RBAC, 1: Audit Log
    val pesoFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "PH")) }

    val employees = remember {
        mutableStateListOf(
            EmployeeUser("E1", "EMP-2026-001", "Admin Juan Dela Cruz", "admin.juan", "ADMINISTRATOR", 1000000.0),
            EmployeeUser("E2", "EMP-2026-002", "Christoper Datu", "christoper.collector", "COLLECTOR", 0.0),
            EmployeeUser("E3", "EMP-2026-003", "Maria Santos", "maria.officer", "LOAN_OFFICER", 20000.0),
            EmployeeUser("E4", "EMP-2026-004", "Ana Reyes", "ana.cashier", "CASHIER", 50000.0),
            EmployeeUser("E5", "EMP-2026-005", "Carlos Gutierrez", "carlos.acct", "ACCOUNTANT", 0.0)
        )
    }

    val auditLogs = remember {
        listOf(
            ActivityAuditLog("A1", "Christoper Datu", "COLLECTOR", "Issued OR-20261108-0042 (₱150.00) for Elena Santos", "Nov 08 10:15 AM", "Android POS #42"),
            ActivityAuditLog("A2", "Maria Santos", "LOAN_OFFICER", "Evaluated and submitted LN-DAILY-0112 for approval", "Nov 08 09:30 AM", "Tablet Terminal #02"),
            ActivityAuditLog("A3", "Admin Juan Dela Cruz", "ADMINISTRATOR", "Approved Loan Application LN-DAILY-0112 (₱5,000.00)", "Nov 08 09:45 AM", "Admin Workstation"),
            ActivityAuditLog("A4", "Ana Reyes", "CASHIER", "Recorded Savings Deposit SAV-REC-20261108-0012 (₱500.00)", "Nov 08 10:30 AM", "Vault Counter #01")
        )
    }

    var showAddUserDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Employee & User Management", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Role-Based Access Control (RBAC) & Audit Logs", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddUserDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add User")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddUserDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Employee") },
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Staff Accounts (${employees.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Audit Log (${auditLogs.size})") }
                )
            }

            if (selectedTab == 0) {
                // Employees List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(employees, key = { it.id }) { emp ->
                        EmployeeUserCard(emp = emp, pesoFormat = pesoFormat)
                    }
                }
            } else {
                // Audit Logs List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(auditLogs, key = { it.id }) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(log.employeeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(6.dp)) {
                                        Text(log.role, fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(log.actionText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(log.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(log.ipDevice, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddUserDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddUserDialog = false },
            onSave = { newEmp ->
                employees.add(0, newEmp)
                showAddUserDialog = false
            }
        )
    }
}

@Composable
fun EmployeeUserCard(emp: EmployeeUser, pesoFormat: NumberFormat) {
    val (roleColor, roleBg) = when (emp.role) {
        "ADMINISTRATOR" -> Pair(Color(0xFF7C3AED), Color(0xFFF3E8FF))
        "LOAN_OFFICER" -> Pair(Color(0xFF2563EB), Color(0xFFEFF6FF))
        "CASHIER" -> Pair(Color(0xFF059669), Color(0xFFECFDF5))
        "COLLECTOR" -> Pair(Color(0xFFD97706), Color(0xFFFFFBEB))
        else -> Pair(Color(0xFF475569), Color(0xFFF1F5F9))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(roleBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emp.fullName.take(1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = roleColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(emp.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${emp.employeeCode} • Username: @${emp.username}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = roleBg, shape = RoundedCornerShape(6.dp)) {
                        Text(emp.role, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = roleColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }

                    if (emp.approvalLimit > 0) {
                        Text(
                            text = "Approval Limit: ${if (emp.approvalLimit >= 1000000) "UNLIMITED" else pesoFormat.format(emp.approvalLimit)}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeDialog(
    onDismiss: () -> Unit,
    onSave: (EmployeeUser) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("LOAN_OFFICER") }
    var limitInput by remember { mutableStateOf("20000") }

    val roles = listOf("ADMINISTRATOR", "LOAN_OFFICER", "CASHIER", "COLLECTOR", "ACCOUNTANT")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Staff / User Account", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("System Username *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Assigned Role (RBAC)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                LazyColumn(modifier = Modifier.height(100.dp)) {
                    items(roles) { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRole = r }
                                .background(
                                    if (selectedRole == r) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(r, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = limitInput,
                    onValueChange = { limitInput = it },
                    label = { Text("Approval Limit (PHP)") },
                    prefix = { Text("₱ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && username.isNotBlank()) {
                        val limit = limitInput.toDoubleOrNull() ?: 20000.0
                        val newEmp = EmployeeUser(
                            id = "E${(10..99).random()}",
                            employeeCode = "EMP-2026-${(100..999).random()}",
                            fullName = name.trim(),
                            username = username.trim(),
                            role = selectedRole,
                            approvalLimit = limit
                        )
                        onSave(newEmp)
                    }
                }
            ) { Text("Create Account") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
