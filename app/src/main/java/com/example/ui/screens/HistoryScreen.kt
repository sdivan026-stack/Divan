package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.EstimateEntity
import com.example.data.model.ConstructionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.BandhkamViewModel
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: BandhkamViewModel,
    onEstimateSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estimates by viewModel.savedEstimates.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var estimateToDelete by remember { mutableStateOf<EstimateEntity?>(null) }

    val filteredList = remember(estimates, searchQuery) {
        if (searchQuery.isBlank()) {
            estimates
        } else {
            estimates.filter {
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                        it.siteLocation.contains(searchQuery, ignoreCase = true) ||
                        it.billNumber.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    if (estimateToDelete != null) {
        AlertDialog(
            onDismissRequest = { estimateToDelete = null },
            title = { Text("બિલ ડિલીટ કરવું છે?") },
            text = { Text("${estimateToDelete?.customerName} નું બિલ (${estimateToDelete?.billNumber}) ડિલીટ થઈ જશે.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        estimateToDelete?.let { viewModel.deleteEstimate(it) }
                        estimateToDelete = null
                    }
                ) {
                    Text("ડિલીટ કરો", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { estimateToDelete = null }) {
                    Text("રદ કરો")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "સાચવેલા બિલ અને હિસ્ટ્રી",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald800,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ગ્રાહક, સ્થળ અથવા બિલ નં. શોધો...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_estimate_input"),
                singleLine = true
            )

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Emerald100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FolderOpen,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = if (searchQuery.isBlank()) "કોઈ બિલ સાચવેલ નથી" else "કોઈ મેળ ખાતું બિલ મળ્યું નથી",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )

                        Text(
                            text = "કેલ્ક્યુલેટરમાંથી 'અંદાજ સેવ કરો' બટન દબાવીને ગ્રાહકના બિલ અહીં સાચવી શકો છો.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        EstimateCard(
                            estimate = item,
                            onClick = {
                                viewModel.loadEstimate(item)
                                onEstimateSelected()
                            },
                            onDelete = {
                                estimateToDelete = item
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstimateCard(
    estimate: EstimateEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val type = ConstructionType.fromId(estimate.constructionTypeId)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("estimate_item_${estimate.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Emerald100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = estimate.billNumber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = FormatUtils.formatDate(estimate.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = estimate.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "${estimate.siteLocation} • ${estimate.baseAreaSqFt.toInt()} sqft (${estimate.floors} માળ)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Text(
                        text = type.shortGu,
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald700,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatCurrency(estimate.grandTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Emerald800
                    )
                    Text(
                        text = "બિલ જુઓ ➔",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Amber800
                    )
                }
            }
        }
    }
}
