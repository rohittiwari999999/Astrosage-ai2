package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.astrosage.core.AstroReportItem
import com.example.astrosage.core.astronomy.KundaliChartData
import com.example.astrosage.core.astronomy.Planet
import com.example.ui.theme.*

@Composable
fun AstrologyReportDialog(
    report: AstroReportItem,
    chartData: KundaliChartData?,
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("report_dialog_${report.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavySurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(report.accentColorHex).copy(alpha = 0.6f))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(report.accentColorHex).copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, Color(report.accentColorHex), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(report.iconEmoji, fontSize = 22.sp)
                        }
                        Column {
                            Surface(
                                color = Color(report.accentColorHex).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) report.tagHi else report.tagEn,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(report.accentColorHex),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = if (isHindi) report.titleHi else report.titleEn,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(CosmicNavyElevated, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CosmicBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Report Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Context Pill
                    chartData?.let { chart ->
                        Surface(
                            color = CosmicNavyCard,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = chart.birthDetails.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CelestialGoldBright
                                    )
                                    val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
                                    Text(
                                        text = "${if (isHindi) "लग्न" else "Lagna"}: ${chart.lagna.sign.sanskritName} • ${if (isHindi) "राशि" else "Rashi"}: ${moon?.sign?.sanskritName ?: ""}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Surface(
                                    color = AstroSageOrange.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${chart.currentMahadasha.sanskritName} ${if (isHindi) "दशा" else "Dasha"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AstroSageOrangeLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Report Specific Content
                    val reportContent = generateReportText(report.id, chartData, isHindi)

                    reportContent.sections.forEach { (heading, body) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = heading,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CelestialGoldBright
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = body,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isHindi) "रिपोर्ट बंद करें" else "Close Report",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private data class ReportDetails(
    val sections: List<Pair<String, String>>
)

private fun generateReportText(reportId: String, chart: KundaliChartData?, isHindi: Boolean): ReportDetails {
    val lagnaName = chart?.lagna?.sign?.sanskritName ?: "मेष (Aries)"
    val moonName = chart?.planets?.firstOrNull { it.planet == Planet.MOON }?.sign?.sanskritName ?: "वृषभ (Taurus)"
    val dashaLord = chart?.currentMahadasha?.sanskritName ?: "बृहस्पति (Jupiter)"
    val antardashaLord = chart?.currentAntardasha?.sanskritName ?: "शुक्र (Venus)"

    return when (reportId) {
        "life_report" -> ReportDetails(
            listOf(
                (if (isHindi) "लग्न व व्यक्तित्व विश्लेषण" else "Ascendant & Personality") to
                        if (isHindi) "आपकी लग्न राशि $lagnaName है। आपका लग्न स्वामी बलवान होकर जीवन में स्थिरता, स्वाभिमान एवं नेतृत्व क्षमता प्रदान करता है। प्रथम भाव पर शुभ ग्रहों की दृष्टि से आपकी निर्णय क्षमता प्रखर रहेगी।"
                        else "Your Ascendant sign is $lagnaName. The Lagna lord is well-placed, bestowing resilience, integrity, and strong leadership vitality. Positive benefic aspects indicate high intuitive capability.",
                (if (isHindi) "प्रमुख जीवन योग" else "Major Life Yogas") to
                        if (isHindi) "कुंडली में केंद्र-त्रिकोण राजयोग व गजकेसरी योग का शुभ प्रभाव विद्यमान है। जीवन के 28वें से 42वें वर्ष के मध्य विशेष भाग्योदय व मान-प्रतिष्ठा के संकेत हैं।"
                        else "Benefic Kendra-Trikona Raj Yoga and Gaja Kesari influences are present. Auspicious elevation in career, authority, and prosperity is highlighted between your 28th and 42nd years.",
                (if (isHindi) "आयु, स्वास्थ्य व मार्गदर्शन" else "Vitality & Guidance") to
                        if (isHindi) "दीर्घायु योग निर्मित है। नियमित प्राणायाम, सूर्य नमस्कार एवं गुरुवार को पीले वस्त्र/चना दाल का दान विशेष रूप से कल्याणकारी रहेगा।"
                        else "Strong Longevity combinations are indicated. Regular meditation, Surya Namaskar, and charitable offerings on Thursdays will enhance divine grace."
            )
        )
        "sade_sati_report" -> ReportDetails(
            listOf(
                (if (isHindi) "शनि साढ़े साती स्थिति" else "Shani Sade Sati Status") to
                        if (isHindi) "आपकी चंद्र राशि $moonName के अनुसार शनि देव का प्रभाव संतुलित स्थिति में है। वर्तमान में शनि देव कर्मक्षेत्र एवं उत्तरदायित्वों में परिश्रम की मांग करते हैं।"
                        else "Based on your Moon sign $moonName, Saturn transits are currently balanced. Methodical work, perseverance, and ethical conduct bring solid lasting rewards.",
                (if (isHindi) "ढैय्या व चरण प्रभाव" else "Phases & Transit Impacts") to
                        if (isHindi) "शनि की साढ़े साती के प्रभाव से मानसिक अनुशासन और अनावश्यक खर्चों पर नियंत्रण आवश्यक है। भूमि, मकान या दीर्घकालिक निवेश में अनुकूल लाभ मिल सकता है।"
                        else "Discipline in financial outlays and emotional patience are advised during Saturn cycles. Favorable results in long-term assets and property may emerge with patience.",
                (if (isHindi) "अचूक शांति उपाय" else "Effective Remedies") to
                        if (isHindi) "प्रत्येक शनिवार को पीपल के वृक्ष के नीचे सरसों के तेल का दीपक प्रज्वलित करें एवं 'ॐ शं शनैश्चराय नमः' मंत्र का 108 बार जप करें। काले तिल का दान करें।"
                        else "Light a mustard oil lamp under a Peepal tree on Saturdays. Chant 'Om Sham Shanaishcharaya Namah' 108 times and donate black sesame seeds."
            )
        )
        "career_report" -> ReportDetails(
            listOf(
                (if (isHindi) "दशम भाव व आजीविका क्षेत्र" else "10th House & Suitable Professions") to
                        if (isHindi) "दशम भाव में शुभ ग्रहों की स्थिति से प्रशासनिक, तकनीकी, शिक्षा, परामर्श एवं प्रबंधन के क्षेत्रों में अत्यधिक सफलता का योग है।"
                        else "Strong planetary placements in the 10th house favor executive administration, technological consultancy, education, financial management, and leadership roles.",
                (if (isHindi) "पदोन्नति व व्यापारिक समय" else "Promotion & Business Timings") to
                        if (isHindi) "वर्तमान $dashaLord महादशा व $antardashaLord अंतर्दशा के दौरान पदोन्नति, वेतन वृद्धि तथा नए व्यावसायिक अनुबंधों के उत्तम अवसर निर्मित हो रहे हैं।"
                        else "Under the current $dashaLord Mahadasha and $antardashaLord Antardasha, promising avenues for salary revision, promotions, and lucrative ventures are supported.",
                (if (isHindi) "करियर उन्नति उपाय" else "Career Growth Remedy") to
                        if (isHindi) "प्रतिदिन प्रातःकाल तांबे के लोटे से सूर्य देव को अर्घ्य दें एवं 'ॐ घृणि सूर्याय नमः' का जप करें। कार्यस्थल पर उत्तर-पूर्व दिशा को स्वच्छ रखें।"
                        else "Offer Arghya (water) to Lord Surya at dawn in a copper vessel chanting 'Om Ghrini Suryaya Namah'. Keep the North-East corner of your workplace clutter-free."
            )
        )
        "marriage_report" -> ReportDetails(
            listOf(
                (if (isHindi) "सप्तम भाव व जीवनसाथी" else "7th House & Spouse Personality") to
                        if (isHindi) "सप्तमेश की शुभ स्थिति से जीवनसाथी सुसंस्कृत, बुद्धिमान, कर्तव्यनिष्ठ एवं सहायक प्रकृति का होगा। वैवाहिक जीवन में परस्पर विश्वास का माहौल रहेगा।"
                        else "A well-aspected 7th house indicates a cultured, intellectually mature, supportive, and loyal spouse. Mutual understanding and domestic peace are assured.",
                (if (isHindi) "मांगलिक दोष विश्लेषण" else "Manglik Dosha Assessment") to
                        if (isHindi) "मंगल की स्थिति सामान्य व प्रभावहीन है, अतः किसी गंभीर दोष का प्रभाव नहीं है। सौम्य दृष्टि से दांपत्य सुख में स्थिरता बनी रहेगी।"
                        else "Mars position is peaceful and not afflicted; hence no severe Kuja Dosha is present. Harmony and domestic security remain stable.",
                (if (isHindi) "सुखी दांपत्य के उपाय" else "Marital Bliss Remedy") to
                        if (isHindi) "शुक्ल पक्ष के शुक्रवार को मां लक्ष्मी की आराधना करें एवं सफेद पुष्प व मिश्री अर्पित करें। शालिग्राम या शिव-पार्वती पूजन शुभ रहेगा।"
                        else "Worship Goddess Lakshmi on Shukla Paksha Fridays with white flowers and rock sugar. Joint prayers to Shiva-Parvati foster eternal harmony."
            )
        )
        "wealth_report" -> ReportDetails(
            listOf(
                (if (isHindi) "धन व लाभ भाव विश्लेषण" else "2nd & 11th Houses Analysis") to
                        if (isHindi) "द्वितीय (धन) एवं एकादश (लाभ) भाव में शुभ योगों का निर्माण हो रहा है। पैतृक संपत्ति एवं स्वयं के पराक्रम से पर्याप्त संचित धन प्राप्त होगा।"
                        else "Auspicious alignments connecting the 2nd (accumulated wealth) and 11th (gains) houses indicate steady financial expansion and hereditary/self-earned assets.",
                (if (isHindi) "निवेश व बचत मार्गदर्शन" else "Investment Guidance") to
                        if (isHindi) "रियल एस्टेट, स्वर्ण एवं सुरक्षित म्यूचुअल फंड में निवेश विशेष फलदायी रहेगा। सट्टेबाजी या त्वरित लाभ वाली योजनाओं से बचें।"
                        else "Investments in real estate, gold, and balanced portfolios yield consistent compounding. Avoid speculative or impulsive intraday risks.",
                (if (isHindi) "लक्ष्मी प्राप्ति उपाय" else "Prosperity Remedy") to
                        if (isHindi) "श्री सूक्त का नित्य पाठ करें तथा कनकधारा स्तोत्र सुनें। ईशान कोण में जलपात्र स्थापित करें।"
                        else "Recite Sri Suktam or listen to Kanakadhara Stotram regularly. Place a small brass water vessel in the Ishanya (North-East) corner."
            )
        )
        else -> ReportDetails(
            listOf(
                (if (isHindi) "ग्रह दशा व गोचर सारांश" else "Planetary Transit Summary") to
                        if (isHindi) "आपकी जन्मपत्रिका में $lagnaName लग्न एवं $moonName राशि के आधार पर ग्रह गोचर अनुकूलता की ओर अग्रसर हैं। वर्तमान $dashaLord दशा आपको प्रगति के मार्ग पर अग्रसर रखेगी।"
                        else "Based on your $lagnaName Ascendant and $moonName Moon, planetary transits are supportive. The reigning $dashaLord Dasha keeps you motivated.",
                (if (isHindi) "शुभ अंक, रंग व दिशा" else "Auspicious Numerics & Colors") to
                        if (isHindi) "शुभ अंक: 3, 7, 9 | शुभ रंग: केसरिया, पीला, श्वेत | शुभ दिशा: उत्तर-पूर्व (ईशान) | शुभ वार: गुरुवार व रविवार।"
                        else "Lucky Numbers: 3, 7, 9 | Lucky Colors: Saffron, Gold, White | Lucky Direction: North-East | Favorable Days: Thursday & Sunday.",
                (if (isHindi) "दैनिक कल्याण मंत्र" else "Daily Wellness Mantra") to
                        if (isHindi) "'ॐ नमो भगवते वासुदेवाय' का नित्य 21 बार जप करें एवं तुलसी के पौधे में नियमित जल अर्पित करें।"
                        else "Chant 'Om Namo Bhagavate Vasudevaya' 21 times every morning and water a holy Tulsi plant."
            )
        )
    }
}
