package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BandhkamViewModel

@Composable
fun VoiceReadoutCard(
    viewModel: BandhkamViewModel,
    modifier: Modifier = Modifier
) {
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val currentSection by viewModel.currentSpeechSection.collectAsState()
    var selectedSpeed by remember { mutableFloatStateOf(0.95f) }

    // Pulsing animation for audio wave effect while speaking
    val infiniteTransition = rememberInfiniteTransition(label = "VoicePulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSpeaking) Emerald50 else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSpeaking) Emerald600 else Slate200)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSpeaking) 4.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_readout_card")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaking) Emerald600 else Emerald100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.SpatialAudioOff,
                            contentDescription = "Voice Readout",
                            tint = if (isSpeaking) Color.White else Emerald800,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isSpeaking) "ઓટોમેટિક ગુજરાતી અવાજ ચાલુ છે..." else "ઓટોમેટિક ગુજરાતી અવાજ (Voice)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSpeaking) Emerald900 else Slate900
                        )
                        Text(
                            text = if (isSpeaking) (currentSection ?: "સંપૂર્ણ બિલ બોલાઈ રહ્યું છે...") else "ગ્રાહક અને સમગ્ર બિલ ગુજરાતીમાં સાંભળો",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSpeaking) Emerald700 else Slate500,
                            fontSize = 11.sp
                        )
                    }
                }

                // Speed chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val speeds = listOf(0.85f to "0.8x", 0.95f to "1.0x", 1.15f to "1.2x")
                    speeds.forEach { (speedVal, label) ->
                        val isSpeedSelected = selectedSpeed == speedVal
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSpeedSelected) Emerald700 else Slate100,
                            contentColor = if (isSpeedSelected) Color.White else Slate700,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedSpeed = speedVal
                                    viewModel.setVoiceSpeed(speedVal)
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Audio wave visualizer when speaking
            AnimatedVisibility(visible = isSpeaking) {
                Surface(
                    color = Emerald100,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(5) { i ->
                                val height = (10 + (i * 4 * waveScale)).dp
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(height.coerceIn(8.dp, 24.dp))
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Emerald800)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ગુજરાતી અવાજ બોલી રહ્યો છે...",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald900,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { viewModel.stopSpeaking() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrickRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("stop_speaking_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("અવાજ બંધ કરો", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Action Button: Play / Listen
            if (!isSpeaking) {
                Button(
                    onClick = { viewModel.speakCurrentBill() },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("listen_voice_bill_button")
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "વોઇસમાં બિલ સાંભળો (Listen to Bill)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
