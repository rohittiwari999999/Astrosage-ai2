package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import java.util.Calendar
import java.util.Locale

@Composable
fun AvakhadaChakraView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val moonPos = chart.planets.firstOrNull { it.planet == Planet.MOON }
    val sunPos = chart.planets.firstOrNull { it.planet == Planet.SUN }

    val nakshatra = moonPos?.nakshatra
    val moonSign = moonPos?.sign
    val pada = moonPos?.pada ?: 1
    val lagnaSign = chart.lagna.sign
    val moonHouse = moonPos?.house ?: 1

    // Calculation of Paya and Namakshar
    val (payaHi, payaEn) = VedicAnalysisEngine.calculatePaya(moonHouse)
    val namakshar = if (nakshatra != null) {
        VedicAnalysisEngine.getNamakshar(nakshatra, pada)
    } else "अ (A)"

    val calendar = Calendar.getInstance().apply {
        set(chart.birthDetails.year, chart.birthDetails.month - 1, chart.birthDetails.day)
    }
    val dayOfWeekEn = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        else -> "Saturday"
    }
    val dayOfWeekHi = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "रविवार"
        Calendar.MONDAY -> "सोमवार"
        Calendar.TUESDAY -> "मंगलवार"
        Calendar.WEDNESDAY -> "बुधवार"
        Calendar.THURSDAY -> "गुरुवार"
        Calendar.FRIDAY -> "शुक्रवार"
        else -> "शनिवार"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("basic_details_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. BIRTH DETAILS CARD (जन्म विवरण) ---
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
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = CelestialGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isHindi) "जन्म विवरण (Birth Details)" else "Birth Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright
                            )
                            Text(
                                text = "${chart.birthDetails.name} • ${if (isHindi) "प्रामाणिक समय" else "Standard Time"}",
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
                            text = if (isHindi) "लाहिड़ी अयनांश" else "Lahiri Ayanamsha",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstroSageOrangeLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val birthRows = listOf(
                    Pair(if (isHindi) "नाम (Name)" else "Name", chart.birthDetails.name),
                    Pair(if (isHindi) "लिंग (Gender)" else "Gender", chart.birthDetails.gender),
                    Pair(if (isHindi) "जन्म दिनांक (Date of Birth)" else "Date of Birth", String.format(Locale.US, "%02d/%02d/%04d", chart.birthDetails.day, chart.birthDetails.month, chart.birthDetails.year)),
                    Pair(if (isHindi) "जन्म समय (Time of Birth)" else "Time of Birth", String.format(Locale.US, "%02d:%02d", chart.birthDetails.hour, chart.birthDetails.minute)),
                    Pair(if (isHindi) "जन्म वार (Day of Birth)" else "Day of Birth", if (isHindi) dayOfWeekHi else dayOfWeekEn),
                    Pair(if (isHindi) "जन्म स्थान (Birth Place)" else "Place of Birth", chart.birthDetails.placeName),
                    Pair(if (isHindi) "अक्षांश / रेखांश (Lat/Long)" else "Lat / Long", "${String.format(Locale.US, "%.2f° N", chart.birthDetails.latitude)}, ${String.format(Locale.US, "%.2f° E", chart.birthDetails.longitude)}"),
                    Pair(if (isHindi) "समय क्षेत्र (Timezone)" else "Timezone", "IST (UTC +5:30)"),
                    Pair(if (isHindi) "सूर्योदय / सूर्यास्त (Sun Timing)" else "Sunrise / Sunset", "05:42 AM / 06:48 PM"),
                    Pair(if (isHindi) "अयनांश (Ayanamsha)" else "Ayanamsha", "Lahiri (Chitrapaksha) 23° 47' 12\"")
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
                ) {
                    birthRows.forEachIndexed { index, (key, value) ->
                        val isEven = index % 2 == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isEven) CosmicNavySurface else CosmicNavyCard)
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = key, fontSize = 12.sp, color = TextSecondary)
                            Text(text = value, fontSize = 12.sp, color = CelestialGoldBright, fontWeight = FontWeight.SemiBold)
                        }
                        if (index < birthRows.size - 1) {
                            HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

        // --- 2. AVAKHADA CHAKRA CARD (अवकहड़ा चक्र) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("avakhada_chakra_card"),
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
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = AstroSageOrangeLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isHindi) "अवकहड़ा चक्र (Avakahada Chakra)" else "Avakhada Chakra",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstroSageOrangeLight
                            )
                            Text(
                                text = if (isHindi) "वैदिक पंचांग एवं नक्षत्र तत्व" else "Vedic Panchang & Nakshatra Elements",
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
                            text = if (isHindi) "वैदिक मानक" else "Vedic Standard",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstroSageOrangeLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val avakhadaRows = listOf(
                    Pair(if (isHindi) "पाया (Paya)" else "Paya", if (isHindi) payaHi else payaEn),
                    Pair(if (isHindi) "वर्ण (Varna)" else "Varna", nakshatra?.varna ?: "Brahmin"),
                    Pair(if (isHindi) "वश्य (Vashya)" else "Vashya", if (isHindi) "चतुष्पद / मानव" else "Chatushpada / Manava"),
                    Pair(if (isHindi) "तारा (Tara)" else "Tara", VedicAnalysisEngine.getTaraName(isHindi)),
                    Pair(if (isHindi) "योनि (Yoni)" else "Yoni", nakshatra?.yoni ?: "Horse"),
                    Pair(if (isHindi) "गण (Gana)" else "Gana", nakshatra?.gana ?: "Deva"),
                    Pair(if (isHindi) "नाड़ी (Nadi)" else "Nadi", nakshatra?.nadi ?: "Adi"),
                    Pair(if (isHindi) "नामाक्षर (First Letter)" else "Namakshar", namakshar),
                    Pair(if (isHindi) "लग्न (Ascendant)" else "Ascendant", "${lagnaSign.sanskritName} (${lagnaSign.englishName})"),
                    Pair(if (isHindi) "लग्न स्वामी (Lagna Lord)" else "Lagna Lord", lagnaSign.ruler.sanskritName),
                    Pair(if (isHindi) "चंद्र राशि (Moon Sign)" else "Moon Sign", "${moonSign?.sanskritName} (${moonSign?.englishName})"),
                    Pair(if (isHindi) "राशि स्वामी (Rashi Lord)" else "Rashi Lord", moonSign?.ruler?.sanskritName ?: ""),
                    Pair(if (isHindi) "नक्षत्र व चरण (Nakshatra-Pada)" else "Nakshatra & Pada", "${nakshatra?.englishName} (चरण $pada)"),
                    Pair(if (isHindi) "नक्षत्र स्वामी (Nakshatra Lord)" else "Nakshatra Lord", nakshatra?.lord?.sanskritName ?: ""),
                    Pair(if (isHindi) "सूर्य राशि (Sun Sign)" else "Sun Sign", "${sunPos?.sign?.sanskritName} (${sunPos?.sign?.englishName})"),
                    Pair(if (isHindi) "तत्व (Element)" else "Element", lagnaSign.element)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
                ) {
                    avakhadaRows.forEachIndexed { index, (key, value) ->
                        val isEven = index % 2 == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isEven) CosmicNavySurface else CosmicNavyCard)
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = key, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(text = value, fontSize = 12.sp, color = CelestialGoldBright, fontWeight = FontWeight.SemiBold)
                        }
                        if (index < avakhadaRows.size - 1) {
                            HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}
