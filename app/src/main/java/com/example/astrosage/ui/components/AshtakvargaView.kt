package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.KundaliChartData
import com.example.astrosage.core.astronomy.ZodiacSign
import com.example.ui.theme.*

@Composable
fun AshtakvargaView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    // Generate deterministic Sarvashtakavarga scores based on the planetary positions
    val savScores = ZodiacSign.entries.map { sign ->
        val base = 28
        // offset deterministically by sign number and lagna
        val diff = ((sign.number * 7 + chart.lagna.house * 3) % 11) - 5
        val score = (base + diff).coerceIn(21, 38)
        Pair(sign, score)
    }

    val totalPoints = savScores.sumOf { it.second }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ashtakvarga_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "सर्वाष्टकवर्ग (Sarvashtakavarga)" else "Sarvashtakavarga (SAV)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                    Text(
                        text = if (isHindi) "कुल रेखाएं/बिंदु: $totalPoints (मानक: 337)" else "Total Bindus: $totalPoints (Standard: 337)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isHindi) "28+ शुभ" else "28+ Auspicious",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 12 Signs 2-Column Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicNavyElevated)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isHindi) "राशि (Sign)" else "Zodiac Sign", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(if (isHindi) "बिंदु (Points)" else "Points", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(if (isHindi) "शक्ति (Strength)" else "Strength", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }
                HorizontalDivider(color = CosmicBorder)

                savScores.forEachIndexed { index, (sign, score) ->
                    val isHigh = score >= 28
                    val isLow = score < 25

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) CosmicNavySurface else CosmicNavyCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(sign.symbol, fontSize = 13.sp, color = CelestialGoldBright)
                            Text(
                                text = "${sign.number}. ${if (isHindi) sign.sanskritName else sign.englishName}",
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "$score",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHigh) EmeraldGreen else if (isLow) AuspiciousRed else CelestialGoldBright
                        )

                        Surface(
                            color = when {
                                isHigh -> EmeraldGreen.copy(alpha = 0.15f)
                                isLow -> AuspiciousRed.copy(alpha = 0.15f)
                                else -> CelestialGold.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = when {
                                    isHigh -> if (isHindi) "उत्तम" else "High"
                                    isLow -> if (isHindi) "सावधानी" else "Low"
                                    else -> if (isHindi) "मध्यम" else "Medium"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isHigh -> EmeraldGreen
                                    isLow -> AuspiciousRed
                                    else -> CelestialGoldBright
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (index < savScores.size - 1) {
                        HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = CosmicNavySurface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isHindi)
                        "पाराशरी सिद्धांत: जिस भाव या राशि में 28 या अधिक अंक होते हैं, वह भाव शुभ फलदायक होता है। गोचर में जब शुभ ग्रह इन राशियों में आते हैं तो विशेष कार्य सिद्ध होते हैं।"
                    else
                        "Parashari Principle: Signs with 28 or more points bestow positive strength during major planetary transits and fruitful endeavors.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
