package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.astronomy.PanchangData
import com.example.astrosage.ui.theme.AuspiciousGreen
import com.example.astrosage.ui.theme.GoldAccent
import com.example.astrosage.ui.theme.InauspiciousRed
import com.example.astrosage.ui.theme.SaffronPrimary

@Composable
fun PanchangCard(
    panchang: PanchangData,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("panchang_summary_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Live status banner
            val isWarning = panchang.currentActivePeriod.contains("राहुकाल")
            val isAuspicious = panchang.currentActivePeriod.contains("अभिजित") || panchang.currentActivePeriod.contains("शुभ") || panchang.currentActivePeriod.contains("अमृत")
            val bannerBg = if (isWarning) InauspiciousRed.copy(alpha = 0.15f)
            else if (isAuspicious) AuspiciousGreen.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            val bannerColor = if (isWarning) InauspiciousRed else if (isAuspicious) AuspiciousGreen else MaterialTheme.colorScheme.primary

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("live_period_banner"),
                shape = RoundedCornerShape(8.dp),
                color = bannerBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "स्थिति",
                        tint = bannerColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = panchang.currentActivePeriod,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = bannerColor
                        )
                    )
                }
            }

            // Header: Date, City & Day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${panchang.dayOfWeekHindi}, ${panchang.dateString}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${panchang.samvatsaraName} संवत्सर | वि.सं. ${panchang.vikramSamvat}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SaffronPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = panchang.cityName.substringBefore(" ("),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SaffronPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Celestial Times: Sunrise, Sunset, Moonrise, Moonset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TimeItem(
                    icon = Icons.Default.WbSunny,
                    label = "सूर्योदय",
                    value = panchang.sunrise,
                    color = GoldAccent
                )
                TimeItem(
                    icon = Icons.Default.WbTwilight,
                    label = "सूर्यास्त",
                    value = panchang.sunset,
                    color = SaffronPrimary
                )
                TimeItem(
                    icon = Icons.Default.NightsStay,
                    label = "चन्द्रोदय",
                    value = panchang.moonrise,
                    color = Color(0xFF64B5F6)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pancha-Anga Rows: Tithi & Nakshatra
            PanchangElementRow(
                title = "तिथि",
                value = "${panchang.tithi} (${panchang.tithiPaksha})",
                subText = "समाप्ति: ${panchang.tithiEndTime} तक"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )

            PanchangElementRow(
                title = "नक्षत्र",
                value = "${panchang.nakshatra} (चरण ${panchang.nakshatraPada})",
                subText = "समाप्ति: ${panchang.nakshatraEndTime} तक"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )

            // Yoga & Karana
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "योग",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = panchang.yoga.substringBefore(" ("),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "तक ${panchang.yogaEndTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "करण",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = panchang.karana.substringBefore(" ("),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "तक ${panchang.karanaEndTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Muhurats quick preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "शुभ अभिजित:",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuspiciousGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = panchang.abhijitMuhurat,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "अशुभ राहुकाल:",
                        style = MaterialTheme.typography.labelSmall,
                        color = InauspiciousRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = panchang.rahuKaal,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun PanchangElementRow(
    title: String,
    value: String,
    subText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        Text(
            text = subText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
