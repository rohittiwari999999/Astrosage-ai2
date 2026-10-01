package com.example.astrosage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrosage.core.ai.ChatMessage
import com.example.ui.theme.*

@Composable
fun AskAcharyaAiSection(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    isThinking: Boolean,
    modifier: Modifier = Modifier
) {
    var queryText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "When will I see major career growth?",
        "What does my 7th house indicate about marriage?",
        "Which gemstone suits my Lagna best?",
        "Are there any major doshas in my chart?",
        "Financial outlook for this year?"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ask_acharya_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CosmicBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Brush.linearGradient(listOf(SacredSaffron, CelestialGold)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🕉️", fontSize = 18.sp)
                }

                Column {
                    Text(
                        text = "Ask Acharya AI (Vedic Consultation)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                    Text(
                        text = "Real-time answers grounded in your exact planetary coordinates",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Question Chips
            Text("Suggested Inquiries:", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickQuestions.take(2).forEach { q ->
                    Surface(
                        color = CosmicNavyElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { onSendMessage(q) }
                    ) {
                        Text(
                            text = q,
                            fontSize = 11.sp,
                            color = CelestialGoldBright,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Messages Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 340.dp)
                    .background(CosmicNavyDark, RoundedCornerShape(12.dp))
                    .border(1.dp, CosmicBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ask Acharya AI anything about your horoscope, career prospects, love & marriage, remedies, or planetary transits.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    messages.forEach { msg ->
                        val isUser = msg.sender == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .background(
                                        if (isUser) MysticPurple.copy(alpha = 0.35f) else CosmicNavyElevated,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        if (isUser) MysticPurple else CosmicBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    if (isThinking) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CelestialGold, strokeWidth = 2.dp)
                            Text("Acharya AI is contemplating your planetary positions...", fontSize = 11.sp, color = CelestialGoldBright)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Query Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = { Text("Ask a question about your chart...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CosmicNavySurface,
                        unfocusedContainerColor = CosmicNavySurface,
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = CosmicBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )

                IconButton(
                    onClick = {
                        if (queryText.isNotBlank() && !isThinking) {
                            val q = queryText.trim()
                            queryText = ""
                            onSendMessage(q)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(CelestialGold, CircleShape)
                        .testTag("ai_chat_send_button"),
                    enabled = queryText.isNotBlank() && !isThinking
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Question",
                        tint = CosmicNavyDark
                    )
                }
            }
        }
    }
}
