package com.example.astrosage.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.astrosage.core.astronomy.*
import com.example.astrosage.ui.MainViewModel
import com.example.astrosage.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundaliDetailScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chartData by viewModel.currentChart.collectAsState()
    val aiReading by viewModel.currentAiReading.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatThinking by viewModel.isChatThinking.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val profiles by viewModel.profiles.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // Default to 0 (BASIC) matching screenshot
    var activeSubView by remember { mutableStateOf<String?>(null) } // Sub-menu opened from 6 golden icons or direct modal
    var showEditDialog by remember { mutableStateOf(false) }
    var showProfileDropdown by remember { mutableStateOf(false) }
    var showSectionDropdown by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showCloudDialog by remember { mutableStateOf(false) }

    // Intercept back button to return to Basic menu if inside a sub-view
    BackHandler(enabled = activeSubView != null || selectedTabIndex != 0) {
        if (activeSubView != null) {
            activeSubView = null
        } else {
            selectedTabIndex = 0
        }
    }

    // All 24 Kundali sub-menus in the top horizontal scrollable slider menu matching AstroSage
    val tabTitles = listOf(
        "BASIC",
        "LAGNA",
        "NAVAMSHA",
        "CLOUD",
        "MOON",
        "CHALIT",
        "PLANETS",
        "PLANETS-SUB",
        "CHALIT TABLE",
        "BIRTH DETAILS",
        "PANCHANG",
        "ASHTAKVARGA",
        "KARAKAMSHA",
        "SWAMSA",
        "TRANSIT",
        "SHAD BALA",
        "PRASTHARASHTAKVARGA",
        "BHAV MADHYA",
        "FRIENDSHIP",
        "PERSON DETAILS",
        "AVKAHADA CHAKRA",
        "GHATAK AND FAVOURABLE",
        "DOWNLOAD PDF",
        "REPORTS",
        "ASK QUESTIONS"
    )

    val currentTitle = when {
        activeSubView != null -> activeSubView!!
        selectedTabIndex == 0 -> "Basic"
        else -> tabTitles[selectedTabIndex]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showSectionDropdown = true }
                    ) {
                        Text(
                            text = currentTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Section",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Section Dropdown Menu
                    DropdownMenu(
                        expanded = showSectionDropdown,
                        onDismissRequest = { showSectionDropdown = false },
                        modifier = Modifier.background(CosmicNavyCard)
                    ) {
                        Text(
                            text = if (isHindi) "कुंडली विभाग (Sections)" else "Kundali Sections",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        HorizontalDivider(color = CosmicBorder)

                        tabTitles.forEachIndexed { idx, name ->
                            DropdownMenuItem(
                                text = { Text(name, color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    showSectionDropdown = false
                                    activeSubView = null
                                    selectedTabIndex = idx
                                }
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (activeSubView != null) {
                                activeSubView = null
                            } else if (selectedTabIndex != 0) {
                                selectedTabIndex = 0
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (activeSubView != null || selectedTabIndex != 0) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                            contentDescription = "Navigation",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // AstroSage Yellow Upgrade Button
                    Surface(
                        onClick = { showUpgradeDialog = true },
                        color = Color(0xFFFFC107),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("astrosage_upgrade_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Upgrade",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Upgrade",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Question / Help button
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Overflow Menu
                    var showOverflow by remember { mutableStateOf(false) }
                    IconButton(onClick = { showOverflow = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showOverflow,
                        onDismissRequest = { showOverflow = false },
                        modifier = Modifier.background(CosmicNavyCard)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Birth Details", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = AstroSageOrangeLight) },
                            onClick = {
                                showOverflow = false
                                showEditDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cloud Backup & Sync", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.CloudSync, contentDescription = null, tint = AstroSageOrangeLight) },
                            onClick = {
                                showOverflow = false
                                showCloudDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "Switch to English" else "हिन्दी में बदलें", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = AstroSageOrangeLight) },
                            onClick = {
                                showOverflow = false
                                viewModel.toggleLanguage()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share Kundali Summary", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = AstroSageOrangeLight) },
                            onClick = {
                                showOverflow = false
                                chartData?.let { shareKundaliSummary(context, it, isHindi) }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        if (chartData == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AstroSageOrange)
            }
        } else {
            val chart = chartData!!

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Black)
            ) {
                // Top Tab Bar with ALL 24 SUB-MENUS as requested
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Black,
                    contentColor = AstroSageOrange,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color.White
                            )
                        }
                    },
                    divider = {
                        HorizontalDivider(color = Color(0xFF222225), thickness = 1.dp)
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index && activeSubView == null,
                            onClick = {
                                selectedTabIndex = index
                                activeSubView = null
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (selectedTabIndex == index && activeSubView == null) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index && activeSubView == null) Color.White else Color(0xFF888890)
                                )
                            },
                            modifier = Modifier.testTag("astrosage_tab_${index}")
                        )
                    }
                }

                // If a sub-view was opened from the 6 golden icons or quick modal
                if (activeSubView != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            // Sub-view breadcrumb back header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF141416))
                                    .clickable { activeSubView = null }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Basic",
                                    tint = AstroSageOrangeLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Back to Basic ($activeSubView)",
                                    color = AstroSageOrangeLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        item {
                            when (activeSubView) {
                                "Dasha" -> {
                                    DashaTimelineView(
                                        dashaList = chart.dashaTimeline,
                                        currentMahadasha = chart.currentMahadasha,
                                        currentAntardasha = chart.currentAntardasha,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                "Predictions" -> DoshaRemediesView(chart = chart, isHindi = isHindi)
                                "KP System" -> PlanetsSubLordView(chart = chart, isHindi = isHindi)
                                "Shodashvarga" -> ShodashvargaView(chart = chart, isHindi = isHindi)
                                "Lal Kitab" -> LalKitabView(chart = chart, isHindi = isHindi)
                                "Varshphal" -> VarshphalView(chart = chart, isHindi = isHindi)
                                else -> {
                                    AvakhadaChakraView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                    }
                } else {
                    // Regular Tab Content across all 25 tabs
                    when (selectedTabIndex) {
                        0 -> {
                            // 0. BASIC TAB: MATCHES USER'S SCREENSHOT EXACTLY (Chat Bar + 6 Golden Icons + 2-Column Matrix)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black),
                                contentPadding = PaddingValues(bottom = 32.dp)
                            ) {
                                item {
                                    KundaliSubMenuDirectory(
                                        chartData = chart,
                                        chatMessages = chatMessages,
                                        isChatThinking = isChatThinking,
                                        onSendChat = { query ->
                                            viewModel.sendChatMessage(query)
                                        },
                                        onClearChat = {
                                            viewModel.clearChat()
                                        },
                                        onMenuItemClick = { item ->
                                            when (item) {
                                                // 6 Golden Shortcuts
                                                "Dasha" -> activeSubView = "Dasha"
                                                "Predictions" -> activeSubView = "Predictions"
                                                "KP System" -> selectedTabIndex = 7
                                                "Shodashvarga" -> activeSubView = "Shodashvarga"
                                                "Lal Kitab" -> activeSubView = "Lal Kitab"
                                                "Varshphal" -> activeSubView = "Varshphal"

                                                // 24 Grid Items mapped directly to corresponding Slider Tab
                                                "Lagna" -> selectedTabIndex = 1
                                                "Navamsha" -> selectedTabIndex = 2
                                                "Cloud" -> {
                                                    selectedTabIndex = 3
                                                    showCloudDialog = true
                                                }
                                                "Moon" -> selectedTabIndex = 4
                                                "Chalit" -> selectedTabIndex = 5
                                                "Planets" -> selectedTabIndex = 6
                                                "Planets-Sub" -> selectedTabIndex = 7
                                                "Chalit Table" -> selectedTabIndex = 8
                                                "Birth Details" -> selectedTabIndex = 9
                                                "Panchang" -> selectedTabIndex = 10
                                                "Ashtakvarga" -> selectedTabIndex = 11
                                                "Karakamsha" -> selectedTabIndex = 12
                                                "Swamsa" -> selectedTabIndex = 13
                                                "Transit" -> selectedTabIndex = 14
                                                "Shad Bala" -> selectedTabIndex = 15
                                                "Prastharashtakvarga" -> selectedTabIndex = 16
                                                "Bhav Madhya" -> selectedTabIndex = 17
                                                "Friendship" -> selectedTabIndex = 18
                                                "Person Details" -> {
                                                    selectedTabIndex = 19
                                                    showEditDialog = true
                                                }
                                                "Avkahada Chakra" -> selectedTabIndex = 20
                                                "Ghatak and Favourable" -> selectedTabIndex = 21
                                                "Download PDF" -> selectedTabIndex = 22
                                                "Reports" -> selectedTabIndex = 23
                                                "Ask Questions" -> selectedTabIndex = 24
                                                else -> activeSubView = item
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        1 -> {
                            // LAGNA D1 CHART
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliChartContainer(
                                        chartData = chart,
                                        initialDivision = ChartDivision.LAGNA_D1
                                    )
                                }
                            }
                        }
                        2 -> {
                            // NAVAMSHA D9 CHART
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliChartContainer(
                                        chartData = chart,
                                        initialDivision = ChartDivision.NAVAMSHA_D9
                                    )
                                }
                            }
                        }
                        3 -> {
                            // CLOUD
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = AstroSageOrangeLight)
                                                Text("AstroSage Cloud Backup", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text("Your profiles and kundlis are safely synced with AstroSage Cloud.", color = TextSecondary, fontSize = 13.sp)
                                            Button(
                                                onClick = { showCloudDialog = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
                                            ) {
                                                Text("Open Sync Settings")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        4 -> {
                            // MOON CHANDRA KUNDALI
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliChartContainer(
                                        chartData = chart,
                                        initialDivision = ChartDivision.MOON_CHANDRA
                                    )
                                }
                            }
                        }
                        5 -> {
                            // CHALIT CHART
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliChartContainer(
                                        chartData = chart,
                                        initialDivision = ChartDivision.CHALIT
                                    )
                                }
                            }
                        }
                        6 -> {
                            // PLANETS GRAHA STHITI
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    PlanetPositionTable(
                                        lagna = chart.lagna,
                                        planets = chart.planets
                                    )
                                }
                            }
                        }
                        7 -> {
                            // PLANETS-SUB (KP SUB LORD)
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    PlanetsSubLordView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        8 -> {
                            // CHALIT TABLE
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    ChalitTableView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        9 -> {
                            // BIRTH DETAILS
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    AvakhadaChakraView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        10 -> {
                            // PANCHANG
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    BirthPanchangCardView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        11 -> {
                            // ASHTAKVARGA
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    AshtakvargaView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        12 -> {
                            // KARAKAMSHA KUNDALI
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliChartContainer(
                                        chartData = chart,
                                        initialDivision = ChartDivision.KARAKAMSHA
                                    )
                                }
                            }
                        }
                        13 -> {
                            // SWAMSA KUNDALI
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliChartContainer(
                                        chartData = chart,
                                        initialDivision = ChartDivision.SWAMSA
                                    )
                                }
                            }
                        }
                        14 -> {
                            // TRANSIT (GOCHAR 2026)
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    TransitGocharView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        15 -> {
                            // SHAD BALA
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    ShadBalaView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        16 -> {
                            // PRASTHARASHTAKVARGA
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    AshtakvargaView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        17 -> {
                            // BHAV MADHYA
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    ChalitTableView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        18 -> {
                            // FRIENDSHIP
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    PlanetaryFriendshipView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        19 -> {
                            // PERSON DETAILS
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    AvakhadaChakraView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        20 -> {
                            // AVKAHADA CHAKRA
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    AvakhadaChakraView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        21 -> {
                            // GHATAK AND FAVOURABLE
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    GhatakFavourableView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        22 -> {
                            // DOWNLOAD PDF REPORT
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    KundaliPdfDownloadView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        23 -> {
                            // REPORTS
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    DoshaRemediesView(chart = chart, isHindi = isHindi)
                                }
                            }
                        }
                        24 -> {
                            // ASK QUESTIONS
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                item {
                                    AskAcharyaAiSection(
                                        messages = chatMessages,
                                        onSendMessage = { q -> viewModel.sendChatMessage(q) },
                                        isThinking = isChatThinking
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- CLOUD BACKUP DIALOG ---
    if (showCloudDialog) {
        CloudBackupDialog(
            onDismiss = { showCloudDialog = false },
            onBackupSuccess = {
                chartData?.let {
                    viewModel.saveNewProfile(it.birthDetails, "Cloud")
                    Toast.makeText(context, "Kundali backed up to AstroSage Cloud!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // --- ASTRO HELP DIALOG ---
    if (showHelpDialog) {
        AstroHelpDialog(onDismiss = { showHelpDialog = false })
    }

    // --- UPGRADE VIP DIALOG ---
    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Stars, contentDescription = "VIP", tint = Color(0xFFFFC107))
                    Text("AstroSage VIP Upgrade", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Unlock unlimited premium Vedic reports, 100+ page Brihat Kundali, daily transit notifications, and direct priority consultations with Acharya AI.", color = TextSecondary, fontSize = 13.sp)
                    Surface(color = CosmicNavyElevated, shape = RoundedCornerShape(8.dp)) {
                        Text("Active Status: All features fully unlocked for your session!", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showUpgradeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
                ) {
                    Text("Done", color = Color.White)
                }
            },
            containerColor = CosmicNavyCard
        )
    }

    // --- EDIT BIRTH DETAILS DIALOG ---
    if (showEditDialog && chartData != null) {
        val curr = chartData!!.birthDetails
        var editName by remember { mutableStateOf(curr.name) }
        var editDay by remember { mutableStateOf(curr.day.toString()) }
        var editMonth by remember { mutableStateOf(curr.month.toString()) }
        var editYear by remember { mutableStateOf(curr.year.toString()) }
        var editHour by remember { mutableStateOf(curr.hour.toString()) }
        var editMinute by remember { mutableStateOf(curr.minute.toString()) }
        var editPlace by remember { mutableStateOf(curr.placeName) }
        var editGender by remember { mutableStateOf(curr.gender) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = CosmicNavyDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "जन्म विवरण संपादित करें" else "Edit Birth Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        IconButton(onClick = { showEditDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (isHindi) "नाम" else "Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AstroSageOrangeLight,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editDay,
                            onValueChange = { editDay = it },
                            label = { Text("Day") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editMonth,
                            onValueChange = { editMonth = it },
                            label = { Text("Month") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editYear,
                            onValueChange = { editYear = it },
                            label = { Text("Year") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editHour,
                            onValueChange = { editHour = it },
                            label = { Text("Hour (0-23)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editMinute,
                            onValueChange = { editMinute = it },
                            label = { Text("Min (0-59)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editPlace,
                        onValueChange = { editPlace = it },
                        label = { Text(if (isHindi) "जन्म स्थान" else "Birth Place") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val updated = BirthDetails(
                                    name = editName.ifBlank { "User" },
                                    gender = editGender,
                                    year = editYear.toIntOrNull() ?: curr.year,
                                    month = editMonth.toIntOrNull() ?: curr.month,
                                    day = editDay.toIntOrNull() ?: curr.day,
                                    hour = editHour.toIntOrNull() ?: curr.hour,
                                    minute = editMinute.toIntOrNull() ?: curr.minute,
                                    placeName = editPlace.ifBlank { curr.placeName },
                                    latitude = curr.latitude,
                                    longitude = curr.longitude,
                                    timezoneOffsetHours = curr.timezoneOffsetHours
                                )
                                viewModel.calculateChart(updated)
                                viewModel.saveNewProfile(updated, "Self")
                                Toast.makeText(
                                    context,
                                    if (isHindi) "कुंडली सफलतापूर्वक अपडेट हुई!" else "Kundali updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showEditDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
                        ) {
                            Text(if (isHindi) "अपडेट करें" else "Update Kundali", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showEditDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isHindi) "रद्द करें" else "Cancel")
                        }
                    }
                }
            }
        }
    }
}

private fun shareKundaliSummary(context: Context, chart: KundaliChartData, isHindi: Boolean) {
    val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
    val text = if (isHindi) {
        """
        📜 AstroSage वैदिक कुंडली
        नाम: ${chart.birthDetails.name}
        जन्म: ${chart.birthDetails.formattedDateTime()} (${chart.birthDetails.placeName})
        लग्न: ${chart.lagna.sign.sanskritName} • चंद्र: ${moon?.sign?.sanskritName}
        नक्षत्र: ${moon?.nakshatra?.englishName} (चरण ${moon?.pada})
        महादशा: ${chart.currentMahadasha.sanskritName} - ${chart.currentAntardasha.sanskritName}
        """.trimIndent()
    } else {
        """
        📜 AstroSage Vedic Kundali
        Name: ${chart.birthDetails.name}
        Birth: ${chart.birthDetails.formattedDateTime()} (${chart.birthDetails.placeName})
        Lagna: ${chart.lagna.sign.sanskritName} • Moon: ${moon?.sign?.sanskritName}
        Nakshatra: ${moon?.nakshatra?.englishName} (Pada ${moon?.pada})
        Dasha: ${chart.currentMahadasha.sanskritName} - ${chart.currentAntardasha.sanskritName}
        """.trimIndent()
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "AstroSage Kundali - ${chart.birthDetails.name}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Kundali via"))
}
