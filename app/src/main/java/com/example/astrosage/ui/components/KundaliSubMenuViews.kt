package com.example.astrosage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.ai.ChatMessage
import com.example.astrosage.core.astronomy.*
import com.example.ui.theme.*

/**
 * 2-Column Sub-Menu Grid matching the authentic AstroSage mobile layout with
 * an integrated AI Astro Assistant in the Basic window.
 */
@Composable
fun KundaliSubMenuDirectory(
    chartData: KundaliChartData,
    chatMessages: List<ChatMessage>,
    isChatThinking: Boolean,
    onSendChat: (String) -> Unit,
    onMenuItemClick: (String) -> Unit,
    onClearChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var chatText by remember { mutableStateOf("") }

    val quickChips = listOf(
        "💼 Career & Job Growth",
        "💍 Marriage & Spouse",
        "💰 Wealth & Dhan Yoga",
        "🛡️ Health & Shani Dosha",
        "💎 Lucky Gemstone",
        "🕉️ Current Mahadasha"
    )

    val leftColumn = listOf(
        "Lagna",
        "Cloud",
        "Chalit",
        "Planets-Sub",
        "Birth Details",
        "Ashtakvarga",
        "Swamsa",
        "Shad Bala",
        "Bhav Madhya",
        "Person Details",
        "Ghatak and Favourable",
        "Reports"
    )

    val rightColumn = listOf(
        "Navamsha",
        "Moon",
        "Planets",
        "Chalit Table",
        "Panchang",
        "Karakamsha",
        "Transit",
        "Prastharashtakvarga",
        "Friendship",
        "Avkahada Chakra",
        "Download PDF",
        "Ask Questions"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        // AstroSage Chat Bar with Pill Border and Yellow Circular Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .background(Color(0xFF141416), RoundedCornerShape(23.dp))
                    .border(1.dp, Color(0xFF333338), RoundedCornerShape(23.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (chatText.isEmpty()) {
                    Text(
                        text = "Ask AI Astro Assistant...",
                        color = Color(0xFF888890),
                        fontSize = 13.5.sp
                    )
                }
                TextField(
                    value = chatText,
                    onValueChange = { chatText = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AstroSageOrangeLight,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (chatText.isNotBlank()) {
                                onSendChat(chatText)
                                chatText = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("astrosage_chat_input")
                )
            }

            // Yellow Send Button
            IconButton(
                onClick = {
                    if (chatText.isNotBlank()) {
                        onSendChat(chatText)
                        chatText = ""
                    } else {
                        onMenuItemClick("Ask Questions")
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFFFC107), CircleShape)
                    .testTag("astrosage_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Quick Inquiries Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickChips.forEach { chip ->
                Surface(
                    color = Color(0xFF1E1E24),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.8.dp, Color(0xFF383840)),
                    modifier = Modifier.clickable { onSendChat(chip) }
                ) {
                    Text(
                        text = chip,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFD54F),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI ASTRO ASSISTANT INTERACTIVE CARD IN KUNDLI'S BASIC WINDOW
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .testTag("ai_astro_assistant_basic_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141418)),
            border = BorderStroke(1.dp, Color(0xFF2C2C36))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFFC107))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🕉️", fontSize = 15.sp)
                        }
                        Column {
                            Text(
                                text = "Acharya AI Astro Assistant",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "${chartData.birthDetails.name} • ${chartData.lagna.sign.englishName} Lagna • ${chartData.currentMahadasha.sanskritName} Dasha",
                                fontSize = 10.5.sp,
                                color = Color(0xFFAAAAAF)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Text(
                            text = "Online",
                            fontSize = 10.5.sp,
                            color = Color(0xFF81C784),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Messages Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E0E12), RoundedCornerShape(10.dp))
                        .border(0.8.dp, Color(0xFF222228), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val displayMessages = chatMessages.takeLast(4)
                    if (displayMessages.isEmpty()) {
                        Text(
                            text = "Namaste! I am your AI Astro Assistant. Ask me anything about your career, marriage, finances, health, or planetary remedies.",
                            fontSize = 12.sp,
                            color = Color(0xFFC0C0C8),
                            lineHeight = 17.sp
                        )
                    } else {
                        displayMessages.forEach { msg ->
                            val isUser = msg.sender == "user"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 300.dp)
                                        .background(
                                            if (isUser) Color(0xFF2E1C38) else Color(0xFF1B1D24),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            0.6.dp,
                                            if (isUser) Color(0xFF6A3D82) else Color(0xFF383842),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = if (isUser) "You" else "Acharya AI",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUser) Color(0xFFCE93D8) else Color(0xFFFFD54F)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = msg.text,
                                            fontSize = 11.5.sp,
                                            color = Color(0xFFE8E8EE),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isChatThinking) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFFFFC107),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Acharya AI is analyzing planetary positions & dasha...",
                                fontSize = 11.sp,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }

                // Assistant Card Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onClearChat,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = Color(0xFFAAAAAF),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Inquiries",
                            fontSize = 11.sp,
                            color = Color(0xFFAAAAAF)
                        )
                    }

                    TextButton(
                        onClick = { onMenuItemClick("Ask Questions") },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Full Consultation ↗",
                            fontSize = 11.sp,
                            color = Color(0xFFFFC107),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6 Golden Shortcut Icons matching screenshot: Dasha, Predictions, KP System, Shodashvarga, Lal Kitab, Varshphal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Row 1: Dasha, Predictions, KP System
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Top
            ) {
                // 1. Dasha
                GoldenShortcutItem(
                    title = "Dasha",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Brightness7,
                            contentDescription = "Dasha",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    onClick = { onMenuItemClick("Dasha") }
                )

                // 2. Predictions
                GoldenShortcutItem(
                    title = "Predictions",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Predictions",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    onClick = { onMenuItemClick("Predictions") }
                )

                // 3. KP System
                GoldenShortcutItem(
                    title = "KP System",
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .border(2.dp, Color(0xFFFFC107), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "KP",
                                color = Color(0xFFFFC107),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    onClick = { onMenuItemClick("KP System") }
                )
            }

            // Row 2: Shodashvarga, Lal Kitab, Varshphal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Top
            ) {
                // 4. Shodashvarga
                GoldenShortcutItem(
                    title = "Shodashvarga",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Window,
                            contentDescription = "Shodashvarga",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    onClick = { onMenuItemClick("Shodashvarga") }
                )

                // 5. Lal Kitab
                GoldenShortcutItem(
                    title = "Lal Kitab",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Lal Kitab",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    onClick = { onMenuItemClick("Lal Kitab") }
                )

                // 6. Varshphal
                GoldenShortcutItem(
                    title = "Varshphal",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Varshphal",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    onClick = { onMenuItemClick("Varshphal") }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Matrix with crisp grid lines
        val rowCount = leftColumn.size
        for (i in 0 until rowCount) {
            val leftItem = leftColumn[i]
            val rightItem = rightColumn[i]

            // Horizontal border line
            HorizontalDivider(
                color = Color(0xFF2A2A2E),
                thickness = 0.8.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                // Left Cell
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onMenuItemClick(leftItem) }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = leftItem,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                // Vertical Divider Line between the 2 columns
                VerticalDivider(
                    color = Color(0xFF2A2A2E),
                    thickness = 0.8.dp,
                    modifier = Modifier.fillMaxHeight()
                )

                // Right Cell
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onMenuItemClick(rightItem) }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rightItem,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom border
        HorizontalDivider(
            color = Color(0xFF2A2A2E),
            thickness = 0.8.dp
        )
    }
}

/**
 * Chalit Table & Bhav Madhya View.
 */
@Composable
fun ChalitTableView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val houses = remember(chart) {
        KundaliSubMenuCalculations.calculateChalitHouses(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "भाव चलित एवं भाव मध्य विवरण" else "Bhava Chalit & House Cusps (Bhav Madhya)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "प्रत्येक भाव का आरंभ, मध्य (कस्प) एवं समाप्ति बिंदु" else "Starting, middle cusp and ending degrees of all 12 houses",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AstroSageOrange, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("भाव", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
            Text("आरंभ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
            Text("भाव मध्य", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.4f), textAlign = TextAlign.Center)
            Text("समाप्त", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
            Text("स्वामी/उप", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
        }

        // Table Rows
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicNavyCard, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .border(1.dp, CosmicBorder, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
        ) {
            houses.forEachIndexed { index, h ->
                val bg = if (index % 2 == 0) CosmicNavyElevated.copy(alpha = 0.5f) else Color.Transparent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bg)
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("H${h.house}", color = CelestialGoldBright, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                    Text("${h.startSign.symbol} ${h.startDegree.toInt()}°", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
                    Text("${h.midSign.sanskritName} ${h.midDegree.toInt()}°", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.4f), textAlign = TextAlign.Center)
                    Text("${h.endSign.symbol} ${h.endDegree.toInt()}°", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
                    Text("${h.signLord.shortCode}/${h.subLord.shortCode}", color = AstroSageOrangeLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), textAlign = TextAlign.Center)
                }
                if (index < houses.size - 1) {
                    HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }
        }
    }
}

/**
 * Planets-Sub (KP Sub-Lord) View.
 */
@Composable
fun PlanetsSubLordView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val items = remember(chart) {
        KundaliSubMenuCalculations.calculatePlanetSubLordList(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "ग्रह-उपस्वामी (Planets Sub-Lord - KP)" else "Planets Sub-Lord (KP System)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "राशिश, नक्षत्रेश (Star Lord) एवं उपस्वामी (Sub Lord) का सूक्ष्म विभाजन" else "Micro-division of signs, Star lords and Sub lords for accurate timing",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AstroSageOrange, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(vertical = 10.dp, horizontal = 6.dp)
        ) {
            Text("ग्रह", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("अंश", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center)
            Text("राशि", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
            Text("राशिश", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("नक्षत्रेश", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
            Text("उपस्वामी", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicNavyCard, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .border(1.dp, CosmicBorder, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
        ) {
            items.forEachIndexed { idx, item ->
                val bg = if (idx % 2 == 0) CosmicNavyElevated.copy(alpha = 0.5f) else Color.Transparent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bg)
                        .padding(vertical = 9.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.planet.sanskritName,
                        color = if (item.planet == Planet.ASCENDANT) CelestialGoldBright else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(item.degreeStr, color = TextSecondary, fontSize = 10.sp, modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center)
                    Text("${item.sign.symbol} ${item.sign.sanskritName.take(3)}", color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                    Text(item.signLord.shortCode, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(item.starLord.sanskritName.take(4), color = Color(0xFF64B5F6), fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                    Surface(
                        color = AstroSageOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(
                            item.subLord.sanskritName.take(4),
                            color = AstroSageOrangeLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                if (idx < items.size - 1) {
                    HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }
        }
    }
}

/**
 * Planetary Friendship (Graha Maitri - Panchadha) View.
 */
@Composable
fun PlanetaryFriendshipView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val list = remember(chart) {
        KundaliSubMenuCalculations.calculateFriendshipMatrix(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "पंचधा ग्रह मैत्री चक्र (Planetary Friendship)" else "Panchadha Planetary Friendship",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "नैसर्गिक एवं तात्कालिक मैत्री को मिलाकर पंचधा (अधिमित्र, मित्र, सम, शत्रु, अधिशत्रु) विश्लेषण" else "Composite friendship combining natural & temporal relationships",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        list.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.planet.symbol} ${item.planet.sanskritName} (${item.planet.englishName})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldBright
                        )
                    }

                    HorizontalDivider(color = CosmicBorder.copy(alpha = 0.5f))

                    // Panchadha Friends
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("मित्र (Friends): ", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        Text(
                            if (item.panchadhaFriends.isNotEmpty()) item.panchadhaFriends.joinToString { it.sanskritName } else "कोई नहीं",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    // Panchadha Neutrals
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("सम (Neutral): ", fontSize = 11.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                        Text(
                            if (item.panchadhaNeutrals.isNotEmpty()) item.panchadhaNeutrals.joinToString { it.sanskritName } else "कोई नहीं",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    // Panchadha Enemies
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("शत्रु (Enemies): ", fontSize = 11.sp, color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                        Text(
                            if (item.panchadhaEnemies.isNotEmpty()) item.panchadhaEnemies.joinToString { it.sanskritName } else "कोई नहीं",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Shadbala (Planetary Strengths) View.
 */
@Composable
fun ShadBalaView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val list = remember(chart) {
        KundaliSubMenuCalculations.calculateShadbala(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "षड्बल (Shadbala - Planetary Strength)" else "Shadbala (Planetary Strength Analysis)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "स्थान, दिग, काल, चेष्टा, नैसर्गिक एवं दृग बल का संपूर्ण मापन (रूपा में)" else "Six-fold planetary strength indicators in Rupas and Virupas",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        list.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = AstroSageOrange.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("#${item.rank}", color = AstroSageOrangeLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "${item.planet.sanskritName} (${item.planet.englishName})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${String.format("%.2f", item.totalRupas)} Rupa (${item.strengthPercentage}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.strengthPercentage >= 100) Color(0xFF4CAF50) else Color(0xFFFFB74D)
                        )
                    }

                    // Strength Progress Bar
                    LinearProgressIndicator(
                        progress = { (item.strengthPercentage / 150f).coerceIn(0.1f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = if (item.strengthPercentage >= 100) Color(0xFF4CAF50) else AstroSageOrange,
                        trackColor = CosmicNavyDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("आवश्यक: ${item.requiredRupas} Rupa", fontSize = 10.sp, color = TextSecondary)
                        Text("कुल वीरुपा: ${item.totalVirupas.toInt()}", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

/**
 * Gochar / Transit View (2026).
 */
@Composable
fun TransitGocharView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val transits = remember(chart) {
        KundaliSubMenuCalculations.calculateTransits(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "वर्तमान गोचर स्थिति (Transit 2026)" else "Current Planetary Transit (Gochar 2026)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "जन्म लग्न एवं चंद्र राशि से वर्तमान ग्रहों के गोचर का फल" else "Planetary transit influences relative to your natal Moon & Ascendant",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        transits.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.planet.symbol, fontSize = 16.sp, color = AstroSageOrangeLight)
                            Text(
                                text = "${item.planet.sanskritName} → ${item.transitSign.sanskritName} (${item.transitSign.englishName})",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = if (item.isFavourable) Color(0xFF2E7D32).copy(alpha = 0.25f) else Color(0xFFC62828).copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (item.isFavourable) (if (isHindi) "शुभ गोचर" else "Auspicious") else (if (isHindi) "अशुभ गोचर" else "Inauspicious"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isFavourable) Color(0xFF81C784) else Color(0xFFE57373),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "चंद्र से भाव: ${item.houseFromMoon} • लग्न से भाव: ${item.houseFromLagna}",
                        fontSize = 11.sp,
                        color = CelestialGoldBright
                    )

                    Text(
                        text = if (isHindi) item.effectsHi else item.effects,
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Janma Panchang Card View.
 */
@Composable
fun BirthPanchangCardView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val panchang = remember(chart) {
        KundaliSubMenuCalculations.calculateBirthPanchang(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "जन्म पंचांग (Birth Panchang)" else "Birth Panchang Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "जन्म समय का पंचांग: तिथि, वार, नक्षत्र, योग, करण एवं सूर्योदय" else "Vedic five elements of time at the exact moment of birth",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        val rows = listOf(
            Pair(if (isHindi) "तिथि (Tithi)" else "Tithi", if (isHindi) panchang.tithiHi else panchang.tithi),
            Pair(if (isHindi) "वार (Day)" else "Day / Vaar", if (isHindi) panchang.vaarHi else panchang.vaar),
            Pair(if (isHindi) "नक्षत्र (Nakshatra)" else "Nakshatra", "${panchang.nakshatra} (चरण ${panchang.pada})"),
            Pair(if (isHindi) "योग (Yoga)" else "Yoga", if (isHindi) panchang.yogaHi else panchang.yoga),
            Pair(if (isHindi) "करण (Karana)" else "Karana", if (isHindi) panchang.karanaHi else panchang.karana),
            Pair(if (isHindi) "सूर्य राशि (Sun Sign)" else "Sun Sign", if (isHindi) panchang.sunSignHi else panchang.sunSign),
            Pair(if (isHindi) "चंद्र राशि (Moon Sign)" else "Moon Sign", if (isHindi) panchang.moonSignHi else panchang.moonSign),
            Pair(if (isHindi) "सूर्योदय (Sunrise)" else "Sunrise", panchang.sunrise),
            Pair(if (isHindi) "सूर्यास्त (Sunset)" else "Sunset", panchang.sunset),
            Pair(if (isHindi) "अयनांश (Ayanamsha)" else "Ayanamsha", panchang.ayanamshaStr)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                rows.forEachIndexed { idx, (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, color = TextSecondary, fontSize = 12.sp)
                        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (idx < rows.size - 1) {
                        HorizontalDivider(color = CosmicBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

/**
 * Cloud Backup & Sync Dialog.
 */
@Composable
fun CloudBackupDialog(
    onDismiss: () -> Unit,
    onBackupSuccess: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.CloudSync, contentDescription = "Cloud", tint = AstroSageOrangeLight)
                Text("Cloud Backup & Sync", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "AstroSage Cloud secures your saved Kundalis and allows effortless sync across devices.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Surface(
                    color = CosmicNavyElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Local Kundalis:", color = TextSecondary, fontSize = 12.sp)
                            Text("Backed Up in Room DB", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Cloud Status:", color = TextSecondary, fontSize = 12.sp)
                            Text("Synced with Google Account", color = CelestialGoldBright, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onBackupSuccess()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
            ) {
                Text("Sync Now", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = CosmicNavyCard
    )
}

/**
 * Help & AstroSage Guide Dialog.
 */
@Composable
fun AstroHelpDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "Help", tint = AstroSageOrangeLight)
                Text("Kundali Navigation Guide", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "• BASIC: Tap any tile in the 2-column menu directory to jump straight to Lagna, Navamsha, Moon, Planets, Chalit, Panchang, Ashtakvarga, and Reports.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    "• TABS: Swipe or tap top horizontal tabs (BASIC, LAGNA, NAVAMSHA, MOON, CHALIT, PLANETS, etc.) for direct chart analysis.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    "• CHAT: Type questions into the Chat bar to consult Acharya AI directly about career, health, wealth, or marriage.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AstroSageOrange)
            ) {
                Text("Got It", color = Color.White)
            }
        },
        containerColor = CosmicNavyCard
    )
}

@Composable
fun GoldenShortcutItem(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(96.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Lal Kitab View: Teva Type, Pakka Ghar, Sleeping Houses, and 6 Upay
 */
@Composable
fun LalKitabView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val details = remember(chart) {
        KundaliSubMenuCalculations.calculateLalKitab(chart)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isHindi) "लाल किताब कुण्डली विश्लेषण" else "Lal Kitab Kundali Analysis",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "कुण्डली प्रकार: ${details.tevaTypeHi} • किस्मत जगाने वाला ग्रह: ${details.kismatJaganewalaGrah.sanskritName}" else "Chart Type: ${details.tevaType} • Fate Awakener: ${details.kismatJaganewalaGrah.englishName}",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Pakka Ghar & Sleeping Houses
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isHindi) "पक्का घर एवं सोया ग्रह/घर" else "Pakka Ghar & Sleeping Houses (Soya Ghar)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                details.houses.forEachIndexed { idx, h ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "भाव ${h.house} (पक्का: ${h.pakkaGharLord.sanskritName})",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = if (h.isSleeping) "सोया हुआ (Sleeping)" else "जागृत (${h.planets.joinToString { it.planet.shortCode }})",
                            fontSize = 11.5.sp,
                            color = if (h.isSleeping) Color(0xFF9E9E9E) else Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (idx < details.houses.size - 1) {
                        HorizontalDivider(color = CosmicBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }

        // Lal Kitab Remedies
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isHindi) "लाल किताब अचूक उपाय (Remedies)" else "Lal Kitab Authentic Remedies",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )

                val remedyList = if (isHindi) details.remediesHi else details.remedies
                remedyList.forEachIndexed { i, remedy ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            color = AstroSageOrange.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${i + 1}", color = AstroSageOrangeLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = remedy,
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Varshphal (Annual Solar Return - Tajika) View
 */
@Composable
fun VarshphalView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val varshphal = remember(chart) {
        KundaliSubMenuCalculations.calculateVarshphal(chart, 2026)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "ताजिक वर्षफल 2026" else "Tajika Varshphal (2026)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGoldBright
                    )
                    Surface(
                        color = AstroSageOrange.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "वर्ष आयु: ${varshphal.age} वर्ष",
                            color = AstroSageOrangeLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = if (isHindi) "मुंथा: ${varshphal.munthaSign.sanskritName} (भाव ${varshphal.munthaHouse}) • वर्षेश: ${varshphal.varshesh.sanskritName}" else "Muntha: ${varshphal.munthaSign.englishName} (House ${varshphal.munthaHouse}) • Year Lord (Varshesh): ${varshphal.varshesh.englishName}",
                    fontSize = 12.5.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Annual Forecast Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isHindi) "वर्षफल फलकथन (Annual Forecast)" else "Annual Forecast & Tajika Yogas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) varshphal.yearForecastHi else varshphal.yearForecast,
                    fontSize = 12.5.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // Varshphal Chart Preview
        KundaliChartContainer(
            chartData = chart,
            initialDivision = ChartDivision.LAGNA_D1
        )
    }
}

/**
 * 16 Divisional Charts (Shodashvarga) View
 */
@Composable
fun ShodashvargaView(
    chart: KundaliChartData,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val list = remember { KundaliSubMenuCalculations.getShodashvargaList() }
    var selectedDivision by remember { mutableStateOf("D9") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isHindi) "षोडशवर्ग (16 Divisional Charts)" else "Shodashvarga (16 Divisional Charts)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGoldBright
                )
                Text(
                    text = if (isHindi) "ऋषि पराशर प्रणीत संपूर्ण 16 वर्ग चक्रों का सूक्ष्म फलित" else "Detailed micro-divisional vargas as prescribed by Sage Parashara",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Active Chart
        KundaliChartContainer(
            chartData = chart,
            initialDivision = if (selectedDivision == "D1") ChartDivision.LAGNA_D1 else ChartDivision.NAVAMSHA_D9
        )

        // 16 Varga Selection Grid
        Text(
            text = if (isHindi) "वर्ग चक्र चुनें" else "Select Divisional Chart",
            color = CelestialGoldBright,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )

        list.forEach { item ->
            val isSelected = selectedDivision == item.divisionCode
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedDivision = item.divisionCode },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AstroSageOrange.copy(alpha = 0.2f) else CosmicNavyElevated
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) AstroSageOrangeLight else CosmicBorder)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = AstroSageOrange.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(item.divisionCode, color = AstroSageOrangeLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Text(if (isHindi) item.titleHi else item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(if (isHindi) item.significanceHi else item.significance, color = TextSecondary, fontSize = 10.5.sp)
                    }
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (isSelected) AstroSageOrangeLight else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
