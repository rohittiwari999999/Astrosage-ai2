package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.PanchangData
import com.example.ui.theme.*

@Composable
fun PanchangCard(
    panchang: PanchangData,
    modifier: Modifier = Modifier,
    onViewFullPanchang: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Aaj Ka Panchang (Today's Calendar)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                    Text(
                        text = "${panchang.dateString} • ${panchang.vaar}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = SacredSaffron.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = panchang.paksha,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SacredSaffron,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Key Panchang elements grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PanchangGridItem(title = "Tithi", value = panchang.tithi, modifier = Modifier.weight(1f))
                PanchangGridItem(title = "Nakshatra", value = panchang.nakshatra, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PanchangGridItem(title = "Yoga", value = panchang.yoga, modifier = Modifier.weight(1f))
                PanchangGridItem(title = "Karana", value = panchang.karana, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timings row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicNavyElevated, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TimingInfo(label = "Sunrise", value = panchang.sunrise, color = CelestialGoldBright)
                TimingInfo(label = "Sunset", value = panchang.sunset, color = SacredSaffron)
                TimingInfo(label = "Rahu Kaal", value = panchang.rahuKaal, color = AuspiciousRed)
            }
        }
    }
}

@Composable
private fun PanchangGridItem(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = CosmicNavySurface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}

@Composable
private fun TimingInfo(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = TextSecondary)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
