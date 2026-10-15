package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BandhkamViewModel
import com.example.util.FormatUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivilToolsScreen(
    viewModel: BandhkamViewModel,
    onApplyAreaToCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedToolTab by remember { mutableIntStateOf(0) }

    val toolTabs = listOf("પ્લોટ એરિયા", "સ્લેબ કોંક્રિટ", "સળિયા વજન", "બ્રાસ કન્વર્ટર")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "સિવિલ બાંધકામ ટૂલ્સ",
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = Slate100,
                contentColor = Emerald800,
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                toolTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedToolTab == index,
                        onClick = { selectedToolTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedToolTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            when (selectedToolTab) {
                0 -> PlotAreaConverterTool(viewModel = viewModel, onApply = onApplyAreaToCalculator)
                1 -> SlabConcreteVolumeTool()
                2 -> SteelBarWeightTool(steelRate = viewModel.uiState.collectAsState().value.rates.steelKg)
                3 -> BrassToCftConverterTool()
            }
        }
    }
}

@Composable
fun PlotAreaConverterTool(
    viewModel: BandhkamViewModel,
    onApply: () -> Unit
) {
    var lengthStr by remember { mutableStateOf("50") }
    var widthStr by remember { mutableStateOf("30") }

    val length = lengthStr.toDoubleOrNull() ?: 0.0
    val width = widthStr.toDoubleOrNull() ?: 0.0
    val sqft = length * width
    val sqYard = sqft / 9.0 // 1 Var = 9 SqFt
    val guntha = sqft / 1089.0 // 1 Guntha = 1089 SqFt
    val vigha = sqft / 17424.0 // Standard Gujarat Vigha = 16 Guntha = 17,424 SqFt

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
            Text(
                text = "પ્લોટ / જમીન ક્ષેત્રફળ કન્વર્ટર",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = lengthStr,
                    onValueChange = { lengthStr = it },
                    label = { Text("લંબાઈ (ફૂટ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = widthStr,
                    onValueChange = { widthStr = it },
                    label = { Text("પહોળાઈ (ફૂટ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Surface(
                color = Emerald50,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ક્ષેત્રફળ પરિણામ (Conversions):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Emerald900)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ચોરસ ફૂટ (Sq.Ft):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${FormatUtils.formatIndianNumber(sqft, true)} Sq.Ft", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Emerald800)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ચોરસ વાર / ગજ (Var):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${FormatUtils.formatIndianNumber(sqYard, true)} વાર", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ગુંઠા (Guntha):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${FormatUtils.formatIndianNumber(guntha, true)} ગુંઠા", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("વીઘા (ગુજરાતી Vigha):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${FormatUtils.formatIndianNumber(vigha, true)} વીઘા", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.setBaseArea(sqft.roundToInt().toString())
                    onApply()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("આ માપ કેલ્ક્યુલેટરમાં મોકલો (${sqft.roundToInt()} sqft)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SlabConcreteVolumeTool() {
    var lengthStr by remember { mutableStateOf("30") }
    var widthStr by remember { mutableStateOf("20") }
    var thicknessInch by remember { mutableStateOf("5") } // 5 inches
    var selectedRatio by remember { mutableStateOf("M20 (1:1.5:3)") }

    val length = lengthStr.toDoubleOrNull() ?: 0.0
    val width = widthStr.toDoubleOrNull() ?: 0.0
    val thicknessFeet = (thicknessInch.toDoubleOrNull() ?: 5.0) / 12.0

    val wetVolumeCft = length * width * thicknessFeet
    val dryVolumeCft = wetVolumeCft * 1.54 // 1.54 dry factor
    val wetVolumeCum = wetVolumeCft / 35.3147

    // Ratio M20 = 1:1.5:3 (sum = 5.5) -> Cement = dry / 5.5, Bags = Cement / 1.226
    val ratioSum = if (selectedRatio.contains("M25")) 4.0 else 5.5
    val sandMultiplier = if (selectedRatio.contains("M25")) 1.0 else 1.5
    val aggMultiplier = if (selectedRatio.contains("M25")) 2.0 else 3.0

    val cementCft = dryVolumeCft / ratioSum
    val cementBags = cementCft / 1.226 // 1 bag = 1.226 cft
    val sandCft = cementCft * sandMultiplier
    val aggregateCft = cementCft * aggMultiplier

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
            Text(
                text = "આરસીસી સ્લેબ / છત કોંક્રિટ સામગ્રી કેલ્ક્યુલેટર",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = lengthStr,
                    onValueChange = { lengthStr = it },
                    label = { Text("લંબાઈ (ફૂટ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = widthStr,
                    onValueChange = { widthStr = it },
                    label = { Text("પહોળાઈ (ફૂટ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = thicknessInch,
                    onValueChange = { thicknessInch = it },
                    label = { Text("જાડાઈ (ઇંચ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Surface(
                color = Emerald50,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "સ્લેબ વોલ્યુમ: ${FormatUtils.formatIndianNumber(wetVolumeCft, true)} CFT (${FormatUtils.formatIndianNumber(wetVolumeCum, true)} Cu.M)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                    Divider(color = Emerald200)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("સીમેન્ટ (Cement Bags):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${cementBags.roundToInt()} બેગ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Emerald800)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("રેતી (Sand):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${sandCft.roundToInt()} CFT (${(sandCft / 100.0).format(1)} બ્રાસ)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("કપચી (Aggregate):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${aggregateCft.roundToInt()} CFT (${(aggregateCft / 100.0).format(1)} બ્રાસ)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
            }
        }
    }
}

@Composable
fun SteelBarWeightTool(steelRate: Double) {
    val barSizes = listOf(8, 10, 12, 16, 20, 25)
    var selectedDia by remember { mutableIntStateOf(12) }
    var lengthMetersStr by remember { mutableStateOf("12") } // 1 standard rod = 12 meters
    var rodsCountStr by remember { mutableStateOf("10") }

    val lengthMeters = lengthMetersStr.toDoubleOrNull() ?: 12.0
    val count = rodsCountStr.toDoubleOrNull() ?: 1.0

    // D^2 / 162.28 kg per meter
    val weightPerMeter = (selectedDia * selectedDia) / 162.28
    val singleRodWeight = weightPerMeter * lengthMeters
    val totalWeightKg = singleRodWeight * count
    val totalTonnes = totalWeightKg / 1000.0
    val estimatedCost = totalWeightKg * steelRate

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
            Text(
                text = "સળિયા વજન કેલ્ક્યુલેટર (TMT Steel Weight)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )

            Text("સળિયાનો વ્યાસ પસંદ કરો (Diameter mm):", style = MaterialTheme.typography.labelSmall, color = Slate600)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                barSizes.forEach { dia ->
                    val isSelected = selectedDia == dia
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Emerald700 else Slate100,
                        contentColor = if (isSelected) Color.White else Slate800,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDia = dia }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "${dia}mm",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = lengthMetersStr,
                    onValueChange = { lengthMetersStr = it },
                    label = { Text("લંબાઈ (મીટર)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rodsCountStr,
                    onValueChange = { rodsCountStr = it },
                    label = { Text("સળિયા સંખ્યા") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Surface(
                color = Slate50,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("મીટર દીઠ વજન (D²/162):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${weightPerMeter.format(3)} kg/m", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("૧ સળિયાનું વજન ($lengthMeters મીટર):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${singleRodWeight.format(2)} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("કુલ વજન ($count સળિયા):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${totalWeightKg.format(1)} kg (${totalTonnes.format(3)} ટન)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Emerald800)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("અંદાજિત રકમ (@ ₹$steelRate/kg):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text(FormatUtils.formatCurrency(estimatedCost), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Emerald800)
                    }
                }
            }
        }
    }
}

@Composable
fun BrassToCftConverterTool() {
    var cftValueStr by remember { mutableStateOf("300") }
    var brassValueStr by remember { mutableStateOf("3") }
    var lastEditedByCft by remember { mutableStateOf(true) }

    val cftVal = if (lastEditedByCft) {
        cftValueStr.toDoubleOrNull() ?: 0.0
    } else {
        (brassValueStr.toDoubleOrNull() ?: 0.0) * 100.0
    }

    val brassVal = cftVal / 100.0

    // Vehicle estimates
    val tractorLoads = cftVal / 100.0
    val dumper6Wheeler = cftVal / 300.0
    val dumper10Wheeler = cftVal / 600.0

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
            Text(
                text = "બ્રાસ (Brass) થી CFT અને વાહન કન્વર્ટર",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )

            Text(
                text = "ગુજરાતમાં રેતી અને કપચી માપવા માટે ૧ બ્રાસ = ૧૦૦ CFT ગણવામાં આવે છે.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = if (lastEditedByCft) cftValueStr else cftVal.roundToInt().toString(),
                    onValueChange = {
                        cftValueStr = it
                        lastEditedByCft = true
                    },
                    label = { Text("CFT (ચોરસ ઘન ફૂટ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = if (!lastEditedByCft) brassValueStr else brassVal.format(2),
                    onValueChange = {
                        brassValueStr = it
                        lastEditedByCft = false
                    },
                    label = { Text("બ્રાસ (Brass)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Surface(
                color = Emerald50,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("અંદાજિત વાહન ટ્રિપ (Vehicle Loads):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Emerald900)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ટ્રેક્ટર ટ્રોલી (~100 CFT):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${tractorLoads.format(1)} ટ્રોલી", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("૬-વ્હીલર ડમ્પર (~300 CFT):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${dumper6Wheeler.format(1)} ડમ્પર", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("૧૦-વ્હીલર હાઇવા (~600 CFT):", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("${dumper10Wheeler.format(1)} હાઇવા", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Emerald800)
                    }
                }
            }
        }
    }
}

private fun Double.format(decimals: Int): String {
    return String.format(java.util.Locale.US, "%.${decimals}f", this)
}
