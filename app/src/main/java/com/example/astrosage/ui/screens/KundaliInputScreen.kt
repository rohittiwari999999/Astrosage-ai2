package com.example.astrosage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.BirthDetails
import com.example.astrosage.ui.MainViewModel
import com.example.ui.theme.*

data class CityPreset(val name: String, val lat: Double, val lon: Double)

val POPULAR_CITIES = listOf(
    CityPreset("New Delhi, India", 28.6139, 77.2090),
    CityPreset("Mumbai, India", 19.0760, 72.8777),
    CityPreset("Bengaluru, India", 12.9716, 77.5946),
    CityPreset("Kolkata, India", 22.5726, 88.3639),
    CityPreset("Chennai, India", 13.0827, 80.2707),
    CityPreset("Varanasi, India", 25.3176, 82.9739),
    CityPreset("Ayodhya, India", 26.7922, 82.1998),
    CityPreset("Ujjain, India", 23.1765, 75.7885),
    CityPreset("Jaipur, India", 26.9124, 75.7873),
    CityPreset("London, UK", 51.5074, -0.1278),
    CityPreset("New York, USA", 40.7128, -74.0060)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundaliInputScreen(
    viewModel: MainViewModel,
    onChartCalculated: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi by viewModel.isHindi.collectAsState()

    var name by remember { mutableStateOf("Aarav Sharma") }
    var gender by remember { mutableStateOf("Male") }
    var day by remember { mutableStateOf("15") }
    var month by remember { mutableStateOf("5") }
    var year by remember { mutableStateOf("1995") }
    var hour by remember { mutableStateOf("10") }
    var minute by remember { mutableStateOf("30") }
    var selectedCity by remember { mutableStateOf(POPULAR_CITIES.first()) }
    var saveToProfiles by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "नवीन जन्म कुंडली" else "New Kundali (Birth Chart)",
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = if (isHindi) "सटीक जन्म विवरण प्रविष्ट करें" else "Enter Exact Birth Particulars",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi)
                        "स्विस एफिमेरिस व लाहिड़ी अयनांश के आधार पर शून्य त्रुटि निरयण ग्रह स्थिति।"
                    else
                        "Ephemeris engine computes precise Nirayana coordinates using Lahiri Ayanamsha.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            // Name & Gender
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isHindi) "पूरा नाम" else "Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_name"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("Male", "Female").forEach { g ->
                                val isSelected = gender == g
                                val label = if (isHindi) (if (g == "Male") "पुरुष (Male)" else "स्त्री (Female)") else g
                                Surface(
                                    color = if (isSelected) AstroSageOrange else CosmicNavyElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { gender = g }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Date of Birth
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "जन्म तिथि (Date of Birth)" else "Date of Birth",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = day,
                                onValueChange = { day = it },
                                label = { Text(if (isHindi) "दिन (DD)" else "Day") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_day"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = month,
                                onValueChange = { month = it },
                                label = { Text(if (isHindi) "माह (MM)" else "Month") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_month"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = year,
                                onValueChange = { year = it },
                                label = { Text(if (isHindi) "वर्ष (YYYY)" else "Year") },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("input_year"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Time of Birth
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "जन्म समय (24-Hour Format)" else "Time of Birth (24-Hour Format)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = hour,
                                onValueChange = { hour = it },
                                label = { Text(if (isHindi) "घंटे (0-23)" else "Hour") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_hour"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = minute,
                                onValueChange = { minute = it },
                                label = { Text(if (isHindi) "मिनट (0-59)" else "Minute") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_minute"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Birth City
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "जन्म स्थान (Birth City)" else "Birth Place (Coordinates)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(POPULAR_CITIES) { city ->
                                val isSelected = selectedCity == city
                                Surface(
                                    color = if (isSelected) AstroSageOrange else CosmicNavyElevated,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { selectedCity = city }
                                ) {
                                    Text(
                                        text = city.name.split(",").first(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${selectedCity.name} (${selectedCity.lat}° N, ${selectedCity.lon}° E)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Save to offline room cache checkbox
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { saveToProfiles = !saveToProfiles }
                ) {
                    Checkbox(
                        checked = saveToProfiles,
                        onCheckedChange = { saveToProfiles = it },
                        colors = CheckboxDefaults.colors(checkedColor = AstroSageOrange)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "इस कुंडली को सहेजें (Offline Room Cache)" else "Save this Kundali profile for offline access",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }

            // Generate Button
            item {
                Button(
                    onClick = {
                        val birthDetails = BirthDetails(
                            name = name.ifBlank { "User" },
                            gender = gender,
                            year = year.toIntOrNull() ?: 1995,
                            month = month.toIntOrNull() ?: 5,
                            day = day.toIntOrNull() ?: 15,
                            hour = hour.toIntOrNull() ?: 10,
                            minute = minute.toIntOrNull() ?: 30,
                            placeName = selectedCity.name,
                            latitude = selectedCity.lat,
                            longitude = selectedCity.lon,
                            timezoneOffsetHours = 5.5
                        )

                        if (saveToProfiles) {
                            viewModel.saveNewProfile(birthDetails)
                        } else {
                            viewModel.calculateChart(birthDetails)
                        }

                        onChartCalculated()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_chart_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "कुंडली बनाएं (Generate Kundali)" else "Generate Vedic Kundali",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
