package com.example.astrosage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.astrosage.core.AstroData
import com.example.astrosage.core.AstroReportItem
import com.example.astrosage.core.ServiceItem
import com.example.astrosage.core.astronomy.Planet
import com.example.astrosage.core.astronomy.ZodiacSign
import com.example.astrosage.ui.MainViewModel
import com.example.astrosage.ui.components.AstrologyReportDialog
import com.example.astrosage.ui.components.PanchangCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToNewKundali: () -> Unit,
    onNavigateToChartDetail: () -> Unit,
    onNavigateToMatching: () -> Unit,
    onNavigateToPanchang: () -> Unit,
    onNavigateToSavedProfiles: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val panchang by viewModel.panchangData.collectAsState()
    val currentChart by viewModel.currentChart.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val selectedReport by viewModel.selectedReport.collectAsState()

    var selectedRashi by remember { mutableStateOf(ZodiacSign.TAURUS) }

    // Dialog for displaying reports
    selectedReport?.let { report ->
        AstrologyReportDialog(
            report = report,
            chartData = currentChart,
            isHindi = isHindi,
            onDismiss = { viewModel.closeReport() }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicNavyDark)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Profile Bar (if a chart is loaded)
        item {
            currentChart?.let { chart ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToChartDetail() }
                        .testTag("active_profile_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavySurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AstroSageOrange.copy(alpha = 0.5f))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(AstroSageOrange, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chart.birthDetails.name.take(1).uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = if (isHindi) "${chart.birthDetails.name} की कुंडली" else "${chart.birthDetails.name}'s Kundali",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
                                Text(
                                    text = "${if (isHindi) "लग्न" else "Lagna"}: ${chart.lagna.sign.sanskritName} • ${chart.currentMahadasha.sanskritName} ${if (isHindi) "दशा" else "Dasha"}",
                                    fontSize = 11.sp,
                                    color = CelestialGoldBright
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToChartDetail,
                            colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "कुंडली देखें" else "View Chart",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // AstroSage Signature Services (12 Services Circular Icons Grid)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicNavySurface, RoundedCornerShape(16.dp))
                    .border(1.dp, CosmicBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "प्रमुख सेवाएं (Astro Services)" else "AstroSage Services",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGoldBright
                    )
                    Text(
                        text = if (isHindi) "सभी 12 सेवाएं" else "All 12 Services",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4x3 Grid of Circular Services
                val rows = AstroData.quickServices.chunked(4)
                rows.forEach { serviceRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        serviceRow.forEach { service ->
                            ServiceCircleItem(
                                service = service,
                                isHindi = isHindi,
                                onClick = {
                                    when (service.id) {
                                        "kundali" -> onNavigateToChartDetail()
                                        "matching" -> onNavigateToMatching()
                                        "panchang" -> onNavigateToPanchang()
                                        "ai_astrologer" -> onNavigateToChartDetail()
                                        else -> {
                                            // Open matching report or kundali
                                            val matchedReport = AstroData.astrologyReports.firstOrNull {
                                                it.id.contains(service.id.take(4))
                                            } ?: AstroData.astrologyReports.first()
                                            viewModel.openReport(matchedReport)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Hero Banner: Swiss Ephemeris & Gemini AI
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { onNavigateToNewKundali() }
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(AstroSageOrange, CelestialGold))
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.astrology_hero),
                        contentDescription = "Cosmic Astrology Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.30f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = AstroSageOrange.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SWISS EPHEMERIS + GEMINI AI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "सटीक ग्रह स्थिति व जन्म कुंडली" else "Exact Planetary Coordinates & Kundali",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isHindi) "पाराशरी व केपी सिद्धांत • शून्य त्रुटि गणना" else "Zero Hallucination Degrees • Parashari & KP Logic",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // AstroSage Reports Grid ("रिपोर्ट / Reports" - heavily featured in the screenshot!)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicNavySurface, RoundedCornerShape(16.dp))
                    .border(1.dp, CosmicBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("📊", fontSize = 16.sp)
                        Text(
                            text = if (isHindi) "ज्योतिष रिपोर्ट (Astrology Reports)" else "Astrology Reports",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                    }
                    Text(
                        text = if (isHindi) "मुफ्त पढ़ें" else "Read Free",
                        fontSize = 11.sp,
                        color = AstroSageOrangeLight,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2-Column Grid of Reports
                val reportPairs = AstroData.astrologyReports.chunked(2)
                reportPairs.forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { report ->
                            ReportCard(
                                report = report,
                                isHindi = isHindi,
                                onClick = { viewModel.openReport(report) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Daily Panchang Card
        item {
            PanchangCard(
                panchang = panchang,
                onViewFullPanchang = onNavigateToPanchang
            )
        }

        // Today's Daily Horoscope (Rashifal)
        item {
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
                        Text(
                            text = if (isHindi) "दैनिक राशिफल (Today's Horoscope)" else "Daily Horoscope (Rashifal)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold
                        )
                        Surface(
                            color = AstroSageOrange.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isHindi) "आज का दिन" else "Today",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstroSageOrangeLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isHindi) "अपनी चंद्र राशि का चयन करें" else "Select your Moon Sign (Chandra Rashi)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 12 Signs Horizontal Selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ZodiacSign.entries) { sign ->
                            val isSelected = selectedRashi == sign
                            Surface(
                                color = if (isSelected) AstroSageOrange else CosmicNavyElevated,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { selectedRashi = sign }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = sign.symbol,
                                        fontSize = 16.sp,
                                        color = if (isSelected) Color.White else CelestialGoldBright
                                    )
                                    Text(
                                        text = if (isHindi) sign.sanskritName else sign.englishName,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rashi prediction snippet
                    Surface(
                        color = CosmicNavySurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${selectedRashi.sanskritName} (${selectedRashi.englishName})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CelestialGoldBright
                                )
                                Text(
                                    text = "${if (isHindi) "स्वामी" else "Lord"}: ${selectedRashi.ruler.sanskritName} • ${selectedRashi.element}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = getRashiPrediction(selectedRashi, isHindi),
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCircleItem(
    service: ServiceItem,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
            .testTag("service_${service.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(service.badgeColorHex).copy(alpha = 0.18f), CircleShape)
                .border(1.5.dp, Color(service.badgeColorHex).copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(service.iconEmoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isHindi) service.titleHi else service.titleEn,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun ReportCard(
    report: AstroReportItem,
    isHindi: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("report_card_${report.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(report.accentColorHex).copy(alpha = 0.35f))
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(report.iconEmoji, fontSize = 20.sp)
                Surface(
                    color = Color(report.accentColorHex).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isHindi) report.tagHi else report.tagEn,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(report.accentColorHex),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isHindi) report.titleHi else report.titleEn,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isHindi) report.descHi else report.descEn,
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 13.sp
            )
        }
    }
}

private fun getRashiPrediction(sign: ZodiacSign, isHindi: Boolean): String {
    return if (isHindi) {
        when (sign) {
            ZodiacSign.ARIES -> "आज कार्यक्षेत्र में नई योजनाओं पर काम शुरू हो सकता है। आर्थिक स्थिति सुदृढ़ रहेगी। वाणी पर संयम रखें।"
            ZodiacSign.TAURUS -> "शुक्र का शुभ प्रभाव रचनात्मक कार्यों में सफलता दिलाएगा। पारिवारिक सुख व मित्रों का सहयोग प्राप्त होगा।"
            ZodiacSign.GEMINI -> "बुद्धिमानी से लिए गए निर्णय व्यापार में बड़ा लाभ दिलाएंगे। स्वास्थ्य अच्छा रहेगा, यात्रा के योग हैं।"
            ZodiacSign.CANCER -> "मानसिक शांति बनी रहेगी। माता का आशीर्वाद प्राप्त होगा। किसी पुराने मित्र से शुभ समाचार मिल सकता है।"
            ZodiacSign.LEO -> "सूर्य के तेज से आत्मविश्वास बढ़ेगा। अधिकारियों से प्रशंसा मिलेगी। वित्तीय मामलों में सोच-समझकर निर्णय लें।"
            ZodiacSign.VIRGO -> "विश्लेषणात्मक कार्यों व शिक्षा में विशेष सफलता मिलेगी। कानूनी मामलों में विजय के संकेत हैं।"
            ZodiacSign.LIBRA -> "सामाजिक मान-प्रतिष्ठा में वृद्धि होगी। वैवाहिक जीवन में मधुरता बनी रहेगी। संतुलित खान-पान रखें।"
            ZodiacSign.SCORPIO -> "मंगल के प्रभाव से पराक्रम बढ़ेगा। अटका हुआ धन प्राप्त होने के योग हैं। संपत्ति से लाभ संभव है।"
            ZodiacSign.SAGITTARIUS -> "गुरु के शुभ प्रभाव से धार्मिक कार्यों में रुचि बढ़ेगी। उच्च शिक्षा व करियर में सफलता मिलेगी।"
            ZodiacSign.CAPRICORN -> "मेहनत का पूर्ण फल मिलेगा। कार्यस्थल पर जिम्मेदारियां बढ़ सकती हैं। नियमित दिनचर्या का पालन करें।"
            ZodiacSign.AQUARIUS -> "नवीन विचारों से कार्य में प्रगति होगी। मित्रों व सहयोगियों का पूरा साथ मिलेगा। शाम को ध्यान लगाएं।"
            ZodiacSign.PISCES -> "अध्यात्म व रचनात्मक कार्यों में मन लगेगा। विदेश या दूरस्थ स्थानों से लाभ का योग है। धन का सदुपयोग होगा।"
        }
    } else {
        when (sign) {
            ZodiacSign.ARIES -> "Dynamic planetary alignments encourage ambitious career initiatives today. Financial prospects look steady. Avoid impulsive speech."
            ZodiacSign.TAURUS -> "Venus bestows favorable creative vibes. Excellent day for investments, home improvements, and family harmony."
            ZodiacSign.GEMINI -> "Sharp intellectual focus aids pending negotiations and communication. Good fortune in business."
            ZodiacSign.CANCER -> "Moon indicates heightened intuitive perception. Focus on emotional harmony and creative undertakings."
            ZodiacSign.LEO -> "Sun fuels your confidence and leadership. Authority figures will appreciate your contributions."
            ZodiacSign.VIRGO -> "Mercury brings meticulous problem-solving skills. Favorable for academic pursuits and analytical tasks."
            ZodiacSign.LIBRA -> "A day of balance and diplomatic harmony. Social invitations may open rewarding avenues."
            ZodiacSign.SCORPIO -> "Mars grants deep willpower and resilience. Unexpected positive news regarding career may emerge."
            ZodiacSign.SAGITTARIUS -> "Jupiter brings optimism and philosophical clarity. Favorable time for travel and dharmic endeavors."
            ZodiacSign.CAPRICORN -> "Saturn rewards methodical hard work. Professional stability is reinforced."
            ZodiacSign.AQUARIUS -> "Progressive thinking leads to breakthrough solutions. Collaborative teamwork will be fruitful."
            ZodiacSign.PISCES -> "Spiritual insight and compassion guide your decisions today. Financial gains through creative avenues are indicated."
        }
    }
}
