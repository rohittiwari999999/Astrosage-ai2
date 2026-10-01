package com.example.astrosage.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.KundaliChartData
import com.example.astrosage.core.astronomy.PlanetaryPosition
import com.example.astrosage.core.astronomy.ZodiacSign
import com.example.ui.theme.*

enum class ChartStyle {
    NORTH_INDIAN,
    SOUTH_INDIAN,
    EAST_INDIAN
}

enum class ChartDivision {
    LAGNA_D1,
    NAVAMSHA_D9,
    MOON_CHANDRA,
    CHALIT,
    KARAKAMSHA,
    SWAMSA
}

@Composable
fun KundaliChartContainer(
    chartData: KundaliChartData,
    modifier: Modifier = Modifier,
    initialDivision: ChartDivision = ChartDivision.LAGNA_D1,
    onHouseClick: (Int, ZodiacSign, List<PlanetaryPosition>) -> Unit = { _, _, _ -> }
) {
    var selectedStyle by remember { mutableStateOf(ChartStyle.NORTH_INDIAN) }
    var selectedDivision by remember(initialDivision) { mutableStateOf(initialDivision) }

    val karakamshaData = remember(chartData) {
        com.example.astrosage.core.astronomy.KundaliSubMenuCalculations.calculateKarakamsha(chartData)
    }
    val swamsaData = remember(chartData) {
        com.example.astrosage.core.astronomy.KundaliSubMenuCalculations.calculateSwamsa(chartData)
    }

    val housePlanets = when (selectedDivision) {
        ChartDivision.LAGNA_D1 -> chartData.houses
        ChartDivision.NAVAMSHA_D9 -> chartData.navamshaHouses
        ChartDivision.MOON_CHANDRA -> chartData.moonChartHouses
        ChartDivision.CHALIT -> chartData.houses
        ChartDivision.KARAKAMSHA -> karakamshaData.second
        ChartDivision.SWAMSA -> swamsaData.second
    }

    val lagnaSign = when (selectedDivision) {
        ChartDivision.LAGNA_D1 -> chartData.lagna.sign
        ChartDivision.NAVAMSHA_D9 -> chartData.lagna.navamshaSign
        ChartDivision.MOON_CHANDRA -> chartData.planets.first { it.planet == com.example.astrosage.core.astronomy.Planet.MOON }.sign
        ChartDivision.CHALIT -> chartData.lagna.sign
        ChartDivision.KARAKAMSHA -> karakamshaData.first
        ChartDivision.SWAMSA -> swamsaData.first
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kundali_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chart Style selector tabs (North, South, East)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicNavyElevated, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    Pair(ChartStyle.NORTH_INDIAN, "North Indian"),
                    Pair(ChartStyle.SOUTH_INDIAN, "South Indian"),
                    Pair(ChartStyle.EAST_INDIAN, "East Indian")
                ).forEach { (style, label) ->
                    val isSelected = selectedStyle == style
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) CelestialGold else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedStyle = style }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CosmicNavyDark else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chart Division Selector (D1 Lagna, D9 Navamsha, Chandra, Chalit, Karakamsha, Swamsa)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(ChartDivision.LAGNA_D1, "D1 (Lagna)"),
                    Pair(ChartDivision.NAVAMSHA_D9, "D9 (Navamsha)"),
                    Pair(ChartDivision.MOON_CHANDRA, "Chandra Kundali"),
                    Pair(ChartDivision.CHALIT, "Chalit"),
                    Pair(ChartDivision.KARAKAMSHA, "Karakamsha"),
                    Pair(ChartDivision.SWAMSA, "Swamsa")
                ).forEach { (div, label) ->
                    val isSelected = selectedDivision == div
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDivision = div },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MysticPurple.copy(alpha = 0.3f),
                            selectedLabelColor = CelestialGoldBright
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Render chosen chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(CosmicNavyDark, RoundedCornerShape(12.dp))
                    .border(1.5.dp, CosmicBorder, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (selectedStyle) {
                    ChartStyle.NORTH_INDIAN -> NorthIndianChart(
                        lagnaSign = lagnaSign,
                        housePlanets = housePlanets,
                        onHouseClick = onHouseClick
                    )
                    ChartStyle.SOUTH_INDIAN -> SouthIndianChart(
                        lagnaSign = lagnaSign,
                        housePlanets = housePlanets,
                        onHouseClick = onHouseClick
                    )
                    ChartStyle.EAST_INDIAN -> EastIndianChart(
                        lagnaSign = lagnaSign,
                        housePlanets = housePlanets,
                        onHouseClick = onHouseClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap on any house to view planetary degrees & dignities",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun NorthIndianChart(
    lagnaSign: ZodiacSign,
    housePlanets: Map<Int, List<PlanetaryPosition>>,
    onHouseClick: (Int, ZodiacSign, List<PlanetaryPosition>) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val strokeColor = CelestialGold
        val strokeWidth = 2.dp.toPx()
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        val planetPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFD56B")
            textSize = 11.sp.toPx()
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        val signPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#94A3B8")
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        // 1. Outer boundary
        drawRect(color = strokeColor, style = Stroke(width = strokeWidth))

        // 2. Diagonals
        drawLine(strokeColor, Offset(0f, 0f), Offset(w, h), strokeWidth)
        drawLine(strokeColor, Offset(0f, h), Offset(w, 0f), strokeWidth)

        // 3. Inner diamond
        drawLine(strokeColor, Offset(w / 2f, 0f), Offset(w, h / 2f), strokeWidth)
        drawLine(strokeColor, Offset(w, h / 2f), Offset(w / 2f, h), strokeWidth)
        drawLine(strokeColor, Offset(w / 2f, h), Offset(0f, h / 2f), strokeWidth)
        drawLine(strokeColor, Offset(0f, h / 2f), Offset(w / 2f, 0f), strokeWidth)

        // House centers for North Indian chart
        val houseCenters = listOf(
            Offset(w * 0.50f, h * 0.25f), // House 1 (Top diamond)
            Offset(w * 0.25f, h * 0.12f), // House 2 (Top left triangle)
            Offset(w * 0.12f, h * 0.25f), // House 3 (Left top triangle)
            Offset(w * 0.25f, h * 0.50f), // House 4 (Left diamond)
            Offset(w * 0.12f, h * 0.75f), // House 5 (Left bottom triangle)
            Offset(w * 0.25f, h * 0.88f), // House 6 (Bottom left triangle)
            Offset(w * 0.50f, h * 0.75f), // House 7 (Bottom diamond)
            Offset(w * 0.75f, h * 0.88f), // House 8 (Bottom right triangle)
            Offset(w * 0.88f, h * 0.75f), // House 9 (Right bottom triangle)
            Offset(w * 0.75f, h * 0.50f), // House 10 (Right diamond)
            Offset(w * 0.88f, h * 0.25f), // House 11 (Right top triangle)
            Offset(w * 0.75f, h * 0.12f)  // House 12 (Top right triangle)
        )

        for (hIndex in 1..12) {
            val center = houseCenters[hIndex - 1]
            val signNum = ((lagnaSign.number - 1 + (hIndex - 1)) % 12) + 1
            val planets = housePlanets[hIndex] ?: emptyList()

            // Draw sign number
            drawContext.canvas.nativeCanvas.drawText(
                signNum.toString(),
                center.x,
                center.y - 12f,
                signPaint
            )

            // Draw planets in this house
            if (planets.isNotEmpty()) {
                val planetText = planets.joinToString(" ") { p ->
                    val retro = if (p.isRetrograde) "(R)" else ""
                    "${p.planet.shortCode}$retro"
                }
                drawContext.canvas.nativeCanvas.drawText(
                    planetText,
                    center.x,
                    center.y + 10f,
                    planetPaint
                )
            }
        }
    }
}

@Composable
fun SouthIndianChart(
    lagnaSign: ZodiacSign,
    housePlanets: Map<Int, List<PlanetaryPosition>>,
    onHouseClick: (Int, ZodiacSign, List<PlanetaryPosition>) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellW = w / 4f
        val cellH = h / 4f
        val strokeColor = MysticPurple
        val strokeWidth = 2.dp.toPx()

        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFD56B")
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#94A3B8")
            textSize = 9.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        // Outer grid lines
        for (i in 0..4) {
            drawLine(strokeColor, Offset(i * cellW, 0f), Offset(i * cellW, h), strokeWidth)
            drawLine(strokeColor, Offset(0f, i * cellH), Offset(w, i * cellH), strokeWidth)
        }

        // Blank out central 2x2
        drawRect(
            color = CosmicNavyDark,
            topLeft = Offset(cellW, cellH),
            size = androidx.compose.ui.geometry.Size(cellW * 2f, cellH * 2f)
        )

        // South Indian fixed sign boxes:
        // Pisces(0,0), Aries(1,0), Taurus(2,0), Gemini(3,0)
        // Cancer(3,1), Leo(3,2), Virgo(3,3)
        // Libra(2,3), Scorpio(1,3), Sagittarius(0,3)
        // Capricorn(0,2), Aquarius(0,1)
        val fixedSignBoxes = listOf(
            Pair(ZodiacSign.PISCES, Offset(cellW * 0.5f, cellH * 0.5f)),
            Pair(ZodiacSign.ARIES, Offset(cellW * 1.5f, cellH * 0.5f)),
            Pair(ZodiacSign.TAURUS, Offset(cellW * 2.5f, cellH * 0.5f)),
            Pair(ZodiacSign.GEMINI, Offset(cellW * 3.5f, cellH * 0.5f)),
            Pair(ZodiacSign.CANCER, Offset(cellW * 3.5f, cellH * 1.5f)),
            Pair(ZodiacSign.LEO, Offset(cellW * 3.5f, cellH * 2.5f)),
            Pair(ZodiacSign.VIRGO, Offset(cellW * 3.5f, cellH * 3.5f)),
            Pair(ZodiacSign.LIBRA, Offset(cellW * 2.5f, cellH * 3.5f)),
            Pair(ZodiacSign.SCORPIO, Offset(cellW * 1.5f, cellH * 3.5f)),
            Pair(ZodiacSign.SAGITTARIUS, Offset(cellW * 0.5f, cellH * 3.5f)),
            Pair(ZodiacSign.CAPRICORN, Offset(cellW * 0.5f, cellH * 2.5f)),
            Pair(ZodiacSign.AQUARIUS, Offset(cellW * 0.5f, cellH * 1.5f))
        )

        for ((sign, center) in fixedSignBoxes) {
            val houseNum = ((sign.number - lagnaSign.number + 12) % 12) + 1
            val planets = housePlanets[houseNum] ?: emptyList()
            val isLagna = sign == lagnaSign

            drawContext.canvas.nativeCanvas.drawText(
                "${sign.symbol} ${sign.englishName.take(3)}",
                center.x,
                center.y - 12f,
                labelPaint
            )

            if (isLagna) {
                drawContext.canvas.nativeCanvas.drawText(
                    "Asc (Lagna)",
                    center.x,
                    center.y + 2f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#F5BA42")
                        textSize = 10.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                )
            }

            if (planets.isNotEmpty()) {
                val pStr = planets.joinToString(" ") { it.planet.shortCode }
                drawContext.canvas.nativeCanvas.drawText(
                    pStr,
                    center.x,
                    center.y + 16f,
                    textPaint
                )
            }
        }
    }
}

@Composable
fun EastIndianChart(
    lagnaSign: ZodiacSign,
    housePlanets: Map<Int, List<PlanetaryPosition>>,
    onHouseClick: (Int, ZodiacSign, List<PlanetaryPosition>) -> Unit
) {
    // East Indian chart format
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val strokeColor = SacredSaffron
        val strokeWidth = 2.dp.toPx()

        drawRect(strokeColor, style = Stroke(strokeWidth))
        // Crossed triangles
        drawLine(strokeColor, Offset(0f, 0f), Offset(w, h), strokeWidth)
        drawLine(strokeColor, Offset(0f, h), Offset(w, 0f), strokeWidth)
        drawLine(strokeColor, Offset(w / 2f, 0f), Offset(w / 2f, h), strokeWidth)
        drawLine(strokeColor, Offset(0f, h / 2f), Offset(w, h / 2f), strokeWidth)

        val planetPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFD56B")
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }

        drawContext.canvas.nativeCanvas.drawText(
            "Lagna: ${lagnaSign.sanskritName}",
            w / 2f,
            h / 2f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}
