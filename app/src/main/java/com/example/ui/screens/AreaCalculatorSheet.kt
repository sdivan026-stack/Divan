package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.util.FormatUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaCalculatorSheet(
    onDismiss: () -> Unit,
    onApplyArea: (Double) -> Unit
) {
    var lengthFeet by remember { mutableStateOf("40") }
    var lengthInches by remember { mutableStateOf("0") }
    var widthFeet by remember { mutableStateOf("25") }
    var widthInches by remember { mutableStateOf("0") }

    val totalLength = (lengthFeet.toDoubleOrNull() ?: 0.0) + ((lengthInches.toDoubleOrNull() ?: 0.0) / 12.0)
    val totalWidth = (widthFeet.toDoubleOrNull() ?: 0.0) + ((widthInches.toDoubleOrNull() ?: 0.0) / 12.0)
    val areaSqFt = totalLength * totalWidth
    val areaVar = areaSqFt / 9.0 // 1 Var (Gaj / Sq.Yard) = 9 SqFt
    val areaGuntha = areaSqFt / 1089.0 // 1 Guntha = 1089 SqFt

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("area_calc_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.SquareFoot,
                        contentDescription = null,
                        tint = Emerald700
                    )
                    Text(
                        text = "પ્લોટ / રૂમ માપ સાઈઝ કેલ્ક્યુલેટર",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "લંબાઈ અને પહોળાઈ દાખલ કરીને ચોરસ ફૂટ (Sq.Ft) અને વાર માપો:",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            // Length inputs
            Text(
                text = "૧. લંબાઈ (Length)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = lengthFeet,
                    onValueChange = { lengthFeet = it },
                    label = { Text("ફૂટ (Feet)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("length_feet_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lengthInches,
                    onValueChange = { lengthInches = it },
                    label = { Text("ઇંચ (Inches)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("length_inches_input"),
                    singleLine = true
                )
            }

            // Width inputs
            Text(
                text = "૨. પહોળાઈ (Width)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = widthFeet,
                    onValueChange = { widthFeet = it },
                    label = { Text("ફૂટ (Feet)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("width_feet_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = widthInches,
                    onValueChange = { widthInches = it },
                    label = { Text("ઇંચ (Inches)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("width_inches_input"),
                    singleLine = true
                )
            }

            // Calculated Output Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "કુલ ક્ષેત્રફળ (Total Area):",
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500
                    )
                    Text(
                        text = "${FormatUtils.formatIndianNumber(areaSqFt, true)} ચોરસ ફૂટ (Sq.Ft)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ચોરસ વાર: ${FormatUtils.formatIndianNumber(areaVar, true)} વાર",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Slate700
                        )
                        Text(
                            text = "ગુંઠા: ${FormatUtils.formatIndianNumber(areaGuntha, true)} ગુંઠા",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Slate700
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onApplyArea(areaSqFt)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apply_area_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "આ માપ કેલ્ક્યુલેટરમાં લાગુ કરો (${(areaSqFt).roundToInt()} sqft)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
