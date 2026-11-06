package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TvChannel

@Composable
fun CallInDialog(
    channel: TvChannel,
    isCallerStreaming: Boolean,
    callerStreamText: String,
    onSendQuestion: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var questionInput by remember { mutableStateOf("") }

    val quickQuestions = when {
        channel.genre.contains("Cooking", true) -> listOf(
            "What can I cook with leftover pickles and moonlight?",
            "How do you stop a quantum soufflé from exploding?",
            "Can I substitute antimatter butter?"
        )
        channel.genre.contains("News", true) -> listOf(
            "Is the moon transit shuttle safe to fly tonight?",
            "Who really caused the orbital refinery shutdown?",
            "What's tomorrow's Mars weather forecast?"
        )
        channel.genre.contains("Noir", true) || channel.genre.contains("Crime", true) -> listOf(
            "Jack, who really framed the mayor?",
            "Where should I hide if shadowy hitmen follow me?",
            "Is the dame telling the truth?"
        )
        else -> listOf(
            "What is your advice for home viewers?",
            "Can you give a shoutout to the home viewers?",
            "What happens next on your broadcast?"
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("call_in_hotline_sheet"),
        color = Color(0xFF111422),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(2.dp, Color(0xFF00E5FF).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF00E5FF).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color(0xFF00E5FF))
                    }
                    Column {
                        Text(
                            text = "LIVE STUDIO HOTLINE 📞",
                            color = Color(0xFF00E5FF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = Color(0xFF39FF14),
                                modifier = Modifier.size(8.dp)
                            )
                            Text(
                                text = "ON AIR WITH ${channel.hostName.uppercase()}",
                                color = Color(0xFF39FF14),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Host Live Response Screen Area
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E30)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF2C3250))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = channel.hostEmoji, fontSize = 20.sp)
                            Text(
                                text = "${channel.hostName} (${channel.hostTitle})",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isCallerStreaming) {
                            Text(
                                text = "SPEAKING LIVE...",
                                color = Color(0xFF00E5FF),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (callerStreamText.isNotBlank()) {
                        Text(
                            text = "\"$callerStreamText\"",
                            color = Color(0xFF00E5FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            text = "You are currently on the studio hotline. Ask ${channel.hostName} any question, grill them on their show, or request advice!",
                            color = Color(0xFF868FA6),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    if (isCallerStreaming) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = Color(0xFF00E5FF),
                            trackColor = Color(0xFF242A42)
                        )
                    }
                }
            }

            // Quick Topic Chips
            Text(
                text = "SUGGESTED CALL-IN TOPICS:",
                color = Color(0xFFFFEA00),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                quickQuestions.forEach { q ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isCallerStreaming) {
                                questionInput = q
                                onSendQuestion(q)
                            },
                        color = Color(0xFF1E2236),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF353C5E))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎙️", fontSize = 14.sp)
                            Text(
                                text = q,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Custom Question Input Field
            OutlinedTextField(
                value = questionInput,
                onValueChange = { questionInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("call_in_input_field"),
                placeholder = {
                    Text(
                        text = "Speak into the hotline mic...",
                        color = Color(0xFF676E86),
                        fontSize = 12.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF353C5E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    if (questionInput.isNotBlank()) {
                        onSendQuestion(questionInput)
                    }
                },
                enabled = questionInput.isNotBlank() && !isCallerStreaming,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("call_in_dial_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.Black)
                    Text(
                        text = if (isCallerStreaming) "BROADCASTING CALL LIVE..." else "DIAL IN & ASK ON-AIR",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
