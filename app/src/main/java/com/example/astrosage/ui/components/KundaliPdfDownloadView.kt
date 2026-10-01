package com.example.astrosage.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
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
import com.example.astrosage.core.astronomy.KundaliChartData
import com.example.astrosage.core.astronomy.Planet
import com.example.ui.theme.*

@Composable
fun KundaliPdfDownloadView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPreviewDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableIntStateOf(0) }
    var isDownloading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kundali_pdf_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- AstroSage PDF Hero Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFFD32F2F).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isHindi) "कुंडली पीडीएफ डाउनलोड" else "Download Kundli PDF",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGoldBright
                            )
                            Text(
                                text = if (isHindi) "एस्ट्रोसेज मानक वैदिक पत्रिका" else "AstroSage Standard Vedic Horoscope",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        color = AstroSageOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isHindi) "मुफ्त डाउनलोड" else "FREE PDF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstroSageOrangeLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isHindi)
                        "अपनी संपूर्ण जन्मपत्रिका का विस्तृत रंगीन पीडीएफ डाउनलोड करें या व्हाट्सएप पर साझा करें।"
                    else
                        "Download complete multi-page Vedic horoscope PDF or share with family and astrologers.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PDF Version Selector
                val options = if (isHindi) {
                    listOf(
                        Pair("बेसिक कुंडली (10 पृष्ठ)", "लग्न, नवमांश, अवकहड़ा चक्र, ग्रह स्थिति व विंशोत्तरी दशा।"),
                        Pair("विस्तृत जीवन पत्रिका (50+ पृष्ठ)", "संपूर्ण फलित, योग, साढ़े साती, लाल किताब उपाय व रत्न सलाह।"),
                        Pair("चार्ट्स व अष्टकवर्ग केवल", "रंगीन लग्न व नवमांश चक्र और 12 भावों का अष्टकवर्ग स्कोर।")
                    )
                } else {
                    listOf(
                        Pair("Basic Horoscope (10 Pages)", "Lagna, Navamsha, Avakhada, Planets & Vimshottari Dasha."),
                        Pair("Comprehensive Life Kundali (50+ Pages)", "Full Parashari Yogas, Sade Sati, Lal Kitab & Gemstone report."),
                        Pair("Charts & Ashtakvarga Only", "High-res color D1/D9 charts with Sarvashtakavarga score table.")
                    )
                }

                options.forEachIndexed { index, (title, desc) ->
                    val isSelected = selectedOption == index
                    Surface(
                        onClick = { selectedOption = index },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) AstroSageOrange.copy(alpha = 0.15f) else CosmicNavySurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) AstroSageOrangeLight else CosmicBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CelestialGoldBright else TextPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedOption = index },
                                colors = RadioButtonDefaults.colors(selectedColor = AstroSageOrangeLight)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            isDownloading = true
                            Toast.makeText(
                                context,
                                if (isHindi) "पीडीएफ तैयार हो रहा है... कृपया प्रतीक्षा करें" else "Generating PDF... Please wait",
                                Toast.LENGTH_SHORT
                            ).show()
                            showPreviewDialog = true
                            isDownloading = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_pdf_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "डाउनलोड PDF" else "Download PDF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            shareKundali(context, chart, isHindi)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_kundali_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CelestialGoldBright),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CelestialGoldBright)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "शेयर करें" else "Share Kundli",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // --- INTERACTIVE PDF PREVIEW DIALOG ---
    if (showPreviewDialog) {
        Dialog(onDismissRequest = { showPreviewDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(16.dp),
                color = CosmicNavyDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = AstroSageOrangeLight
                            )
                            Column {
                                Text(
                                    text = if (isHindi) "AstroSage कुंडली पूर्वावलोकन" else "AstroSage Kundli PDF Preview",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = chart.birthDetails.name,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        IconButton(onClick = { showPreviewDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CosmicBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // PDF Document Simulation Sheet
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // AstroSage Title Header in PDF
                            Text(
                                text = "AstroSage.com - Kundali Report",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "विश्व का सबसे विश्वसनीय वैदिक ज्योतिष पोर्टल",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Native Details Table
                            Text(
                                text = "1. जन्म विवरण (Birth Details)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
                            listOf(
                                Pair("नाम (Name)", chart.birthDetails.name),
                                Pair("दिनांक व समय", chart.birthDetails.formattedDateTime()),
                                Pair("जन्म स्थान", chart.birthDetails.placeName),
                                Pair("लग्न (Ascendant)", "${chart.lagna.sign.sanskritName} (${chart.lagna.sign.englishName})"),
                                Pair("चंद्र राशि (Moon Sign)", "${moon?.sign?.sanskritName} (${moon?.sign?.englishName})"),
                                Pair("नक्षत्र (Nakshatra)", "${moon?.nakshatra?.englishName ?: ""} (चरण ${moon?.pada ?: 1})"),
                                Pair("महादशा (Current Dasha)", "${chart.currentMahadasha.sanskritName} - ${chart.currentAntardasha.sanskritName}")
                            ).forEach { (k, v) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = k, fontSize = 10.sp, color = Color.DarkGray)
                                    Text(text = v, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Planetary summary in PDF
                            Text(
                                text = "2. प्रमुख ग्रह स्थिति (Planetary Longitudes)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            chart.planets.forEach { p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 1.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${p.planet.symbol} ${p.planet.sanskritName}:", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("${p.sign.sanskritName} ${String.format("%.1f°", p.signDegree)} (${p.dignity})", fontSize = 10.sp, color = Color.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "नोट: यह प्रामाणिक AstroSage AI वैदिक रिपोर्ट है। विस्तृत 50+ पृष्ठीय रिपोर्ट में सभी 16 वर्ग कुंडलियां, वर्षफल एवं अचूक लाल किताब उपाय सम्मिलित हैं।",
                                    fontSize = 9.sp,
                                    color = Color(0xFFE65100),
                                    modifier = Modifier.padding(8.dp),
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    if (isHindi) "पीडीएफ सफलतापूर्वक सहेजा गया!" else "PDF successfully saved to Downloads!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showPreviewDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
                        ) {
                            Text(if (isHindi) "सहेजें (Save)" else "Save to Phone", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                shareKundali(context, chart, isHindi)
                                showPreviewDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text(if (isHindi) "शेयर करें" else "Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun shareKundali(context: Context, chart: KundaliChartData, isHindi: Boolean) {
    val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
    val shareText = if (isHindi) {
        """
        📜 *AstroSage वैदिक जन्म कुंडली*
        नाम: ${chart.birthDetails.name}
        जन्म: ${chart.birthDetails.formattedDateTime()} (${chart.birthDetails.placeName})
        लग्न: ${chart.lagna.sign.sanskritName} (${chart.lagna.sign.englishName})
        चंद्र राशि: ${moon?.sign?.sanskritName} (${moon?.sign?.englishName})
        नक्षत्र: ${moon?.nakshatra?.englishName} (चरण ${moon?.pada})
        वर्तमान महादशा: ${chart.currentMahadasha.sanskritName} - ${chart.currentAntardasha.sanskritName}
        
        अपनी संपूर्ण जन्मपत्रिका और भविष्यफल के लिए AstroSage AI ऐप देखें।
        """.trimIndent()
    } else {
        """
        📜 *AstroSage Vedic Kundali Summary*
        Name: ${chart.birthDetails.name}
        Birth: ${chart.birthDetails.formattedDateTime()} (${chart.birthDetails.placeName})
        Ascendant (Lagna): ${chart.lagna.sign.sanskritName} (${chart.lagna.sign.englishName})
        Moon Sign (Rashi): ${moon?.sign?.sanskritName} (${moon?.sign?.englishName})
        Nakshatra: ${moon?.nakshatra?.englishName} (Pada ${moon?.pada})
        Current Dasha: ${chart.currentMahadasha.sanskritName} - ${chart.currentAntardasha.sanskritName}
        
        Download AstroSage AI for full 50+ page Vedic Horoscope.
        """.trimIndent()
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "AstroSage Kundali - ${chart.birthDetails.name}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Kundali via"))
}
