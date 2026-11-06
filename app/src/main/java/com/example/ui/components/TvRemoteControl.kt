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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveSheet

@Composable
fun TvRemoteBar(
    isPoweredOn: Boolean,
    onTogglePower: () -> Unit,
    onChannelUp: () -> Unit,
    onChannelDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onOpenSheet: (ActiveSheet) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tv_remote_quick_bar"),
        color = Color(0xFF161823),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, Color(0xFF2E3246))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Quick Action Row (Power, CH, VOL, Full Remote)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red Power Button with tactile look
                Surface(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable { onTogglePower() }
                        .testTag("remote_power_button"),
                    color = if (isPoweredOn) Color(0xFFD50000) else Color(0xFF4A1010),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, if (isPoweredOn) Color(0xFFFF5252) else Color(0xFF882020))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Channel Rocker
                Row(
                    modifier = Modifier
                        .background(Color(0xFF222536), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF3B405A), RoundedCornerShape(12.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onChannelDown,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("remote_ch_down")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Channel Down",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "CH",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = onChannelUp,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("remote_ch_up")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Channel Up",
                            tint = Color.White
                        )
                    }
                }

                // Volume Rocker
                Row(
                    modifier = Modifier
                        .background(Color(0xFF222536), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF3B405A), RoundedCornerShape(12.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onVolumeDown,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("remote_vol_down")
                    ) {
                        Text(
                            text = "-",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "VOL",
                        color = Color(0xFFFFEA00),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = onVolumeUp,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("remote_vol_up")
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Remote Keypad Drawer Toggle
                Surface(
                    modifier = Modifier
                        .clickable { onOpenSheet(ActiveSheet.REMOTE) }
                        .testTag("remote_keypad_sheet_btn"),
                    color = Color(0xFF2F344C),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF4C5376))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Remote Keypad",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "PAD",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Bottom Functional Quick Buttons (Director, Call-In, Guide, FX)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RemoteFeatureChip(
                    label = "DIRECT",
                    icon = Icons.Default.Movie,
                    color = Color(0xFFE040FB),
                    onClick = { onOpenSheet(ActiveSheet.DIRECTOR) },
                    modifier = Modifier.weight(1f),
                    testTag = "remote_direct_scene_chip"
                )
                RemoteFeatureChip(
                    label = "CALL IN",
                    icon = Icons.Default.Call,
                    color = Color(0xFF00E5FF),
                    onClick = { onOpenSheet(ActiveSheet.CALL_IN) },
                    modifier = Modifier.weight(1f),
                    testTag = "remote_call_in_chip"
                )
                RemoteFeatureChip(
                    label = "GUIDE",
                    icon = Icons.Default.List,
                    color = Color(0xFFFF9100),
                    onClick = { onOpenSheet(ActiveSheet.GUIDE) },
                    modifier = Modifier.weight(1f),
                    testTag = "remote_guide_chip"
                )
                RemoteFeatureChip(
                    label = "CRT FX",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF39FF14),
                    onClick = { onOpenSheet(ActiveSheet.SETTINGS) },
                    modifier = Modifier.weight(1f),
                    testTag = "remote_settings_chip"
                )
            }
        }
    }
}

@Composable
private fun RemoteFeatureChip(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        color = Color(0xFF1E2132),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Full expanded authentic retro remote control with complete number pad and controls!
 */
@Composable
fun FullTvRemoteSheet(
    isPoweredOn: Boolean,
    volume: Int,
    isMuted: Boolean,
    isCaptionsVisible: Boolean,
    ttsEnabled: Boolean,
    keypadInput: String,
    onTogglePower: () -> Unit,
    onKeypadDigit: (Int) -> Unit,
    onKeypadSubmit: () -> Unit,
    onKeypadClear: () -> Unit,
    onChannelUp: () -> Unit,
    onChannelDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleCaptions: () -> Unit,
    onToggleTts: () -> Unit,
    onTriggerRoast: () -> Unit,
    onOpenDirector: () -> Unit,
    onOpenCallIn: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenNewChannel: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("full_remote_control_modal"),
        color = Color(0xFF12141F),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        border = BorderStroke(2.dp, Color(0xFF2C3149))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF3D4464), RoundedCornerShape(2.dp))
            )

            // Header & Model label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INFRA-RED COMMANDER",
                    color = Color(0xFF8C95B2),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )

                // Power button
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onTogglePower() }
                        .testTag("full_remote_power_btn"),
                    color = if (isPoweredOn) Color(0xFFD50000) else Color(0xFF3E1212),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, if (isPoweredOn) Color(0xFFFF5252) else Color(0xFF6B1E1E))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Numeric LCD Display for Keypad tuning
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0A0C13),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF333850))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DIGITAL TUNER",
                        color = Color(0xFF636A84),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (keypadInput.isEmpty()) "CH: --" else "CH: $keypadInput",
                        color = Color(0xFF39FF14),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Number Keypad (1 to 9, 0, ENT, DEL)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeypadNumberButton(text = "1", onClick = { onKeypadDigit(1) }, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "2", onClick = { onKeypadDigit(2) }, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "3", onClick = { onKeypadDigit(3) }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeypadNumberButton(text = "4", onClick = { onKeypadDigit(4) }, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "5", onClick = { onKeypadDigit(5) }, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "6", onClick = { onKeypadDigit(6) }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeypadNumberButton(text = "7", onClick = { onKeypadDigit(7) }, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "8", onClick = { onKeypadDigit(8) }, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "9", onClick = { onKeypadDigit(9) }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeypadActionButton(text = "CLR", color = Color(0xFFFF5252), onClick = onKeypadClear, modifier = Modifier.weight(1f))
                    KeypadNumberButton(text = "0", onClick = { onKeypadDigit(0) }, modifier = Modifier.weight(1f))
                    KeypadActionButton(text = "ENT", color = Color(0xFF00E5FF), onClick = onKeypadSubmit, modifier = Modifier.weight(1f))
                }
            }

            // TV Feature Launch Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FullRemoteSpecialButton(
                    label = "DIRECTOR",
                    icon = Icons.Default.Movie,
                    color = Color(0xFFE040FB),
                    onClick = onOpenDirector,
                    modifier = Modifier.weight(1f)
                )
                FullRemoteSpecialButton(
                    label = "CALL IN",
                    icon = Icons.Default.Call,
                    color = Color(0xFF00E5FF),
                    onClick = onOpenCallIn,
                    modifier = Modifier.weight(1f)
                )
                FullRemoteSpecialButton(
                    label = "EPG GUIDE",
                    icon = Icons.Default.List,
                    color = Color(0xFFFF9100),
                    onClick = onOpenGuide,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FullRemoteSpecialButton(
                    label = "MST3K ROAST",
                    icon = Icons.Default.SportsEsports,
                    color = Color(0xFFFF4081),
                    onClick = onTriggerRoast,
                    modifier = Modifier.weight(1f)
                )
                FullRemoteSpecialButton(
                    label = "+ CHANNEL",
                    icon = Icons.Default.Add,
                    color = Color(0xFFFFEA00),
                    onClick = onOpenNewChannel,
                    modifier = Modifier.weight(1f)
                )
                FullRemoteSpecialButton(
                    label = "CRT FILTERS",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF39FF14),
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            // Audio & Caption Toggles Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleCaptions,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isCaptionsVisible) Color(0xFFFFEB3B) else Color.Gray
                    ),
                    border = BorderStroke(1.dp, if (isCaptionsVisible) Color(0xFFFFEB3B) else Color(0xFF363B52))
                ) {
                    Icon(imageVector = Icons.Default.ClosedCaption, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isCaptionsVisible) "CC: ON" else "CC: OFF", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                OutlinedButton(
                    onClick = onToggleMute,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isMuted) Color(0xFFFF5252) else Color(0xFF39FF14)
                    ),
                    border = BorderStroke(1.dp, if (isMuted) Color(0xFFFF5252) else Color(0xFF363B52))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isMuted) "MUTED" else "VOL $volume", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                OutlinedButton(
                    onClick = onToggleTts,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (ttsEnabled) Color(0xFF00E5FF) else Color.Gray
                    ),
                    border = BorderStroke(1.dp, if (ttsEnabled) Color(0xFF00E5FF) else Color(0xFF363B52))
                ) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (ttsEnabled) "VOICE: ON" else "VOICE: OFF", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun KeypadNumberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
            .testTag("keypad_btn_$text"),
        color = Color(0xFF222638),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF383E5C))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun KeypadActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
            .testTag("keypad_action_$text"),
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun FullRemoteSpecialButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .clickable { onClick() },
        color = Color(0xFF1B1F2E),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
