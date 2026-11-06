package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChannelCreatorDialog(
    isAiThinking: Boolean,
    onCreateChannel: (premise: String, genre: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var premiseInput by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Cyberpunk Sci-Fi") }

    val presetGenres = listOf(
        "Cyberpunk Sci-Fi",
        "80s Anime / Toon",
        "Galactic Cooking",
        "Detective Noir",
        "Exo Wildlife Nature",
        "Medieval Fantasy",
        "Reality Game Show",
        "Vaporwave MTV",
        "Haunted Late Night"
    )

    val samplePremises = listOf(
        "Time-traveling antique dealers trying to fix history paradoxes",
        "Intergalactic court where alien disputes are judged with comedy",
        "An 80s arcade hero trapped inside a holographic pinball world",
        "A vampire chef running an underground bistro for monsters"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("channel_creator_sheet"),
        color = Color(0xFF121422),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(2.dp, Color(0xFFFFEA00).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFEA00),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "AI CHANNEL SYNTHESIZER ✨",
                            color = Color(0xFFFFEA00),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Tune into a newly minted television broadcast",
                            color = Color(0xFF8D96B0),
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Genre Selector
            Text(
                text = "1. SELECT BROADCAST GENRE:",
                color = Color(0xFF00E5FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presetGenres.forEach { genre ->
                    val isSelected = selectedGenre == genre
                    Surface(
                        modifier = Modifier
                            .clickable { selectedGenre = genre }
                            .testTag("genre_chip_$genre"),
                        color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF1D2134),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSelected) Color.White else Color(0xFF323956))
                    ) {
                        Text(
                            text = genre,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Premise Input
            Text(
                text = "2. ENTER SHOW PREMISE OR PLOT IDEA:",
                color = Color(0xFFFFEA00),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            OutlinedTextField(
                value = premiseInput,
                onValueChange = { premiseInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_channel_premise_input"),
                placeholder = {
                    Text(
                        text = "e.g. Robot gladiators solving crosswords in an orbital arena...",
                        color = Color(0xFF676E86),
                        fontSize = 12.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFEA00),
                    unfocusedBorderColor = Color(0xFF373E5A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            // Inspiration presets
            Text(
                text = "TRY INSPIRATION PROMPTS:",
                color = Color(0xFFB0B5C9),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                samplePremises.forEach { sample ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { premiseInput = sample },
                        color = Color(0xFF1A1D2E),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF2C324E))
                    ) {
                        Text(
                            text = "💡 $sample",
                            color = Color(0xFFCFD5EA),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (isAiThinking) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Color(0xFFFFEA00),
                    trackColor = Color(0xFF2E3248)
                )
            }

            Button(
                onClick = {
                    if (premiseInput.isNotBlank()) {
                        onCreateChannel(premiseInput, selectedGenre)
                    }
                },
                enabled = premiseInput.isNotBlank() && !isAiThinking,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEA00)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_create_channel_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                    Text(
                        text = if (isAiThinking) "SYNTHESIZING TV FREQUENCY..." else "SYNTHESIZE & TUNE IN",
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
