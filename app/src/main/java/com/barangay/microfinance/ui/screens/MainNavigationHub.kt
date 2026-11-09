package com.barangay.microfinance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : NavDestination("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Borrowers : NavDestination("borrowers", "Borrowers", Icons.Default.People)
    object Loans : NavDestination("loans", "Loans", Icons.Default.Calculate)
    object Collections : NavDestination("collections", "Collections", Icons.Default.Route)
    object Financials : NavDestination("financials", "Financials", Icons.Default.AccountBalance)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationHub() {
    var currentDestination by remember { mutableStateOf<NavDestination>(NavDestination.Dashboard) }
    val context = LocalContext.current

    val destinations = listOf(
        NavDestination.Dashboard,
        NavDestination.Borrowers,
        NavDestination.Loans,
        NavDestination.Collections,
        NavDestination.Financials
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp
            ) {
                destinations.forEach { dest ->
                    NavigationBarItem(
                        selected = (currentDestination == dest),
                        onClick = { currentDestination = dest },
                        icon = {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.title
                            )
                        },
                        label = {
                            Text(text = dest.title)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                NavDestination.Dashboard -> {
                    MainDashboardScreen(
                        collectorName = "Christoper Datu",
                        barangay = "Barangay San Isidro",
                        isOffline = false,
                        pendingSyncCount = 0,
                        onNavigateToNewLoan = { currentDestination = NavDestination.Loans },
                        onNavigateToBorrowers = { currentDestination = NavDestination.Borrowers },
                        onNavigateToCollections = { currentDestination = NavDestination.Collections },
                        onTriggerSync = {
                            Toast.makeText(context, "Triggering WorkManager Sync...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                NavDestination.Borrowers -> {
                    BorrowerManagementScreen(
                        onBorrowerSelect = { borrower ->
                            Toast.makeText(context, "Selected borrower: ${borrower.firstName} ${borrower.lastName}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                NavDestination.Loans -> {
                    LoanManagementScreen(
                        onNavigateToBorrowers = { currentDestination = NavDestination.Borrowers }
                    )
                }
                NavDestination.Collections -> {
                    FieldCollectionScreen(
                        collectorName = "Christoper Datu",
                        barangay = "Barangay San Isidro",
                        isOffline = false,
                        pendingSyncCount = 0,
                        onCollectPaymentClick = { borrowerId, loanId -> },
                        onTriggerSyncClick = {
                            Toast.makeText(context, "Triggering WorkManager Sync...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                NavDestination.Financials -> {
                    FinancialsScreen()
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
