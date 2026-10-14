package com.example.astrosage.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.astrosage.core.astronomy.PlanetPosition
import com.example.astrosage.ui.MainViewModel
import com.example.astrosage.ui.components.PanchangCard
import com.example.astrosage.ui.theme.GoldAccent
import com.example.astrosage.ui.theme.InauspiciousRed
import com.example.astrosage.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundaliDetailScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val panchang by viewModel.panchangState.collectAsState()
    val planets by viewModel.planetsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("कुंडली एवं ग्रह स्थिति (Horoscope)") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "पीछे जाएं")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Birth / Gochar Panchang Card
            item {
                PanchangCard(panchang = panchang)
            }

            // North Indian Kundali Chart Visual
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kundali_chart_card"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "लग्न चक्र / गोचर कुंडली (Lagna Chart)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NorthIndianChart(modifier = Modifier.size(280.dp))
                    }
                }
            }

            // Planetary Positions Table
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("planets_table_card"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ग्रह स्थिति (Planetary Degrees & Nakshatras)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Text("ग्रह", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("राशि", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("अंश", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("नक्षत्र (चरण)", modifier = Modifier.weight(1.8f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }

                        planets.forEach { p ->
                            PlanetRow(planet = p)
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanetRow(planet: PlanetPosition) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${planet.planet.hindiName} ${planet.planet.symbol}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            if (planet.isRetrograde) {
                Text(" (व)", color = InauspiciousRed, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = planet.signNameHi,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = planet.formattedDegree,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${planet.nakshatraNameHi} (${planet.nakshatraPada})",
            modifier = Modifier.weight(1.8f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun NorthIndianChart(modifier: Modifier = Modifier) {
    val strokeColor = SaffronPrimary
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer square
        drawRect(color = strokeColor, topLeft = Offset.Zero, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

        // Diagonals
        drawLine(color = strokeColor, start = Offset(0f, 0f), end = Offset(w, h), strokeWidth = 2f)
        drawLine(color = strokeColor, start = Offset(w, 0f), end = Offset(0f, h), strokeWidth = 2f)

        // Inner Diamond connecting midpoints
        val topMid = Offset(w / 2, 0f)
        val rightMid = Offset(w, h / 2)
        val bottomMid = Offset(w / 2, h)
        val leftMid = Offset(0f, h / 2)

        drawLine(color = strokeColor, start = topMid, end = rightMid, strokeWidth = 2f)
        drawLine(color = strokeColor, start = rightMid, end = bottomMid, strokeWidth = 2f)
        drawLine(color = strokeColor, start = bottomMid, end = leftMid, strokeWidth = 2f)
        drawLine(color = strokeColor, start = leftMid, end = topMid, strokeWidth = 2f)
    }
}
