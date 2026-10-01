package com.example.astrosage.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.AshtakootResult
import com.example.astrosage.core.astronomy.BirthDetails
import com.example.astrosage.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundaliMatchingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi by viewModel.isHindi.collectAsState()

    var boyName by remember { mutableStateOf("Aarav Sharma") }
    var boyDay by remember { mutableStateOf("15") }
    var boyMonth by remember { mutableStateOf("5") }
    var boyYear by remember { mutableStateOf("1995") }

    var girlName by remember { mutableStateOf("Priya Patel") }
    var girlDay by remember { mutableStateOf("22") }
    var girlMonth by remember { mutableStateOf("9") }
    var girlYear by remember { mutableStateOf("1997") }

    val matchingResult by viewModel.matchingResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "कुंडली मिलान (36 गुण)" else "Kundli Matching (36 Gunas)",
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = if (isHindi) "अष्टकूट गुण मिलान (36 अंक प्रणाली)" else "Ashtakoot Guna Milan (36 Points System)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi)
                        "वर्ण, वश्य, तारा, योनि, ग्रह मैत्री, गण, भकूट एवं नाड़ी मिलान का संपूर्ण वैदिक विश्लेषण।"
                    else
                        "Vedic compatibility analysis covering Varna, Vashya, Tara, Yoni, Maitri, Gana, Bhakoot, and Nadi.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            // Boy Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AstroSageOrange.copy(alpha = 0.4f))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AstroSageOrangeLight, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isHindi) "वर विवरण (Groom Details)" else "Groom (Var) Details",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AstroSageOrangeLight
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = boyName,
                            onValueChange = { boyName = it },
                            label = { Text(if (isHindi) "वर का नाम" else "Boy's Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("match_boy_name"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = boyDay, onValueChange = { boyDay = it }, label = { Text(if (isHindi) "दिन" else "Day") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = boyMonth, onValueChange = { boyMonth = it }, label = { Text(if (isHindi) "माह" else "Month") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = boyYear, onValueChange = { boyYear = it }, label = { Text(if (isHindi) "वर्ष" else "Year") }, modifier = Modifier.weight(1.2f))
                        }
                    }
                }
            }

            // Girl Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MysticPurple.copy(alpha = 0.4f))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MysticPurple, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isHindi) "कन्या विवरण (Bride Details)" else "Bride (Kanya) Details",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MysticPurple
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = girlName,
                            onValueChange = { girlName = it },
                            label = { Text(if (isHindi) "कन्या का नाम" else "Girl's Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("match_girl_name"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = girlDay, onValueChange = { girlDay = it }, label = { Text(if (isHindi) "दिन" else "Day") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = girlMonth, onValueChange = { girlMonth = it }, label = { Text(if (isHindi) "माह" else "Month") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = girlYear, onValueChange = { girlYear = it }, label = { Text(if (isHindi) "वर्ष" else "Year") }, modifier = Modifier.weight(1.2f))
                        }
                    }
                }
            }

            // Calculate Button
            item {
                Button(
                    onClick = {
                        val boy = BirthDetails(
                            name = boyName,
                            gender = "Male",
                            year = boyYear.toIntOrNull() ?: 1995,
                            month = boyMonth.toIntOrNull() ?: 5,
                            day = boyDay.toIntOrNull() ?: 15,
                            hour = 10,
                            minute = 30,
                            placeName = "New Delhi, India"
                        )
                        val girl = BirthDetails(
                            name = girlName,
                            gender = "Female",
                            year = girlYear.toIntOrNull() ?: 1997,
                            month = girlMonth.toIntOrNull() ?: 9,
                            day = girlDay.toIntOrNull() ?: 22,
                            hour = 14,
                            minute = 15,
                            placeName = "Mumbai, India"
                        )
                        viewModel.performMatching(boy, girl)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("calculate_matching_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "36 गुण मिलान करें" else "Calculate 36 Guna Milan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Results Section
            item {
                AnimatedVisibility(visible = matchingResult != null) {
                    matchingResult?.let { result ->
                        MatchingResultCard(result = result, isHindi = isHindi)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchingResultCard(
    result: AshtakootResult,
    isHindi: Boolean
) {
    val isAuspicious = result.totalScore >= 18.0

    val kootaItems = listOf(
        Triple(if (isHindi) "वर्ण (Varna)" else "Varna", result.varnaScore, result.varnaMax),
        Triple(if (isHindi) "वश्य (Vashya)" else "Vashya", result.vashyaScore, result.vashyaMax),
        Triple(if (isHindi) "तारा (Tara)" else "Tara", result.taraScore, result.taraMax),
        Triple(if (isHindi) "योनि (Yoni)" else "Yoni", result.yoniScore, result.yoniMax),
        Triple(if (isHindi) "ग्रह मैत्री (Graha Maitri)" else "Graha Maitri", result.maitriScore, result.maitriMax),
        Triple(if (isHindi) "गण (Gana)" else "Gana", result.ganaScore, result.ganaMax),
        Triple(if (isHindi) "भकूट (Bhakoot)" else "Bhakoot", result.bhakootScore, result.bhakootMax),
        Triple(if (isHindi) "नाड़ी (Nadi)" else "Nadi", result.nadiScore, result.nadiMax)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("matching_result_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(AstroSageOrange, CelestialGold))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Big Score Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "गुण मिलान परिणाम" else "Guna Milan Score",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${result.totalScore}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                        Text(
                            text = " / 36",
                            fontSize = 16.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                }

                Surface(
                    color = if (isAuspicious) EmeraldGreen.copy(alpha = 0.2f) else AuspiciousRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isAuspicious)
                            (if (isHindi) "उत्तम मिलान" else "Auspicious Match")
                        else
                            (if (isHindi) "मध्यम मिलान" else "Average Match"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAuspicious) EmeraldGreen else AuspiciousRed,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CosmicBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // 8 Kootas Table
            Text(
                text = if (isHindi) "अष्टकूट विवरण (Ashtakoot Breakdown)" else "Ashtakoot Breakdown",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGoldBright
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicBorder, RoundedCornerShape(8.dp))
            ) {
                kootaItems.forEachIndexed { index, (name, obtained, max) ->
                    val isEven = index % 2 == 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isEven) CosmicNavySurface else CosmicNavyCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(
                            text = "$obtained / $max",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (obtained == max) EmeraldGreen else CelestialGoldBright
                        )
                    }
                    if (index < kootaItems.size - 1) {
                        HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mangal Dosha Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicNavySurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🪐", fontSize = 14.sp)
                        Text(
                            text = if (isHindi) "मांगलिक दोष विश्लेषण (Manglik Dosha)" else "Manglik Dosha Analysis",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstroSageOrangeLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (isHindi) "वर" else "Boy"}: ${if (result.boyManglik) (if (isHindi) "मांगलिक" else "Manglik") else (if (isHindi) "अमांगलिक" else "Non-Manglik")} • ${if (isHindi) "कन्या" else "Girl"}: ${if (result.girlManglik) (if (isHindi) "मांगलिक" else "Manglik") else (if (isHindi) "अमांगलिक" else "Non-Manglik")}",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Verdict Recommendation
            Text(
                text = if (isHindi) "ज्योतिषीय निष्कर्ष (Verdict)" else "Astrological Verdict",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGoldBright
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.recommendation,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
