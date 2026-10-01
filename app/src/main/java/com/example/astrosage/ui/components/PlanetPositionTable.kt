package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.PlanetaryPosition
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun PlanetPositionTable(
    lagna: PlanetaryPosition,
    planets: List<PlanetaryPosition>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Planetary Positions (Graha Sthiti)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGold
            )
            Text(
                text = "Lahiri Ayanamsha (Chitra Paksha) Nirayana Coordinates",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicNavyElevated, RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Planet", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.4f))
                Text("Sign & Deg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.8f))
                Text("House", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(0.8f))
                Text("Nakshatra", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.8f))
                Text("Dignity", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.4f))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Lagna row
            PlanetRow(lagna, isLagna = true)

            // 9 Planets
            planets.forEach { planetPos ->
                HorizontalDivider(color = CosmicBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                PlanetRow(planetPos, isLagna = false)
            }
        }
    }
}

@Composable
private fun PlanetRow(pos: PlanetaryPosition, isLagna: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Planet Name & Glyph
        Row(
            modifier = Modifier.weight(1.4f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = pos.planet.symbol,
                fontSize = 14.sp,
                color = if (isLagna) CelestialGold else MysticPurple
            )
            Column {
                Text(
                    text = pos.planet.sanskritName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (pos.isRetrograde && !isLagna) {
                    Text("Vakri (R)", fontSize = 9.sp, color = AuspiciousRed)
                }
            }
        }

        // Sign & Degree
        Column(modifier = Modifier.weight(1.8f)) {
            Text(
                text = "${pos.sign.sanskritName} ${pos.sign.symbol}",
                fontSize = 12.sp,
                color = TextPrimary
            )
            Text(
                text = String.format(Locale.US, "%.2f°", pos.signDegree),
                fontSize = 10.sp,
                color = TextSecondary
            )
        }

        // House
        Text(
            text = "H${pos.house}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CelestialGoldBright,
            modifier = Modifier.weight(0.8f)
        )

        // Nakshatra & Pada
        Column(modifier = Modifier.weight(1.8f)) {
            Text(
                text = pos.nakshatra.englishName,
                fontSize = 11.sp,
                color = TextPrimary
            )
            Text(
                text = "Pada ${pos.pada}",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }

        // Dignity Badge
        Box(
            modifier = Modifier
                .weight(1.4f)
                .background(
                    when {
                        pos.dignity.contains("Exalted") -> EmeraldGreen.copy(alpha = 0.2f)
                        pos.dignity.contains("Own") || pos.dignity.contains("Moolatrikona") -> CelestialGold.copy(alpha = 0.2f)
                        pos.dignity.contains("Debilitated") -> AuspiciousRed.copy(alpha = 0.2f)
                        else -> CosmicNavyElevated
                    },
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    pos.dignity.contains("Exalted") -> "Exalted"
                    pos.dignity.contains("Debilitated") -> "Neecha"
                    pos.dignity.contains("Own") -> "Own"
                    pos.dignity.contains("Moolatrikona") -> "Moola"
                    pos.dignity.contains("Friendly") -> "Mitra"
                    pos.dignity.contains("Self") -> "Lagna"
                    else -> "Sama"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    pos.dignity.contains("Exalted") -> EmeraldGreen
                    pos.dignity.contains("Own") || pos.dignity.contains("Moolatrikona") -> CelestialGoldBright
                    pos.dignity.contains("Debilitated") -> AuspiciousRed
                    else -> TextSecondary
                }
            )
        }
    }
}
