package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.viewmodel.BandhkamViewModel

enum class NavigationTab(
    val titleGu: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    CALCULATOR("કેલ્ક્યુલેટર", Icons.Filled.Calculate, Icons.Outlined.Calculate, "tab_calculator"),
    INVOICE("બિલ પત્રક", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "tab_invoice"),
    HISTORY("હિસ્ટ્રી", Icons.Filled.HistoryEdu, Icons.Outlined.HistoryEdu, "tab_history"),
    TOOLS("સિવિલ ટૂલ્સ", Icons.Filled.Handyman, Icons.Outlined.Handyman, "tab_tools"),
    PROFILE("પ્રોફાઇલ", Icons.Filled.Business, Icons.Outlined.Business, "tab_profile")
}

@Composable
fun MainScreen(
    viewModel: BandhkamViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.CALCULATOR) }
    var tabBackStack by remember { mutableStateOf(listOf(NavigationTab.CALCULATOR)) }

    fun navigateTo(tab: NavigationTab) {
        if (selectedTab != tab) {
            tabBackStack = tabBackStack + tab
            selectedTab = tab
        }
    }

    fun navigateBack() {
        if (tabBackStack.size > 1) {
            val newStack = tabBackStack.dropLast(1)
            tabBackStack = newStack
            selectedTab = newStack.last()
        } else if (selectedTab != NavigationTab.CALCULATOR) {
            selectedTab = NavigationTab.CALCULATOR
        }
    }

    BackHandler(enabled = selectedTab != NavigationTab.CALCULATOR) {
        navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("main_bottom_nav")
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { navigateTo(tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleGu
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleGu,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Emerald800,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = Emerald800
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "TabCrossfade") { tab ->
                when (tab) {
                    NavigationTab.CALCULATOR -> CalculatorScreen(
                        viewModel = viewModel,
                        onNavigateToInvoice = { navigateTo(NavigationTab.INVOICE) }
                    )
                    NavigationTab.INVOICE -> BillInvoiceScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                    NavigationTab.HISTORY -> HistoryScreen(
                        viewModel = viewModel,
                        onEstimateSelected = { navigateTo(NavigationTab.INVOICE) }
                    )
                    NavigationTab.TOOLS -> CivilToolsScreen(
                        viewModel = viewModel,
                        onApplyAreaToCalculator = { navigateTo(NavigationTab.CALCULATOR) }
                    )
                    NavigationTab.PROFILE -> ProfileSettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
