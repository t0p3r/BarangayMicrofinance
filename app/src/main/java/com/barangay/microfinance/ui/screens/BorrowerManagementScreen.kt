package com.barangay.microfinance.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barangay.microfinance.data.local.db.BorrowerEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowerManagementScreen(
    onBorrowerSelect: (BorrowerEntity) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPurokFilter by remember { mutableStateOf("ALL") }
    var showRegistrationDialog by remember { mutableStateOf(false) }
    var selectedBorrowerForDetails by remember { mutableStateOf<BorrowerEntity?>(null) }

    // Initial sample database of barangay residents
    val borrowersList = remember {
        mutableStateListOf(
            BorrowerEntity(
                id = "1",
                borrowerCode = "BRGY-2026-0001",
                firstName = "Elena",
                lastName = "Santos",
                alias = "Aling Nena",
                purok = "Purok 1 - Sampaguita",
                barangay = "Barangay San Isidro",
                municipality = "San Mateo",
                livelihood = "Sari-sari Store",
                encryptedContactNumber = "+63 917 555 0123",
                govIdType = "PHILSYS",
                encryptedGovIdNumber = "1234-5678-9012",
                coMakerName = "Pedro Santos",
                encryptedCoMakerContact = "+63 918 555 0124",
                creditScore = 740,
                riskTier = "LOW"
            ),
            BorrowerEntity(
                id = "2",
                borrowerCode = "BRGY-2026-0002",
                firstName = "Roberto",
                lastName = "Cruz",
                alias = "Mang Bert",
                purok = "Purok 2 - Masagana",
                barangay = "Barangay San Isidro",
                municipality = "San Mateo",
                livelihood = "Tricycle Driver",
                encryptedContactNumber = "+63 919 555 0456",
                govIdType = "SSS",
                encryptedGovIdNumber = "34-5678901-2",
                coMakerName = "Rosa Cruz",
                encryptedCoMakerContact = "+63 920 555 0457",
                creditScore = 680,
                riskTier = "MEDIUM"
            ),
            BorrowerEntity(
                id = "3",
                borrowerCode = "BRGY-2026-0003",
                firstName = "Maria Clara",
                lastName = "Reyes",
                alias = "Clara",
                purok = "Purok 3 - Maharlika",
                barangay = "Barangay San Isidro",
                municipality = "San Mateo",
                livelihood = "Carinderia",
                encryptedContactNumber = "+63 921 555 0789",
                govIdType = "PHILHEALTH",
                encryptedGovIdNumber = "12-345678901-2",
                coMakerName = "Antonio Reyes",
                encryptedCoMakerContact = "+63 922 555 0790",
                creditScore = 810,
                riskTier = "LOW"
            ),
            BorrowerEntity(
                id = "4",
                borrowerCode = "BRGY-2026-0004",
                firstName = "Juanito",
                lastName = "Del Rosario",
                alias = "Ka Nito",
                purok = "Purok 1 - Sampaguita",
                barangay = "Barangay San Isidro",
                municipality = "San Mateo",
                livelihood = "Vegetable Vendor",
                encryptedContactNumber = "+63 923 555 0999",
                govIdType = "VOTERS_ID",
                encryptedGovIdNumber = "VIN-987654321",
                coMakerName = "Maria Del Rosario",
                encryptedCoMakerContact = "+63 924 555 0998",
                creditScore = 620,
                riskTier = "MEDIUM"
            ),
            BorrowerEntity(
                id = "5",
                borrowerCode = "BRGY-2026-0005",
                firstName = "Teresa",
                lastName = "Villanueva",
                alias = "Tessie",
                purok = "Purok 4 - Maligaya",
                barangay = "Barangay San Isidro",
                municipality = "San Mateo",
                livelihood = "Dressmaking",
                encryptedContactNumber = "+63 925 555 0111",
                govIdType = "PHILSYS",
                encryptedGovIdNumber = "9876-5432-1098",
                coMakerName = "Fernando Villanueva",
                encryptedCoMakerContact = "+63 926 555 0112",
                creditScore = 540,
                riskTier = "HIGH"
            )
        )
    }

    val purokOptions = listOf("ALL", "Purok 1", "Purok 2", "Purok 3", "Purok 4")

    val filteredBorrowers = borrowersList.filter { borrower ->
        val matchesSearch = borrower.firstName.contains(searchQuery, ignoreCase = true) ||
                borrower.lastName.contains(searchQuery, ignoreCase = true) ||
                (borrower.alias?.contains(searchQuery, ignoreCase = true) == true) ||
                borrower.borrowerCode.contains(searchQuery, ignoreCase = true)

        val matchesPurok = if (selectedPurokFilter == "ALL") true
        else borrower.purok.contains(selectedPurokFilter, ignoreCase = true)

        matchesSearch && matchesPurok
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Borrower Directory", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${borrowersList.size} Registered Barangay Borrowers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { showRegistrationDialog = true }) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Register Borrower")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showRegistrationDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Register Borrower") },
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search by name, alias, or borrower code...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Purok Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(purokOptions) { purok ->
                    FilterChip(
                        selected = (selectedPurokFilter == purok),
                        onClick = { selectedPurokFilter = purok },
                        label = { Text(purok) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Borrower List
            if (filteredBorrowers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonSearch,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No borrowers found matching filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredBorrowers, key = { it.id }) { borrower ->
                        BorrowerCard(
                            borrower = borrower,
                            onClick = {
                                selectedBorrowerForDetails = borrower
                                onBorrowerSelect(borrower)
                            }
                        )
                    }
                }
            }
        }
    }

    // New Borrower Registration Dialog
    if (showRegistrationDialog) {
        BorrowerRegistrationDialog(
            onDismiss = { showRegistrationDialog = false },
            onSaveBorrower = { newBorrower ->
                borrowersList.add(0, newBorrower)
                showRegistrationDialog = false
            }
        )
    }

    // Borrower Detail Modal Sheet
    selectedBorrowerForDetails?.let { borrower ->
        BorrowerDetailBottomSheet(
            borrower = borrower,
            onDismiss = { selectedBorrowerForDetails = null }
        )
    }
}

@Composable
fun BorrowerCard(
    borrower: BorrowerEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Initial Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (borrower.riskTier) {
                            "LOW" -> Color(0xFFD1FAE5)
                            "MEDIUM" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${borrower.firstName.take(1)}${borrower.lastName.take(1)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = when (borrower.riskTier) {
                        "LOW" -> Color(0xFF065F46)
                        "MEDIUM" -> Color(0xFF92400E)
                        else -> Color(0xFF991B1B)
                    }
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (borrower.alias != null) "${borrower.firstName} ${borrower.lastName} (\"${borrower.alias}\")"
                        else "${borrower.firstName} ${borrower.lastName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${borrower.borrowerCode} • ${borrower.purok}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Livelihood Chip
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = borrower.livelihood,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Risk Tier Tag
                    Surface(
                        color = when (borrower.riskTier) {
                            "LOW" -> Color(0xFF10B981)
                            "MEDIUM" -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${borrower.riskTier} RISK (${borrower.creditScore})",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowerRegistrationDialog(
    onDismiss: () -> Unit,
    onSaveBorrower: (BorrowerEntity) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var selectedPurok by remember { mutableStateOf("Purok 1 - Sampaguita") }
    var livelihood by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var govIdType by remember { mutableStateOf("PHILSYS") }
    var govIdNumber by remember { mutableStateOf("") }
    var coMakerName by remember { mutableStateOf("") }
    var coMakerContact by remember { mutableStateOf("") }



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Borrower", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Personal Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                item {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = alias,
                        onValueChange = { alias = it },
                        label = { Text("Moniker / Alias (e.g. Aling Nena)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Address & Income", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                item {
                    OutlinedTextField(
                        value = selectedPurok,
                        onValueChange = { selectedPurok = it },
                        label = { Text("Purok") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = livelihood,
                        onValueChange = { livelihood = it },
                        label = { Text("Livelihood (e.g. Sari-sari Store)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = contactNumber,
                        onValueChange = { contactNumber = it },
                        label = { Text("Mobile Number (+63)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Identity Verification & Co-Maker", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                item {
                    OutlinedTextField(
                        value = govIdType,
                        onValueChange = { govIdType = it },
                        label = { Text("Gov ID Type (PHILSYS/SSS/PHILHEALTH)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = govIdNumber,
                        onValueChange = { govIdNumber = it },
                        label = { Text("Gov ID Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = coMakerName,
                        onValueChange = { coMakerName = it },
                        label = { Text("Co-Maker / Guarantor Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = coMakerContact,
                        onValueChange = { coMakerContact = it },
                        label = { Text("Co-Maker Contact Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank()) {
                        val newBorrower = BorrowerEntity(
                            borrowerCode = "BRGY-2026-${(1000..9999).random()}",
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            alias = alias.ifBlank { null },
                            purok = selectedPurok,
                            barangay = "Barangay San Isidro",
                            municipality = "San Mateo",
                            livelihood = livelihood.ifBlank { "Micro-business" },
                            encryptedContactNumber = contactNumber,
                            govIdType = govIdType,
                            encryptedGovIdNumber = govIdNumber,
                            coMakerName = coMakerName.ifBlank { "N/A" },
                            encryptedCoMakerContact = coMakerContact,
                            creditScore = 700,
                            riskTier = "LOW"
                        )
                        onSaveBorrower(newBorrower)
                    }
                }
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowerDetailBottomSheet(
    borrower: BorrowerEntity,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "${borrower.firstName} ${borrower.lastName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    borrower.alias?.let {
                        Text(text = "Moniker: \"$it\"", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = borrower.borrowerCode,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Contact & ID Info (AES-256 Decrypted View)
            DetailSectionHeader(title = "Encrypted Personal Records")
            DetailRow(label = "Contact Number", value = borrower.encryptedContactNumber)
            DetailRow(label = "Gov ID (${borrower.govIdType})", value = borrower.encryptedGovIdNumber)
            DetailRow(label = "Purok & Address", value = "${borrower.purok}, ${borrower.barangay}")
            DetailRow(label = "Livelihood Source", value = borrower.livelihood)

            Spacer(modifier = Modifier.height(16.dp))
            DetailSectionHeader(title = "Guarantor / Co-Maker")
            DetailRow(label = "Co-Maker Name", value = borrower.coMakerName)
            DetailRow(label = "Co-Maker Contact", value = borrower.encryptedCoMakerContact)

            Spacer(modifier = Modifier.height(16.dp))
            DetailSectionHeader(title = "Credit Rating & Risk")
            DetailRow(label = "Credit Score", value = "${borrower.creditScore} / 850")
            DetailRow(label = "Risk Classification", value = borrower.riskTier)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close Profile")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
