package com.example.astrosage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.ui.MainViewModel
import com.example.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val panchang by viewModel.panchangData.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    var currentCal by remember { mutableStateOf(Calendar.getInstance()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "दैनिक पंचांग" else "Daily Panchang",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.toggleLanguage() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text(
                            text = if (isHindi) "ENG" else "हिन्दी",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AstroSageOrange)
            )
        },
        containerColor = CosmicNavyDark
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Switcher Row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AstroSageOrange.copy(alpha = 0.4f))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCal = currentCal.clone() as Calendar
                            newCal.add(Calendar.DAY_OF_YEAR, -1)
                            currentCal = newCal
                            viewModel.refreshPanchangForDate(newCal)
                        }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = AstroSageOrangeLight)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = panchang.dateString,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright
                            )
                            Text(
                                text = "${panchang.vaar} • Vikram Samvat 2083",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = {
                            val newCal = currentCal.clone() as Calendar
                            newCal.add(Calendar.DAY_OF_YEAR, 1)
                            currentCal = newCal
                            viewModel.refreshPanchangForDate(newCal)
                        }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = AstroSageOrangeLight)
                        }
                    }
                }
            }

            // Sun & Moon Timings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "सूर्य समय (Sunrise & Sunset)" else "Sun Timings",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            TimingItem(if (isHindi) "सूर्योदय" else "Sunrise", panchang.sunrise, "🌅", CelestialGoldBright)
                            TimingItem(if (isHindi) "सूर्यास्त" else "Sunset", panchang.sunset, "🌇", SacredSaffron)
                        }
                    }
                }
            }

            // Five Limbs of Panchang (Panchanga Angas)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "पंचांग के पांच अंग" else "Panchanga Angas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        PanchangDetailRow(if (isHindi) "वार (Day)" else "Day", panchang.vaar)
                        PanchangDetailRow(if (isHindi) "तिथि (Tithi)" else "Tithi", "${panchang.tithi} (${panchang.paksha} पक्ष)")
                        PanchangDetailRow(if (isHindi) "नक्षत्र (Nakshatra)" else "Nakshatra", panchang.nakshatra)
                        PanchangDetailRow(if (isHindi) "योग (Yoga)" else "Yoga", panchang.yoga)
                        PanchangDetailRow(if (isHindi) "करण (Karana)" else "Karana", panchang.karana)
                    }
                }
            }

            // Auspicious & Inauspicious Timings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "शुभ व अशुभ मुहूर्त" else "Auspicious & Inauspicious Timings",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EmeraldGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isHindi) "अभिजित मुहूर्त (शुभ)" else "Abhijit Muhurat", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            Text(panchang.abhijitMuhurat, fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AuspiciousRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isHindi) "राहु काल (अशुभ)" else "Rahu Kaal", fontSize = 12.sp, color = AuspiciousRed, fontWeight = FontWeight.Bold)
                            Text(panchang.rahuKaal, fontSize = 12.sp, color = AuspiciousRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimingItem(label: String, time: String, icon: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(time, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = tint)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun PanchangDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
