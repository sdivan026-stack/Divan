package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContractorProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.BandhkamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    viewModel: BandhkamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentProfile by viewModel.contractorProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var firmName by remember(currentProfile) { mutableStateOf(currentProfile.firmName) }
    var proprietorName by remember(currentProfile) { mutableStateOf(currentProfile.proprietorName) }
    var phone by remember(currentProfile) { mutableStateOf(currentProfile.phone) }
    var location by remember(currentProfile) { mutableStateOf(currentProfile.location) }
    var tagline by remember(currentProfile) { mutableStateOf(currentProfile.tagline) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "કોન્ટ્રાક્ટર પ્રોફાઇલ અને સેટિંગ્સ",
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
            // Header Preview Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Emerald50),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Emerald200),
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
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Emerald800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = firmName.ifBlank { "શ્રી ગણેશ કન્સ્ટ્રક્શન" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )
                        Text(
                            text = "${proprietorName.ifBlank { "સિવિલ કોન્ટ્રાક્ટર" }} • $phone",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate700
                        )
                        Text(
                            text = location.ifBlank { "અમદાવાદ, ગુજરાત" },
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }
            }

            // Firm Info Form
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "બિલ માટે પેઢી / વ્યવસાયની વિગત",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = firmName,
                        onValueChange = { firmName = it },
                        label = { Text("પેઢી / કંપનીનું નામ (Firm Name)") },
                        modifier = Modifier.fillMaxWidth().testTag("firm_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = proprietorName,
                        onValueChange = { proprietorName = it },
                        label = { Text("કોન્ટ્રાક્ટર / એન્જિનિયરનું નામ") },
                        modifier = Modifier.fillMaxWidth().testTag("proprietor_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("સંપર્ક / વોટ્સએપ નંબર") },
                        modifier = Modifier.fillMaxWidth().testTag("firm_phone_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("ઓફિસ સરનામું / જિલ્લો / રાજ્ય") },
                        modifier = Modifier.fillMaxWidth().testTag("firm_location_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("ટેગલાઈન / સૂત્ર (Tagline)") },
                        modifier = Modifier.fillMaxWidth().testTag("firm_tagline_input"),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val newProfile = ContractorProfile(
                                firmName = firmName,
                                proprietorName = proprietorName,
                                phone = phone,
                                location = location,
                                tagline = tagline
                            )
                            viewModel.updateProfile(newProfile)
                            Toast.makeText(context, "પ્રોફાઇલ સાચવવામાં આવી!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("પ્રોફાઇલ વિગતો સાચવો", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Save Default Rates Option
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ડિફૉલ્ટ બજાર ભાવો",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Text(
                        text = "હાલના કેલ્ક્યુલેટરમાં સેટ કરેલા ભાવોને (સીમેન્ટ ₹${uiState.rates.cement.toInt()}, રેતી ₹${uiState.rates.sand.toInt()}, કપચી ₹${uiState.rates.aggregate.toInt()}, સ્ટીલ ₹${uiState.rates.steelKg.toInt()}, ઈંટો ₹${uiState.rates.brick}, લેબર ₹${uiState.rates.laborSqft.toInt()}) તમારા ડિફૉલ્ટ ભાવ તરીકે સેવ કરો જેથી દર વખતે બદલવા ન પડે.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )

                    OutlinedButton(
                        onClick = {
                            viewModel.saveAsDefaultRates(uiState.rates)
                            Toast.makeText(context, "હાલના ભાવ ડિફૉલ્ટ તરીકે સેટ થઈ ગયા!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Emerald700)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("હાલના ભાવો ડિફૉલ્ટ બનાવો", color = Emerald800, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // About / Standards Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "સિવિલ એન્જિનિયરિંગ ગુણાંક (Civil Standards):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Text(
                        text = "• સીમેન્ટ: ૦.૩૫ બેગ પ્રતિ ચોરસ ફૂટ (સંપૂર્ણ બાંધકામ)\n• રેતી/વાસરી: ૧.૨૦ CFT પ્રતિ ચોરસ ફૂટ\n• કપચી: ૧.૦૦ CFT પ્રતિ ચોરસ ફૂટ\n• સ્ટીલ સળિયા: ૩.૫૦ કિલો પ્રતિ ચોરસ ફૂટ\n• લાલ ઈંટો: ૧૫ નગ પ્રતિ ચોરસ ફૂટ\n• ૧ બ્રાસ = ૧૦૦ CFT (ગુજરાત માનક)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
