package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BandhkamViewModel
import com.example.util.FormatUtils
import com.example.util.PrintUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillInvoiceScreen(
    viewModel: BandhkamViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val contractorProfile by viewModel.contractorProfile.collectAsState()
    val calculation = viewModel.getCalculation()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "અંદાજિત બાંધકામ બિલ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "બિલ નં: ${uiState.billNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald100
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "પાછા જાઓ",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    val isSpeaking by viewModel.isSpeaking.collectAsState()
                    IconButton(
                        onClick = {
                            if (isSpeaking) {
                                viewModel.stopSpeaking()
                            } else {
                                viewModel.speakCurrentBill()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isSpeaking) "અવાજ બંધ કરો" else "બિલ સાંભળો",
                            tint = if (isSpeaking) Amber500 else Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.generateNewBill()
                            Toast.makeText(context, "નવો બિલ નંબર જનરેટ થયો!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "નવું બિલ",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald800,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            PrintUtils.printBill(
                                context = context,
                                billNo = uiState.billNumber,
                                customerName = uiState.customerName,
                                customerPhone = uiState.customerPhone,
                                siteLocation = uiState.siteLocation,
                                result = calculation,
                                profile = contractorProfile
                            )
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("print_invoice_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("પ્રિન્ટ / PDF", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val summary = FormatUtils.generateWhatsAppSummary(
                                billNo = uiState.billNumber,
                                customerName = uiState.customerName,
                                customerPhone = uiState.customerPhone,
                                siteLocation = uiState.siteLocation,
                                result = calculation,
                                profile = contractorProfile
                            )
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("બાંધકામ બિલ સમરી", summary)
                            clipboard.setPrimaryClip(clip)

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "વોટ્સએપ શેર કરો"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("share_invoice_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Emerald700)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("શેર", color = Emerald800, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.saveEstimate {
                                Toast.makeText(context, "બિલ સેવ થઈ ગયું!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_invoice_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber600),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("સેવ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Automatic Voice Readout Bar
            com.example.ui.components.VoiceReadoutCard(viewModel = viewModel)

            // Printable Sheet Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate300)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("invoice_sheet_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header: Firm & Bill Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contractorProfile.firmName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                            Text(
                                text = "${contractorProfile.proprietorName} • ${contractorProfile.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            Text(
                                text = contractorProfile.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                color = Emerald100,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "બિલ નં: ${uiState.billNumber}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "તારીખ: ${FormatUtils.formatDate()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500
                            )
                        }
                    }

                    Divider(color = Emerald700, thickness = 2.dp)

                    // Customer Details Box
                    Surface(
                        color = Slate50,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ગ્રાહકનું નામ:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                    Text(
                                        text = uiState.customerName.ifBlank { "રમેશભાઈ પટેલ" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    if (uiState.customerPhone.isNotBlank()) {
                                        Text(uiState.customerPhone, style = MaterialTheme.typography.bodySmall, color = Slate600)
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("સાઇટનું સ્થળ:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                    Text(
                                        text = uiState.siteLocation.ifBlank { "ગુજરાત" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                }
                            }

                            Divider(color = Slate200)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("કુલ એરિયા x માળ:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                    Text(
                                        text = "${FormatUtils.formatIndianNumber(calculation.baseAreaSqFt)} sqft (${calculation.floors} માળ)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald800
                                    )
                                    Text(
                                        text = "કુલ: ${FormatUtils.formatIndianNumber(calculation.totalBuiltupArea)} sqft",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("બાંધકામનો પ્રકાર:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                    Text(
                                        text = calculation.constructionType.shortGu,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                }
                            }
                        }
                    }

                    // Itemized Table Header
                    Surface(
                        color = Slate100,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ક્રમ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                            Text("સામગ્રી / વિગત", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("જથ્થો", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.width(68.dp))
                            Text("દર (રૂ.)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.width(60.dp))
                            Text("કુલ (રૂ.)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.width(74.dp))
                        }
                    }

                    // Table Rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        calculation.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.speakItem(item) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.srNo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate500,
                                    modifier = Modifier.width(28.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeDown,
                                            contentDescription = "Speak item",
                                            tint = Emerald700,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = item.nameGu,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate900
                                        )
                                    }
                                    if (item.secondaryInfo != null) {
                                        Text(
                                            text = item.secondaryInfo,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate500,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "${FormatUtils.formatIndianNumber(item.quantity, item.quantity % 1.0 != 0.0)} ${item.unitGu}",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(68.dp)
                                )

                                Text(
                                    text = "₹${FormatUtils.formatIndianNumber(item.rate, item.rate % 1.0 != 0.0)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(60.dp)
                                )

                                Text(
                                    text = "₹${FormatUtils.formatIndianNumber(item.totalCost)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald800,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(74.dp)
                                )
                            }
                            Divider(color = Slate100)
                        }
                    }

                    // Grand Total Box
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Emerald200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "કુલ અંદાજિત રકમ (Grand Total)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                                Text(
                                    text = "અંદાજિત દર: ₹${FormatUtils.formatIndianNumber(calculation.costPerSqFt)} / sqft",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Emerald700
                                )
                            }
                            Text(
                                text = FormatUtils.formatCurrency(calculation.grandTotal),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald800
                            )
                        }
                    }

                    // Notes & Signatures
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "* નોંધ (Notes):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                        Text(
                            text = "૧. આ બિલ સિવિલ માનક ગુણાંક પર આધારિત અંદાજિત પત્રક છે.\n૨. લોકલ ટ્રાન્સપોર્ટ અને મટિરિયલ ક્વોલિટી મુજબ દરોમાં ફેરફાર થઈ શકે છે.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ગ્રાહકની સહી:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Spacer(modifier = Modifier.height(30.dp))
                            Text("_______________________", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("કોન્ટ્રાક્ટરની સહી:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Spacer(modifier = Modifier.height(30.dp))
                            Text("_______________________", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        }
                    }
                }
            }
        }
    }
}
