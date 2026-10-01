package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.Planet
import com.example.astrosage.ui.MainViewModel
import com.example.astrosage.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    KUNDALI_INPUT,
    KUNDALI_DETAIL,
    KUNDALI_MATCHING,
    PANCHANG,
    SAVED_PROFILES
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
                val isHindi by viewModel.isHindi.collectAsState()
                val currentChart by viewModel.currentChart.collectAsState()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // System Back button handling
                if (currentScreen != AppScreen.HOME) {
                    BackHandler {
                        currentScreen = AppScreen.HOME
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = CosmicNavySurface,
                            drawerContentColor = TextPrimary,
                            modifier = Modifier.width(300.dp)
                        ) {
                            // Drawer Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AstroSageOrange)
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("☀️", fontSize = 24.sp)
                                        Text(
                                            text = "AstroSage AI",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isHindi) "भारत का विश्वसनीय ज्योतिष ऐप" else "India's Trusted Vedic Astrology App",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )

                                    currentChart?.let { chart ->
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Surface(
                                            color = Color.White.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(Color.White, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = chart.birthDetails.name.take(1).uppercase(),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AstroSageOrange
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = chart.birthDetails.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                    val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
                                                    Text(
                                                        text = "${chart.lagna.sign.sanskritName} • ${moon?.sign?.sanskritName ?: ""}",
                                                        fontSize = 10.sp,
                                                        color = CelestialGoldBright
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Drawer Navigation Items
                            NavigationDrawerItem(
                                label = { Text(if (isHindi) "मुख्य पृष्ठ (Home)" else "Home") },
                                icon = { Icon(Icons.Default.Home, contentDescription = null, tint = AstroSageOrangeLight) },
                                selected = currentScreen == AppScreen.HOME,
                                onClick = {
                                    currentScreen = AppScreen.HOME
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                label = { Text(if (isHindi) "जन्म कुंडली (Kundali)" else "Birth Chart (Kundali)") },
                                icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = CelestialGoldBright) },
                                selected = currentScreen == AppScreen.KUNDALI_DETAIL,
                                onClick = {
                                    currentScreen = if (viewModel.currentChart.value != null) AppScreen.KUNDALI_DETAIL else AppScreen.KUNDALI_INPUT
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                label = { Text(if (isHindi) "कुंडली मिलान (36 Guna)" else "Kundli Matching (36 Gunas)") },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = AuspiciousRed) },
                                selected = currentScreen == AppScreen.KUNDALI_MATCHING,
                                onClick = {
                                    currentScreen = AppScreen.KUNDALI_MATCHING
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                label = { Text(if (isHindi) "दैनिक पंचांग (Panchang)" else "Daily Panchang") },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AstroSageOrangeLight) },
                                selected = currentScreen == AppScreen.PANCHANG,
                                onClick = {
                                    currentScreen = AppScreen.PANCHANG
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                label = { Text(if (isHindi) "सहेजी गई कुंडलियां (Saved)" else "Saved Kundali Profiles") },
                                icon = { Icon(Icons.Default.People, contentDescription = null, tint = MysticPurple) },
                                selected = currentScreen == AppScreen.SAVED_PROFILES,
                                onClick = {
                                    currentScreen = AppScreen.SAVED_PROFILES
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CosmicBorder)

                            // Language Switcher Tile in Drawer
                            Surface(
                                color = CosmicNavyCard,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clickable { viewModel.toggleLanguage() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("🌐", fontSize = 16.sp)
                                        Text(
                                            text = if (isHindi) "भाषा (Language)" else "App Language",
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                    }
                                    Surface(
                                        color = AstroSageOrange,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "हिन्दी" else "English",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = CosmicNavyDark,
                        topBar = {
                            if (currentScreen == AppScreen.HOME) {
                                TopAppBar(
                                    title = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("☀️", fontSize = 20.sp)
                                            Column {
                                                Text(
                                                    text = "AstroSage AI",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = if (isHindi) "वैदिक ज्योतिष व राशिफल" else "Vedic Astrology & Predictions",
                                                    fontSize = 9.sp,
                                                    color = Color.White.copy(alpha = 0.85f)
                                                )
                                            }
                                        }
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = "Menu",
                                                tint = Color.White
                                            )
                                        }
                                    },
                                    actions = {
                                        // Language toggle pill
                                        Surface(
                                            color = Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .clickable { viewModel.toggleLanguage() }
                                                .padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isHindi) "अ / Eng" else "Eng / अ",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        // Wallet balance badge
                                        Surface(
                                            color = CelestialGold.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("🪙", fontSize = 11.sp)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = if (isHindi) "₹ 100" else "₹ 100",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CelestialGoldBright
                                                )
                                            }
                                        }

                                        // Saved profiles button
                                        IconButton(onClick = { currentScreen = AppScreen.SAVED_PROFILES }) {
                                            Icon(
                                                imageVector = Icons.Default.People,
                                                contentDescription = "Saved Kundalis",
                                                tint = Color.White
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AstroSageOrange)
                                )
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = CosmicNavySurface,
                                contentColor = TextPrimary,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .testTag("main_bottom_nav")
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.HOME,
                                    onClick = { currentScreen = AppScreen.HOME },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text(if (isHindi) "होम" else "Home", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AstroSageOrangeLight,
                                        indicatorColor = AstroSageOrange,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.KUNDALI_INPUT || currentScreen == AppScreen.KUNDALI_DETAIL,
                                    onClick = {
                                        if (viewModel.currentChart.value != null) {
                                            currentScreen = AppScreen.KUNDALI_DETAIL
                                        } else {
                                            currentScreen = AppScreen.KUNDALI_INPUT
                                        }
                                    },
                                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = "Kundali") },
                                    label = { Text(if (isHindi) "कुंडली" else "Kundali", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AstroSageOrangeLight,
                                        indicatorColor = AstroSageOrange,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_kundali")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.KUNDALI_MATCHING,
                                    onClick = { currentScreen = AppScreen.KUNDALI_MATCHING },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Matching") },
                                    label = { Text(if (isHindi) "मिलान" else "Matching", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AstroSageOrangeLight,
                                        indicatorColor = AstroSageOrange,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_matching")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.PANCHANG,
                                    onClick = { currentScreen = AppScreen.PANCHANG },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Panchang") },
                                    label = { Text(if (isHindi) "पंचांग" else "Panchang", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AstroSageOrangeLight,
                                        indicatorColor = AstroSageOrange,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_panchang")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.SAVED_PROFILES,
                                    onClick = { currentScreen = AppScreen.SAVED_PROFILES },
                                    icon = { Icon(Icons.Default.People, contentDescription = "Profiles") },
                                    label = { Text(if (isHindi) "प्रोफाइल" else "Profiles", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AstroSageOrangeLight,
                                        indicatorColor = AstroSageOrange,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_profiles")
                                )
                            }
                        }
                    ) { innerPadding ->
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToNewKundali = { currentScreen = AppScreen.KUNDALI_INPUT },
                                onNavigateToChartDetail = { currentScreen = AppScreen.KUNDALI_DETAIL },
                                onNavigateToMatching = { currentScreen = AppScreen.KUNDALI_MATCHING },
                                onNavigateToPanchang = { currentScreen = AppScreen.PANCHANG },
                                onNavigateToSavedProfiles = { currentScreen = AppScreen.SAVED_PROFILES },
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.KUNDALI_INPUT -> KundaliInputScreen(
                                viewModel = viewModel,
                                onChartCalculated = { currentScreen = AppScreen.KUNDALI_DETAIL },
                                onBack = { currentScreen = AppScreen.HOME },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.KUNDALI_DETAIL -> KundaliDetailScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.KUNDALI_MATCHING -> KundaliMatchingScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.PANCHANG -> PanchangScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.SAVED_PROFILES -> SavedProfilesScreen(
                                viewModel = viewModel,
                                onProfileSelected = { currentScreen = AppScreen.KUNDALI_DETAIL },
                                onAddNew = { currentScreen = AppScreen.KUNDALI_INPUT },
                                onBack = { currentScreen = AppScreen.HOME },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
