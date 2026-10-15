package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConstructionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.BandhkamViewModel
import com.example.util.FormatUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: BandhkamViewModel,
    onNavigateToInvoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val contractorProfile by viewModel.contractorProfile.collectAsState()
    val calculation = viewModel.getCalculation()

    var showAreaSheet by remember { mutableStateOf(false) }
    var saveSuccessSnackbar by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showAreaSheet) {
        AreaCalculatorSheet(
            onDismiss = { showAreaSheet = false },
            onApplyArea = { area ->
                viewModel.setBaseArea(area.roundToInt().toString())
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            if (saveSuccessSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { saveSuccessSnackbar = false }) {
                            Text("બરાબર", color = Color.White)
                        }
                    }
                ) {
                    Text("અંદાજ સફળતાપૂર્વક સાચવવામાં આવ્યો છે!")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald950),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = Emerald200,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "મકાન બાંધકામ કેલ્ક્યુલેટર",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ગુજરાત સ્ટાન્ડર્ડ સિવિલ ગુણાંક આધારે અંદાજ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald200
                        )
                    }
                    Surface(
                        color = Emerald900,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "🇮🇳 Factor",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald100
                        )
                    }
                }
            }

            // Step 1: Project Information Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Emerald600)
                        )
                        Text(
                            text = "૧. પ્રોજેક્ટની વિગત અને માપ (Project Inputs)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Customer Name
                    OutlinedTextField(
                        value = uiState.customerName,
                        onValueChange = { viewModel.setCustomerName(it) },
                        label = { Text("ગ્રાહકનું નામ (Customer Name)") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = Emerald700)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_name_input"),
                        singleLine = true
                    )

                    // Location & Phone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.siteLocation,
                            onValueChange = { viewModel.setSiteLocation(it) },
                            label = { Text("સ્થળ / સરનામું (Location)") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Place, contentDescription = null, tint = Emerald700)
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("site_location_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.customerPhone,
                            onValueChange = { viewModel.setCustomerPhone(it) },
                            label = { Text("મોબાઇલ નં.") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Phone, contentDescription = null, tint = Emerald700)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("customer_phone_input"),
                            singleLine = true
                        )
                    }

                    // Plot Area with Quick Add chips & Length x Width Calculator Trigger
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "કુલ બાંધકામ એરિયા (ચોરસ ફૂટ / Sq.Ft):",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextButton(
                                onClick = { showAreaSheet = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("open_area_calc_button")
                            ) {
                                Icon(
                                    Icons.Default.Calculate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Emerald700
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("L × W માપો", fontSize = 12.sp, color = Emerald700, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = uiState.baseAreaText,
                            onValueChange = { viewModel.setBaseArea(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Text(
                                    "Sq.Ft",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald700,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("plot_area_input"),
                            singleLine = true
                        )

                        // Quick Area preset chips (+100, +500, 1000, 1500, 2000)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("500", "1000", "1500", "2000").forEach { preset ->
                                val isSelected = uiState.baseAreaText == preset
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setBaseArea(preset) },
                                    label = { Text("$preset sqft", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Floors Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "માળની સંખ્યા (Floors):",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val floorOptions = listOf(
                                1 to "G",
                                2 to "G + 1",
                                3 to "G + 2",
                                4 to "G + 3"
                            )
                            floorOptions.forEach { (floorCount, label) ->
                                val isSelected = uiState.floors == floorCount
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Emerald700 else Slate100,
                                    contentColor = if (isSelected) Color.White else Slate800,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setFloors(floorCount) }
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Emerald700 else Slate300,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        textAlign = TextAlign.Center,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        val baseArea = uiState.baseAreaText.toDoubleOrNull() ?: 0.0
                        Text(
                            text = "કુલ બિલ્ટ-અપ એરિયા: ${(baseArea * uiState.floors).roundToInt()} ચોરસ ફૂટ (${uiState.floors} માળ સહિત)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald800,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Construction Type Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "બાંધકામનો પ્રકાર (Type of Construction):",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        ConstructionType.entries.forEach { type ->
                            val isSelected = uiState.constructionType == type
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Emerald50 else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setConstructionType(type) }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Emerald600 else Slate200,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.setConstructionType(type) },
                                        colors = RadioButtonDefaults.colors(selectedColor = Emerald700)
                                    )
                                    Column {
                                        Text(
                                            text = type.titleGu,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Emerald900 else Slate800
                                        )
                                        val subtitle = when (type) {
                                            ConstructionType.FULL_STRUCTURE -> "પાયાથી સ્લેબ: ઈંટો, રેતી, કપચી, સ્ટીલ, ચણતર અને પ્લાસ્ટર સંપૂર્ણ"
                                            ConstructionType.BEAM_SLAB_CASTING -> "દીવાલો તૈયાર હોવાથી ઈંટો = ૦. માત્ર બીમ-ધાબાનું સીમેન્ટ, સળિયા, કપચી અને લેબર"
                                            ConstructionType.SLAB_ONLY -> "માત્ર આરસીસી છત ભરાઈ (RCC Slab Only)"
                                            ConstructionType.BRICK_PLASTER -> "માત્ર દીવાલ ઈંટ ચણતર અને પ્લાસ્ટર કાર્ય"
                                            ConstructionType.FLOORING_FINISHING -> "લાદી ફ્લોરિંગ અને કલર ફિનિશિંગ કાર્ય"
                                        }
                                        Text(
                                            text = subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Emerald700 else Slate500,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Step 2: Rate Modifiers Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Amber500)
                            )
                            Text(
                                text = "૨. બજાર ભાવ એડજસ્ટ કરો (Live Rate Modifier)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = { viewModel.resetRatesToDefault() },
                            modifier = Modifier.testTag("reset_rates_button")
                        ) {
                            Text("ડિફૉલ્ટ ભાવ", color = Emerald700, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // 6-grid rate inputs
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RateInputField(
                            label = "સીમેન્ટ (રૂ/બેગ)",
                            value = uiState.rates.cement,
                            onValueChange = { viewModel.updateCementRate(it) },
                            modifier = Modifier.weight(1f)
                        )
                        RateInputField(
                            label = "રેતી (રૂ/cft)",
                            value = uiState.rates.sand,
                            onValueChange = { viewModel.updateSandRate(it) },
                            modifier = Modifier.weight(1f)
                        )
                        RateInputField(
                            label = "કપચી (રૂ/cft)",
                            value = uiState.rates.aggregate,
                            onValueChange = { viewModel.updateAggregateRate(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RateInputField(
                            label = "સળિયા (રૂ/કિલો)",
                            value = uiState.rates.steelKg,
                            onValueChange = { viewModel.updateSteelRate(it) },
                            modifier = Modifier.weight(1f)
                        )
                        RateInputField(
                            label = "ઈંટો (રૂ/નગ)",
                            value = uiState.rates.brick,
                            onValueChange = { viewModel.updateBrickRate(it) },
                            modifier = Modifier.weight(1f),
                            allowDecimals = true
                        )
                        RateInputField(
                            label = "લેબર (રૂ/sqft)",
                            value = uiState.rates.laborSqft,
                            onValueChange = { viewModel.updateLaborRate(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Calculation Summary & Grand Total
            Card(
                colors = CardDefaults.cardColors(containerColor = Emerald50),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald200)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = "દર ચોરસ ફૂટ અંદાજ: ₹${FormatUtils.formatIndianNumber(calculation.costPerSqFt)} / sqft",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald700
                            )
                        }
                        Text(
                            text = FormatUtils.formatCurrency(calculation.grandTotal),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Emerald800
                        )
                    }

                    Divider(color = Emerald200)

                    // Quick material summary tags
                    Text(
                        text = "જરૂરી મુખ્ય સામગ્રી અંદાજ:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        calculation.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.speakItem(item) }
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeDown,
                                        contentDescription = "Speak ${item.nameGu}",
                                        tint = Emerald700,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = item.nameGu,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate700
                                    )
                                }
                                Text(
                                    text = "${FormatUtils.formatIndianNumber(item.quantity, item.quantity % 1.0 != 0.0)} ${item.unitGu}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "₹${FormatUtils.formatIndianNumber(item.totalCost)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Emerald800
                                )
                            }
                        }
                    }

                    if (uiState.constructionType == ConstructionType.BEAM_SLAB_CASTING) {
                        Surface(
                            color = Emerald100,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🧱 નોંધ: દીવાલો અગાઉથી ચણાઈ ગઈ હોવાથી ઈંટો (Bricks) નો ખર્ચ આપોઆપ ₹૦ (શૂન્ય) થયો છે. ફક્ત બીમ-ધાબાનું સીમેન્ટ, સળિયા, કપચી, વાસરી અને લેબર ખર્ચ ગણાયેલ છે.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald950,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Automatic Gujarati Voice Readout Card
            com.example.ui.components.VoiceReadoutCard(viewModel = viewModel)

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onNavigateToInvoice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("view_invoice_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "અંદાજિત બિલ પત્રક જુઓ અને પ્રિન્ટ કરો",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "વોટ્સએપ સમરી શેર કરો"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("whatsapp_share_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Emerald700)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp શેર", color = Emerald800, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.saveEstimate {
                                saveSuccessSnackbar = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_estimate_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber600),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("અંદાજ સેવ કરો", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RateInputField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    allowDecimals: Boolean = false
) {
    var textVal by remember(value) {
        mutableStateOf(if (allowDecimals) value.toString() else value.roundToInt().toString())
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Slate500,
            fontSize = 10.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
            value = textVal,
            onValueChange = { inputStr ->
                textVal = inputStr
                inputStr.toDoubleOrNull()?.let { num -> onValueChange(num) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            shape = RoundedCornerShape(8.dp)
        )
    }
}
