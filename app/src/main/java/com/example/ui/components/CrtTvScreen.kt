package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Commentary
import com.example.model.CrtSettings
import com.example.model.PollOption
import com.example.model.TvChannel
import com.example.model.TvScene
import kotlin.random.Random

@Composable
fun CrtTvScreen(
    channel: TvChannel,
    isPoweredOn: Boolean,
    isWarmingUp: Boolean,
    isChangingChannel: Boolean,
    crtSettings: CrtSettings,
    volume: Int,
    isMuted: Boolean,
    isCaptionsVisible: Boolean,
    latestCommentary: Commentary?,
    isCommentaryVisible: Boolean,
    userVotedOptionId: Int?,
    onVoteOption: (Int) -> Unit,
    onDirectSceneClick: () -> Unit,
    onCallInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Aspect ratio container (4:3 retro CRT or 16:9 widescreen)
    val targetAspect = if (crtSettings.aspectRatio4x3) 4f / 3f else 16f / 9f

    // Retro wooden / graphite TV cabinet chassis
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .testTag("crt_tv_chassis"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E202B) // Retro matte dark chassis
        ),
        border = BorderStroke(3.dp, Color(0xFF323646))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Top TV Bezel with speaker grille, logo, and power LED
            TvTopBezel(isPoweredOn = isPoweredOn)

            Spacer(modifier = Modifier.height(8.dp))

            // Main CRT Glass Screen Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(targetAspect)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .border(2.dp, Color(0xFF0F1017), RoundedCornerShape(20.dp))
                    .testTag("crt_glass_screen"),
                contentAlignment = Alignment.Center
            ) {
                if (!isPoweredOn) {
                    // TV is powered off: Dark curved CRT glass with phosphor reflection
                    PoweredOffScreen()
                } else if (isWarmingUp || isChangingChannel) {
                    // Realistic static fuzz and snow
                    StaticFuzzScreen(isWarmingUp = isWarmingUp)
                } else {
                    // Active TV Channel Broadcast
                    ActiveBroadcastContent(
                        channel = channel,
                        isCaptionsVisible = isCaptionsVisible,
                        userVotedOptionId = userVotedOptionId,
                        onVoteOption = onVoteOption,
                        onDirectSceneClick = onDirectSceneClick,
                        onCallInClick = onCallInClick
                    )

                    // On-Screen Display (OSD) overlay
                    OsdOverlay(
                        channel = channel,
                        volume = volume,
                        isMuted = isMuted,
                        isCaptionsVisible = isCaptionsVisible
                    )

                    // Mystery Science MST3K Commentators
                    if (isCommentaryVisible && latestCommentary != null) {
                        Mst3kCommentatorsOverlay(
                            commentary = latestCommentary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 28.dp)
                        )
                    }
                }

                // CRT Post-Processing FX (Scanlines, Phosphor glow, Curvature vignette)
                if (isPoweredOn && !isChangingChannel) {
                    CrtEffectsOverlay(crtSettings = crtSettings)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom TV Controls panel (Hardware buttons & knobs)
            TvBottomHardwarePanel(
                channelNumber = channel.channelNumber,
                isPoweredOn = isPoweredOn
            )
        }
    }
}

@Composable
private fun TvTopBezel(isPoweredOn: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speaker Grille lines
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(3.dp)
                        .background(Color(0xFF13141B), RoundedCornerShape(2.dp))
                )
            }
        }

        // Brand Badge
        Text(
            text = "TELE-AI 2099",
            color = Color(0xFFAAAEB8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )

        // Power Status Indicator LED
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isPoweredOn) "ON AIR" else "STANDBY",
                color = if (isPoweredOn) Color(0xFF39FF14) else Color(0xFFFF5252),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isPoweredOn) Color(0xFF39FF14) else Color(0xFFFF3D00),
                        shape = CircleShape
                    )
                    .border(
                        1.dp,
                        if (isPoweredOn) Color(0x8839FF14) else Color(0x88FF3D00),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
private fun ActiveBroadcastContent(
    channel: TvChannel,
    isCaptionsVisible: Boolean,
    userVotedOptionId: Int?,
    onVoteOption: (Int) -> Unit,
    onDirectSceneClick: () -> Unit,
    onCallInClick: () -> Unit
) {
    val scene = channel.currentScene

    Box(modifier = Modifier.fillMaxSize()) {
        // Channel Backdrop Image or Thematic Gradient
        if (channel.backdropRes != null) {
            Image(
                painter = painterResource(id = channel.backdropRes),
                contentDescription = channel.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Cinematic dark gradient scrim for contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x99000000),
                                Color(0x44000000),
                                Color(0xCC05060A)
                            )
                        )
                    )
            )
        } else {
            // Dynamic colorful sci-fi canvas backdrop
            DynamicChannelBackdrop(genre = channel.genre)
        }

        // Live Broadcast Scene Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Broadcast Information Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Host Card & Scene Title
                Surface(
                    color = Color(0xDD0C0E17),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = channel.hostEmoji, fontSize = 16.sp)
                        Column {
                            Text(
                                text = channel.hostName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = channel.genre,
                                color = Color(0xFF00E5FF),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Interactive Quick Action Buttons directly on screen!
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        modifier = Modifier
                            .clickable { onDirectSceneClick() }
                            .testTag("on_screen_direct_btn"),
                        color = Color(0xDDE040FB),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🎬", fontSize = 11.sp)
                            Text(
                                text = "DIRECT",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clickable { onCallInClick() }
                            .testTag("on_screen_call_btn"),
                        color = Color(0xDD00E5FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("📞", fontSize = 11.sp)
                            Text(
                                text = "CALL IN",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Middle: Live Stage Drama (Dialogue & Description)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Scene Title Banner
                Text(
                    text = "▶ ${scene.title.uppercase()}",
                    color = Color(0xFFFFEA00),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // Host Dialogue Speech Bubble
                Surface(
                    color = Color(0xEE090B14),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF39FF14).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = scene.speaker.uppercase(),
                                color = Color(0xFF39FF14),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "• LIVE DIALOGUE",
                                color = Color.Gray,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\"${scene.dialogue}\"",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Interactive Live Viewer Poll (if active)
                if (scene.pollOptions.isNotEmpty()) {
                    LivePollCard(
                        question = scene.pollQuestion,
                        options = scene.pollOptions,
                        userVotedId = userVotedOptionId,
                        onVote = onVoteOption
                    )
                }
            }

            // Bottom: Breaking News Ticker Crawler
            NewsTickerBar(tickerText = scene.breakingNewsTicker)
        }

        // Closed Captions (Yellow Teletext style)
        if (isCaptionsVisible) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                color = Color(0xEE000000),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "[CC] ${scene.speaker}: ${scene.dialogue}",
                    color = Color(0xFFFFEB3B), // Vintage teletext yellow
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LivePollCard(
    question: String,
    options: List<PollOption>,
    userVotedId: Int?,
    onVote: (Int) -> Unit
) {
    Surface(
        color = Color(0xCC111322),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFFF9100).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Poll,
                    contentDescription = null,
                    tint = Color(0xFFFF9100),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "VIEWER LIVE VOTE: $question",
                    color = Color(0xFFFFB74D),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val totalVotes = options.sumOf { it.votes }.coerceAtLeast(1)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { opt ->
                    val percentage = (opt.votes.toFloat() / totalVotes * 100).toInt()
                    val isSelected = userVotedId == opt.id

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = userVotedId == null) { onVote(opt.id) }
                            .testTag("poll_option_${opt.id}"),
                        color = if (isSelected) Color(0xFFE65100) else Color(0xFF1E2138),
                        shape = RoundedCornerShape(4.dp),
                        border = if (isSelected) BorderStroke(1.dp, Color.White) else null
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = opt.text,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$percentage%",
                                    color = Color(0xFFFFCC80),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { opt.votes.toFloat() / totalVotes },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = if (isSelected) Color.White else Color(0xFFFF9100),
                                trackColor = Color(0xFF2E3250),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsTickerBar(tickerText: String) {
    Surface(
        color = Color(0xFFD50000), // Breaking news red
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "TICKER ●",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = tickerText,
                color = Color.White,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OsdOverlay(
    channel: TvChannel,
    volume: Int,
    isMuted: Boolean,
    isCaptionsVisible: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Channel ID Badge in classic Phosphor Green
        Text(
            text = "CH ${String.format("%02d", channel.channelNumber)} STEREO",
            color = Color(0xFF39FF14),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        // Status indicators (CC, Volume, REC)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isCaptionsVisible) {
                Text(
                    text = "[CC]",
                    color = Color(0xFF39FF14),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            if (isMuted) {
                Text(
                    text = "MUTE",
                    color = Color(0xFFFF5252),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "VOL $volume",
                    color = Color(0xFF39FF14),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun Mst3kCommentatorsOverlay(
    commentary: Commentary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(0.65f),
        horizontalAlignment = Alignment.End
    ) {
        // Speech Bubble
        Surface(
            color = Color(0xEE1A1C29),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp),
            border = BorderStroke(1.dp, Color(0xFFFF4081))
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = commentary.speaker,
                    color = Color(0xFFFF4081),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = commentary.text,
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        // Robot Silhouette Heads
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = "🦾", fontSize = 18.sp)
            Text(text = "🤖", fontSize = 22.sp)
        }
    }
}

@Composable
private fun DynamicChannelBackdrop(genre: String) {
    val bgColors = when {
        genre.contains("Cartoon", true) -> listOf(Color(0xFF311B92), Color(0xFF00B0FF), Color(0xFFFF4081))
        genre.contains("Nature", true) || genre.contains("Safari", true) -> listOf(Color(0xFF004D40), Color(0xFF00796B), Color(0xFF1B5E20))
        genre.contains("Synth", true) || genre.contains("MTV", true) -> listOf(Color(0xFF2E0854), Color(0xFF880E4F), Color(0xFF00E5FF))
        else -> listOf(Color(0xFF0D1117), Color(0xFF1A237E), Color(0xFF311B92))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(bgColors, radius = 700f))
    )
}

@Composable
private fun StaticFuzzScreen(isWarmingUp: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "static_noise")
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "noise_frame"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF10121A)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawStaticNoise(frame)
        }

        Surface(
            color = Color(0xCC000000),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFF00E5FF))
        ) {
            Text(
                text = if (isWarmingUp) "WARMING TUBE..." else "ACQUIRING SIGNAL...",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

private fun DrawScope.drawStaticNoise(seedOffset: Float) {
    val random = Random((seedOffset * 100).toInt())
    val pointCount = 1200
    val points = ArrayList<Offset>(pointCount)
    for (i in 0 until pointCount) {
        points.add(
            Offset(
                x = random.nextFloat() * size.width,
                y = random.nextFloat() * size.height
            )
        )
    }
    drawPoints(
        points = points,
        pointMode = PointMode.Points,
        color = Color(0xCCFFFFFF),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
}

@Composable
private fun PoweredOffScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090D)),
        contentAlignment = Alignment.Center
    ) {
        // Curved glass specular reflection highlight
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1AFFFFFF), Color.Transparent),
                    center = Offset(size.width * 0.35f, size.height * 0.35f),
                    radius = size.width * 0.4f
                )
            )
        }
        Text(
            text = "POWER OFF\nPRESS REMOTE TO TURN ON",
            color = Color(0xFF4A4E5A),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun CrtEffectsOverlay(crtSettings: CrtSettings) {
    val infiniteTransition = rememberInfiniteTransition(label = "crt_anim")
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker_alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Scanlines
        if (crtSettings.scanlines) {
            val scanlineSpacing = 4f
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0x33000000),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.2f
                )
                y += scanlineSpacing
            }
        }

        // 2. Screen Vignette & Tube Curvature
        if (crtSettings.curvature) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x33000000),
                        Color(0x99000000)
                    ),
                    center = Offset(width / 2f, height / 2f),
                    radius = width * 0.65f
                )
            )
        }

        // 3. Phosphor Amber tint or Green glow
        if (crtSettings.retroAmberMode) {
            drawRect(color = Color(0x2BFFB300))
        } else if (crtSettings.phosphorGlow) {
            drawRect(color = Color(0x0C00FF66))
        }

        // 4. Subtle CRT Screen Flicker
        if (crtSettings.flicker) {
            drawRect(color = Color.White.copy(alpha = flickerAlpha))
        }
    }
}

@Composable
private fun TvBottomHardwarePanel(
    channelNumber: Int,
    isPoweredOn: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Channel Rotary Knob styling
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF13141B), CircleShape)
                    .border(2.dp, Color(0xFF45495E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(10.dp)
                        .background(Color(0xFF39FF14))
                )
            }
            Text(
                text = "TUNER DIAL",
                color = Color(0xFF6B7280),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Channel Digital LED Readout
        Surface(
            color = Color(0xFF0A0C13),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color(0xFF272B3C))
        ) {
            Text(
                text = if (isPoweredOn) String.format("%02d", channelNumber) else "--",
                color = if (isPoweredOn) Color(0xFFFF3D00) else Color(0xFF3E4353),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        // Antenna and Stereo Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "STEREO AI",
                color = Color(0xFF6B7280),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF00E5FF), CircleShape)
            )
        }
    }
}
