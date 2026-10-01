package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.ai.KundaliAiReading
import com.example.ui.theme.*

@Composable
fun AiPredictionCard(
    reading: KundaliAiReading,
    onRefreshAi: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_prediction_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CelestialGold, MysticPurple)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with AI Badge
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
                            .size(32.dp)
                            .background(Brush.linearGradient(listOf(CelestialGold, SacredSaffron)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Reading",
                            tint = CosmicNavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Astrosage AI Kundali Insights",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold
                        )
                        Text(
                            text = if (reading.isAiGenerated) "Generated via Google AI Studio (Gemini 3.5)" else "Calculated via Parashari Vedic Principles",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = CelestialGold,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = onRefreshAi,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Regenerate AI Reading",
                            tint = CelestialGoldBright
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Lagna Analysis Section
            SectionHeader(title = "1. Lagna (Ascendant) & Life Path Analysis")
            Text(
                text = reading.lagnaAnalysis,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Current Dasha Prediction
            SectionHeader(title = "2. Current Dasha Predictions & Karmic Milestones")
            Text(
                text = reading.currentDashaPrediction,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Strengths and Weaknesses
            SectionHeader(title = "3. Planetary Strengths & Afflictions")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Positive Planets Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldGreen.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Positive Planets", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        reading.strengthsAndWeaknesses.positivePlanets.forEach { p ->
                            Text("• $p", fontSize = 10.sp, color = TextPrimary, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }

                // Afflicted Planets Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AuspiciousRed.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = AuspiciousRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Afflicted Planets", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuspiciousRed)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        reading.strengthsAndWeaknesses.afflictedPlanets.forEach { p ->
                            Text("• $p", fontSize = 10.sp, color = TextPrimary, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Vedic Remedies (Gemstone, Mantra, Rudraksha)
            SectionHeader(title = "4. Prescribed Vedic Remedies")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicNavyElevated, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemedyItem(label = "Recommended Gemstone", value = reading.remedies.gemstone)
                RemedyItem(label = "Vedic / Beej Mantra", value = reading.remedies.mantra)
                RemedyItem(label = "Sacred Rudraksha", value = reading.remedies.rudraksha)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = CelestialGoldBright
    )
}

@Composable
private fun RemedyItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MysticPurple)
        Text(text = value.ifBlank { "Standard worship and meditation" }, fontSize = 12.sp, color = TextPrimary)
    }
}
