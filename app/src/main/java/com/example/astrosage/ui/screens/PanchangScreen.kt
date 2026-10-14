package com.example.astrosage.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.ChoghadiyaItem
import com.example.astrosage.core.astronomy.PanchangData
import com.example.astrosage.ui.CityLocation
import com.example.astrosage.ui.MainViewModel
import com.example.astrosage.ui.components.PanchangCard
import com.example.astrosage.ui.theme.AuspiciousGreen
import com.example.astrosage.ui.theme.GoldAccent
import com.example.astrosage.ui.theme.InauspiciousRed
import com.example.astrosage.ui.theme.NeutralBlue
import com.example.astrosage.ui.theme.SaffronPrimary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangScreen(
    viewModel: MainViewModel,
    onNavigateToKundali: () -> Unit = {}
) {
    val panchang by viewModel.panchangState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val currentCal by viewModel.currentCalendar.collectAsState()

    var showCityDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("पंचांग (Main)", "शुभ-अशुभ मुहूर्त", "चौघड़िया (Choghadiya)", "संवत व चन्द्रबल")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "दैनिक पंचांग",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${panchang.cityName.substringBefore(" (")} • ${panchang.dateString}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCityDialog = true },
                        modifier = Modifier.testTag("change_city_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "स्थान बदलें",
                            tint = SaffronPrimary
                        )
                    }
                    IconButton(
                        onClick = onNavigateToKundali,
                        modifier = Modifier.testTag("nav_kundali_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "कुंडली देखें",
                            tint = GoldAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Date Switcher Bar (Yesterday, Today, Tomorrow)
            DateSwitcherRow(
                currentCal = currentCal,
                onPreviousDay = { viewModel.changeDateOffset(-1) },
                onToday = { viewModel.setCalendarDate(Calendar.getInstance()) },
                onNextDay = { viewModel.changeDateOffset(1) }
            )

            // Category Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth().testTag("panchang_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> MainPanchangTab(panchang = panchang)
                1 -> MuhuratTab(panchang = panchang)
                2 -> ChoghadiyaTab(panchang = panchang)
                3 -> SamvatAndStrengthTab(panchang = panchang)
            }
        }
    }

    if (showCityDialog) {
        CitySelectionDialog(
            cities = viewModel.popularCities,
            selectedCity = selectedCity,
            onSelect = {
                viewModel.setCity(it)
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false }
        )
    }
}

@Composable
private fun DateSwitcherRow(
    currentCal: Calendar,
    onPreviousDay: () -> Unit,
    onToday: () -> Unit,
    onNextDay: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDay, modifier = Modifier.testTag("prev_day_button")) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "पिछला दिन")
            }

            OutlinedButton(
                onClick = onToday,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("today_button")
            ) {
                Icon(Icons.Default.Today, contentDescription = "आज", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("आज (Today)")
            }

            IconButton(onClick = onNextDay, modifier = Modifier.testTag("next_day_button")) {
                Icon(Icons.Default.ChevronRight, contentDescription = "अगला दिन")
            }
        }
    }
}

@Composable
private fun MainPanchangTab(panchang: PanchangData) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
        item {
            PanchangCard(panchang = panchang)
        }

        // Festivals & Vrat Banner
        if (panchang.festivals.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("festivals_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = "पर्व", tint = SaffronPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "आज के व्रत एवं प्रमुख पर्व (Festivals & Vrat)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        panchang.festivals.forEach { fest ->
                            Text(
                                text = "• $fest",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }

        // Detailed Astronomical Durations
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("sun_moon_details_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "खगोलीय मान (Astronomical Timings)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailDataRow(label = "सूर्योदय (Sunrise)", value = panchang.sunrise)
                    DetailDataRow(label = "सूर्यास्त (Sunset)", value = panchang.sunset)
                    DetailDataRow(label = "चन्द्रोदय (Moonrise)", value = panchang.moonrise)
                    DetailDataRow(label = "चन्द्रास्त (Moonset)", value = panchang.moonset)
                    DetailDataRow(label = "मध्याह्न काल (Solar Noon)", value = panchang.solarNoon)
                    DetailDataRow(label = "दिनमान (Day Duration)", value = panchang.dinMaan)
                    DetailDataRow(label = "रात्रिमान (Night Duration)", value = panchang.ratriMaan)
                    DetailDataRow(label = "सूर्य राशि (Sun Sign)", value = panchang.sunSign)
                    DetailDataRow(label = "चन्द्र राशि (Moon Sign)", value = panchang.moonSign)
                }
            }
        }
    }
}

@Composable
private fun MuhuratTab(panchang: PanchangData) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Auspicious Muhurats
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("auspicious_muhurats_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AuspiciousGreen.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "शुभ", tint = AuspiciousGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "शुभ मुहूर्त (Auspicious Timings)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuspiciousGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailDataRow(label = "अभिजित मुहूर्त (Abhijit)", value = panchang.abhijitMuhurat, isBold = true)
                    DetailDataRow(label = "अमृत काल (Amrit Kaal)", value = panchang.amritKaal)
                    DetailDataRow(label = "ब्रह्म मुहूर्त (Brahma Muhurat)", value = panchang.brahmaMuhurat)
                }
            }
        }

        // Inauspicious Muhurats (Rahu Kaal etc)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("inauspicious_muhurats_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = InauspiciousRed.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "अशुभ", tint = InauspiciousRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "अशुभ मुहूर्त (Inauspicious Timings)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InauspiciousRed
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailDataRow(label = "राहुकाल (Rahu Kaal)", value = panchang.rahuKaal, isBold = true, isWarning = true)
                    DetailDataRow(label = "यमगण्ड (Yamaganda)", value = panchang.yamaganda)
                    DetailDataRow(label = "गुलिक काल (Gulika Kaal)", value = panchang.gulikaKaal)
                    DetailDataRow(label = "दुर्मुहूर्त (Dur Muhurat)", value = panchang.durMuhurat)
                    DetailDataRow(label = "दिशाशूल (Inauspicious Travel Dir)", value = panchang.dishashool)
                }
            }
        }
    }
}

@Composable
private fun ChoghadiyaTab(panchang: PanchangData) {
    var isDayMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = isDayMode,
                onClick = { isDayMode = true },
                label = { Text("दिन का चौघड़िया (Day)") },
                leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("day_choghadiya_chip")
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterChip(
                selected = !isDayMode,
                onClick = { isDayMode = false },
                label = { Text("रात का चौघड़िया (Night)") },
                leadingIcon = { Icon(Icons.Default.NightsStay, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("night_choghadiya_chip")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val list = if (isDayMode) panchang.dayChoghadiya else panchang.nightChoghadiya

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("choghadiya_list")
        ) {
            items(list) { item ->
                ChoghadiyaCard(item = item)
            }
        }
    }
}

@Composable
private fun ChoghadiyaCard(item: ChoghadiyaItem) {
    val isGood = item.type in listOf("शुभ", "लाभ", "अमृत")
    val isNeutral = item.type == "चल"
    val badgeColor = if (isGood) AuspiciousGreen else if (isNeutral) NeutralBlue else InauspiciousRed

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${item.nature.substringBefore(" ")})",
                        style = MaterialTheme.typography.bodySmall,
                        color = badgeColor
                    )
                }
                Text(
                    text = "स्वामी: ${item.lord}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${item.startTime} - ${item.endTime}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun SamvatAndStrengthTab(panchang: PanchangData) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Samvat details
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("samvat_details_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "संवत एवं मास विवरण (Vedic Calendar)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailDataRow(label = "संवत्सर (Jovian Year)", value = panchang.samvatsaraName)
                    DetailDataRow(label = "विक्रम संवत", value = panchang.vikramSamvat.toString())
                    DetailDataRow(label = "शक संवत", value = panchang.shakaSamvat.toString())
                    DetailDataRow(label = "पूर्णिमांत मास", value = panchang.hinduMonthPurnimanta)
                    DetailDataRow(label = "अमांत मास", value = panchang.hinduMonthAmanta)
                    DetailDataRow(label = "ऋतु (Season)", value = panchang.ritu)
                    DetailDataRow(label = "अयन", value = panchang.ayana)
                    DetailDataRow(label = "चन्द्र वास (Chandra Vasa)", value = panchang.chandraVasa)
                }
            }
        }

        // Chandrabal Signs
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("chandrabal_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "चन्द्रबल (Auspicious Signs Today)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "आज चन्द्रमा की स्थिति के अनुसार इन राशियों के लिए दिन अत्यंत फलदायी व अनुकूल है:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        panchang.chandrabalSigns.take(6).forEach { sign ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AuspiciousGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = sign,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = AuspiciousGreen,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailDataRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isWarning: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                color = if (isWarning) InauspiciousRed else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun CitySelectionDialog(
    cities: List<CityLocation>,
    selectedCity: CityLocation,
    onSelect: (CityLocation) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("स्थान चुनें (Select City)") },
        text = {
            LazyColumn {
                items(cities) { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(city) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = city.nameHi,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (city == selectedCity) FontWeight.Bold else FontWeight.Normal,
                                color = if (city == selectedCity) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (city == selectedCity) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SaffronPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("बंद करें") }
        }
    )
}
