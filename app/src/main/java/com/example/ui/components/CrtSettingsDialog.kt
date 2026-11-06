package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrtSettings

@Composable
fun CrtSettingsDialog(
    crtSettings: CrtSettings,
    onUpdateSettings: ((CrtSettings) -> CrtSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("crt_settings_sheet"),
        color = Color(0xFF111320),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(2.dp, Color(0xFF39FF14).copy(alpha = 0.6f))
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
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color(0xFF39FF14),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "CRT CHASSIS & FX 📺",
                            color = Color(0xFF39FF14),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Cathode Ray Tube calibration & acoustics",
                            color = Color(0xFF8890AA),
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Setting Cards
            CrtSettingToggleRow(
                title = "Retro CRT Scanlines",
                description = "Simulates authentic analog phosphor cathode beam scanlines",
                checked = crtSettings.scanlines,
                onCheckedChange = { value -> onUpdateSettings { it.copy(scanlines = value) } },
                testTag = "toggle_scanlines"
            )

            CrtSettingToggleRow(
                title = "Tube Curvature & Vignette",
                description = "Spherical glass tube edges and corner light falloff",
                checked = crtSettings.curvature,
                onCheckedChange = { value -> onUpdateSettings { it.copy(curvature = value) } },
                testTag = "toggle_curvature"
            )

            CrtSettingToggleRow(
                title = "Phosphor Amber Monochrome Mode",
                description = "Classic 80s amber computer monitor and teletext glow",
                checked = crtSettings.retroAmberMode,
                onCheckedChange = { value -> onUpdateSettings { it.copy(retroAmberMode = value) } },
                testTag = "toggle_amber_mode"
            )

            CrtSettingToggleRow(
                title = "4:3 Classic Aspect Ratio",
                description = "Square vintage CRT format (disable for 16:9 widescreen)",
                checked = crtSettings.aspectRatio4x3,
                onCheckedChange = { value -> onUpdateSettings { it.copy(aspectRatio4x3 = value) } },
                testTag = "toggle_aspect_ratio"
            )

            CrtSettingToggleRow(
                title = "Screen Flicker & Refresh Hum",
                description = "Gentle 60Hz analog tube flicker simulation",
                checked = crtSettings.flicker,
                onCheckedChange = { value -> onUpdateSettings { it.copy(flicker = value) } },
                testTag = "toggle_flicker"
            )

            CrtSettingToggleRow(
                title = "Hardware Sound Effects",
                description = "Analog white-noise static burst, remote click, and power pop",
                checked = crtSettings.soundEffects,
                onCheckedChange = { value -> onUpdateSettings { it.copy(soundEffects = value) } },
                testTag = "toggle_sound_effects"
            )

            CrtSettingToggleRow(
                title = "Live Text-To-Speech Narration",
                description = "TV hosts speak their lines out loud via speech synthesizer",
                checked = crtSettings.ttsVoice,
                onCheckedChange = { value -> onUpdateSettings { it.copy(ttsVoice = value) } },
                testTag = "toggle_tts"
            )
        }
    }
}

@Composable
private fun CrtSettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181B2B)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF2C314C))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = Color(0xFF8B92AB),
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF39FF14),
                    checkedTrackColor = Color(0xFF1B5E20),
                    uncheckedThumbColor = Color(0xFF6B7280),
                    uncheckedTrackColor = Color(0xFF222638)
                )
            )
        }
    }
}
