package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import com.example.astrosage.core.astronomy.VedicAnalysisEngine
import com.example.ui.theme.*

@Composable
fun DoshaRemediesView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val manglik = VedicAnalysisEngine.analyzeManglik(chart)
    val kaalSarp = VedicAnalysisEngine.analyzeKaalSarp(chart)
    val sadeSati = VedicAnalysisEngine.analyzeSadeSati(chart)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dosha_remedies_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. MANGLIK DOSHA CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (manglik.isManglik) Color(0xFFE53935).copy(alpha = 0.2f) else Color(0xFF43A047).copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (manglik.isManglik) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (manglik.isManglik) Color(0xFFFF5252) else Color(0xFF81C784),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isHindi) "मांगलिक दोष विश्लेषण" else "Manglik Dosha Analysis",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright
                            )
                            Text(
                                text = "${if (isHindi) "मंगल स्थिति: भाव" else "Mars in House"} ${manglik.marsHouse}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        color = if (manglik.isManglik) Color(0xFFD32F2F).copy(alpha = 0.2f) else Color(0xFF388E3C).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (manglik.isManglik) {
                                if (isHindi) "${manglik.percentage}% मांगलिक" else "${manglik.percentage}% Manglik"
                            } else {
                                if (isHindi) "दोष मुक्त (Non-Manglik)" else "Non-Manglik"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (manglik.isManglik) Color(0xFFFF8A80) else Color(0xFFA5D6A7),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { manglik.percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = if (manglik.percentage > 50) Color(0xFFE53935) else CelestialGold,
                    trackColor = CosmicNavyElevated,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isHindi) manglik.descriptionHi else manglik.descriptionEn,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remedies List
                Text(
                    text = if (isHindi) "अचूक वैदिक उपाय (Remedies):" else "Recommended Remedies:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstroSageOrangeLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                val remedies = if (isHindi) manglik.remediesHi else manglik.remediesEn
                remedies.forEach { remedy ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", color = CelestialGold, fontSize = 12.sp)
                        Text(remedy, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }

        // --- 2. KAAL SARP DOSHA CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MysticPurple.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = MysticPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isHindi) "कालसर्प दोष विश्लेषण" else "Kaal Sarp Yoga Analysis",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright
                            )
                            Text(
                                text = if (isHindi) kaalSarp.typeNameHi else kaalSarp.typeNameEn,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        color = AstroSageOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isHindi) kaalSarp.intensityHi else kaalSarp.intensityEn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstroSageOrangeLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (kaalSarp.hasDosha) {
                        if (isHindi) "राहु और केतु के अक्ष में ग्रह संचरण के कारण जीवन में आकस्मिक उतार-चढ़ाव या संघर्ष का सामना करना पड़ सकता है।"
                        else "Planets hemmed between Rahu and Ketu axis indicate sudden life transitions and karmic growth opportunities."
                    } else {
                        if (isHindi) "आपकी जन्म कुंडली कालसर्प दोष के कुप्रभाव से पूर्णतया सुरक्षित है। ग्रह स्वतंत्र रूप से शुभ फल देने में समर्थ हैं।"
                        else "Your chart is completely clear of Kaal Sarp Dosha, allowing planets to deliver favourable results."
                    },
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isHindi) "शांति व निवारण उपाय (Remedies):" else "Remedies & Mantras:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstroSageOrangeLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                val ksRemedies = if (isHindi) kaalSarp.remediesHi else kaalSarp.remediesEn
                ksRemedies.forEach { remedy ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", color = CelestialGold, fontSize = 12.sp)
                        Text(remedy, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }

        // --- 3. SHANI SADE SATI CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                            text = if (isHindi) "शनि साढ़े साती स्थिति" else "Shani Sade Sati Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Text(
                            text = "${if (isHindi) "वर्तमान गोचर" else "Transit"}: ${if (isHindi) sadeSati.saturnSignHi else sadeSati.saturnSignEn}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = if (sadeSati.isActive) AstroSageOrange.copy(alpha = 0.2f) else Color(0xFF388E3C).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isHindi) sadeSati.phaseHi else sadeSati.phaseEn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sadeSati.isActive) AstroSageOrangeLight else Color(0xFFA5D6A7),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isHindi) sadeSati.impactHi else sadeSati.impactEn,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isHindi) "शनि शांति के सरल उपाय (Shani Remedies):" else "Effective Shani Remedies:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstroSageOrangeLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                val shaniRemedies = if (isHindi) sadeSati.remediesHi else sadeSati.remediesEn
                shaniRemedies.forEach { remedy ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", color = CelestialGold, fontSize = 12.sp)
                        Text(remedy, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
