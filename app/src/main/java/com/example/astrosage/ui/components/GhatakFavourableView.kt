package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.astrosage.core.astronomy.Planet
import com.example.astrosage.core.astronomy.VedicAnalysisEngine
import com.example.ui.theme.*

@Composable
fun GhatakFavourableView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val moonPos = chart.planets.firstOrNull { it.planet == Planet.MOON }
    val moonSign = moonPos?.sign ?: chart.lagna.sign

    val favourable = VedicAnalysisEngine.calculateFavourableDetails(chart)
    val ghatak = VedicAnalysisEngine.calculateGhatakDetails(moonSign)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ghatak_favourable_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. FAVOURABLE ELEMENTS CARD (अनुकूल तत्त्व) ---
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
                            color = CelestialGold.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CelestialGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isHindi) "अनुकूल तत्त्व (शुभ बिंदु)" else "Favourable Points",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright
                            )
                            Text(
                                text = if (isHindi) "शुभ रत्न, अंक, वार एवं दिशा" else "Lucky Stones, Numbers, Days & Direction",
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
                            text = if (isHindi) "शुभ फलदायी" else "Auspicious",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstroSageOrangeLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Recommended Gemstones Grid (भाग्य रत्न, जीवन रत्न, कारक रत्न)
                Text(
                    text = if (isHindi) "अनुशंसित शुभ रत्न (Recommended Gemstones):" else "Recommended Gemstones:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bhagya Ratna
                    GemstoneCard(
                        title = if (isHindi) "भाग्य रत्न (9th Lord)" else "Lucky Stone",
                        gemstone = favourable.bhagyaRatna,
                        isHindi = isHindi,
                        colorHex = 0xFFFFD54F,
                        modifier = Modifier.weight(1f)
                    )
                    // Jeevan Ratna
                    GemstoneCard(
                        title = if (isHindi) "जीवन रत्न (Lagna)" else "Life Stone",
                        gemstone = favourable.jeevanRatna,
                        isHindi = isHindi,
                        colorHex = 0xFF81D4FA,
                        modifier = Modifier.weight(1f)
                    )
                    // Punya Ratna
                    GemstoneCard(
                        title = if (isHindi) "कारक रत्न (5th Lord)" else "Punya Stone",
                        gemstone = favourable.punyaRatna,
                        isHindi = isHindi,
                        colorHex = 0xFFA5D6A7,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Auspicious Attributes Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
                ) {
                    val favRows = listOf(
                        Pair(
                            if (isHindi) "शुभ वार (Lucky Days)" else "Lucky Days",
                            (if (isHindi) favourable.luckyDaysHi else favourable.luckyDays).joinToString(", ")
                        ),
                        Pair(
                            if (isHindi) "शुभ अंक (Lucky Numbers)" else "Lucky Numbers",
                            favourable.luckyNumbers.joinToString(", ")
                        ),
                        Pair(
                            if (isHindi) "शुभ रंग (Lucky Colors)" else "Lucky Colors",
                            (if (isHindi) favourable.luckyColorsHi else favourable.luckyColors).joinToString(", ")
                        ),
                        Pair(
                            if (isHindi) "शुभ दिशा (Favourable Direction)" else "Lucky Direction",
                            if (isHindi) favourable.luckyDirectionHi else favourable.luckyDirection
                        ),
                        Pair(
                            if (isHindi) "इष्ट देवता (Ishta Devata)" else "Ishta Devata",
                            if (isHindi) favourable.ishtaDevataHi else favourable.ishtaDevata
                        ),
                        Pair(
                            if (isHindi) "मित्र राशियां (Friendly Signs)" else "Friendly Signs",
                            (if (isHindi) favourable.friendlySignsHi else favourable.friendlySigns).joinToString(", ")
                        )
                    )

                    favRows.forEachIndexed { idx, (k, v) ->
                        val isEven = idx % 2 == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isEven) CosmicNavySurface else CosmicNavyCard)
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = k, fontSize = 12.sp, color = TextSecondary)
                            Text(text = v, fontSize = 12.sp, color = CelestialGoldBright, fontWeight = FontWeight.SemiBold)
                        }
                        if (idx < favRows.size - 1) {
                            HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

        // --- 2. GHATAK CHAKRA CARD (घातक चक्र) ---
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
                            color = AstroSageOrange.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = AstroSageOrangeLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isHindi) "घातक चक्र (Ghatak Chakra)" else "Ghatak Chakra (Inauspicious Points)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstroSageOrangeLight
                            )
                            Text(
                                text = "${if (isHindi) "चंद्र राशि पर आधारित" else "Based on Moon Sign"}: ${if (isHindi) ghatak.rashiNameHi else ghatak.rashiNameEn}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFD32F2F).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isHindi) "सावधानी योग्य" else "Caution",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8A80),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isHindi)
                        "शास्त्रीय मान्यतानुसार इन तिथियों, वारों व नक्षत्रों में कोई नया शुभ कार्य या यात्रा आरंभ करने से बचना चाहिए।"
                    else
                        "According to classical Vedic texts, avoid initiating critical new endeavors or journeys during these Ghatak periods.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ghatak Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
                ) {
                    val ghatakRows = listOf(
                        Pair(if (isHindi) "घातक मास (Ghatak Month)" else "Ghatak Month", if (isHindi) ghatak.ghatakMonthHi else ghatak.ghatakMonthEn),
                        Pair(if (isHindi) "घातक तिथि (Ghatak Tithi)" else "Ghatak Tithi", if (isHindi) ghatak.ghatakTithiHi else ghatak.ghatakTithiEn),
                        Pair(if (isHindi) "घातक वार (Ghatak Day)" else "Ghatak Day", if (isHindi) ghatak.ghatakDayHi else ghatak.ghatakDayEn),
                        Pair(if (isHindi) "घातक नक्षत्र (Ghatak Nakshatra)" else "Ghatak Nakshatra", if (isHindi) ghatak.ghatakNakshatraHi else ghatak.ghatakNakshatraEn),
                        Pair(if (isHindi) "घातक प्रहर (Ghatak Prahar)" else "Ghatak Prahar", if (isHindi) ghatak.ghatakPraharHi else ghatak.ghatakPraharEn),
                        Pair(if (isHindi) "घातक लग्न (Ghatak Ascendant)" else "Ghatak Lagna", if (isHindi) ghatak.ghatakLagnaHi else ghatak.ghatakLagnaEn)
                    )

                    ghatakRows.forEachIndexed { idx, (k, v) ->
                        val isEven = idx % 2 == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isEven) CosmicNavySurface else CosmicNavyCard)
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = k, fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = v,
                                fontSize = 12.sp,
                                color = Color(0xFFFFAB91),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (idx < ghatakRows.size - 1) {
                            HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GemstoneCard(
    title: String,
    gemstone: com.example.astrosage.core.astronomy.GemstoneInfo,
    isHindi: Boolean,
    colorHex: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(colorHex).copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(colorHex).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(colorHex)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = Color(colorHex),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isHindi) gemstone.nameHi else gemstone.nameEn,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = gemstone.metal,
                fontSize = 9.sp,
                color = TextMuted
            )
            Text(
                text = gemstone.finger,
                fontSize = 9.sp,
                color = TextSecondary
            )
        }
    }
}
