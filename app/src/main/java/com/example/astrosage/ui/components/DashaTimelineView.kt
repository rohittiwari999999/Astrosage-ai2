package com.example.astrosage.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.DashaPeriod
import com.example.astrosage.core.astronomy.Planet
import com.example.ui.theme.*

@Composable
fun DashaTimelineView(
    dashaList: List<DashaPeriod>,
    currentMahadasha: Planet,
    currentAntardasha: Planet,
    modifier: Modifier = Modifier
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
                        text = "Vimshottari Dasha (120 Years)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                    Text(
                        text = "Planetary periods governing karmic milestones",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Current Active Badge
                Surface(
                    color = MysticPurple.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MysticPurple))
                ) {
                    Text(
                        text = "Active: ${currentMahadasha.shortCode}-${currentAntardasha.shortCode}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGoldBright,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            dashaList.forEach { period ->
                MahadashaItem(period)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MahadashaItem(period: DashaPeriod) {
    var expanded by remember { mutableStateOf(period.isCurrent) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (period.isCurrent) 1.5.dp else 1.dp,
                color = if (period.isCurrent) CelestialGold else CosmicBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        color = if (period.isCurrent) CosmicNavyElevated else CosmicNavySurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (period.isCurrent) CelestialGold else MysticPurple.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period.lord.symbol,
                            fontSize = 14.sp,
                            color = if (period.isCurrent) CosmicNavyDark else CelestialGoldBright
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${period.lord.sanskritName} Mahadasha",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (period.isCurrent) CelestialGold else TextPrimary
                            )
                            if (period.isCurrent) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(EmeraldGreen, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("CURRENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        Text(
                            text = "${period.startDate}  →  ${period.endDate}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand Antardashas",
                        tint = TextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = expanded && period.subPeriods.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 8.dp)
                ) {
                    Text(
                        text = "Antardashas (Sub-periods):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    period.subPeriods.forEach { sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                                .background(
                                    if (sub.isCurrent) MysticPurple.copy(alpha = 0.2f) else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${period.lord.shortCode} - ${sub.lord.sanskritName}",
                                    fontSize = 11.sp,
                                    fontWeight = if (sub.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sub.isCurrent) CelestialGoldBright else TextPrimary
                                )
                                if (sub.isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(Active Antardasha)", fontSize = 9.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "${sub.startDate} - ${sub.endDate}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
