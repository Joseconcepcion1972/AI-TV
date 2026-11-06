package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveSheet
import com.example.model.ChatMessage
import com.example.ui.components.CallInDialog
import com.example.ui.components.ChannelCreatorDialog
import com.example.ui.components.CrtSettingsDialog
import com.example.ui.components.CrtTvScreen
import com.example.ui.components.DirectorDialog
import com.example.ui.components.FullTvRemoteSheet
import com.example.ui.components.TvGuideSheet
import com.example.ui.components.TvRemoteBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvMainScreen(
    viewModel: TvViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle back button for closing active sheets
    BackHandler(enabled = uiState.activeSheet != ActiveSheet.NONE) {
        viewModel.closeSheet()
    }

    LaunchedEffect(uiState.statusNotification) {
        uiState.statusNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("tv_main_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0C0E16),
        bottomBar = {
            TvRemoteBar(
                isPoweredOn = uiState.isPoweredOn,
                onTogglePower = { viewModel.togglePower() },
                onChannelUp = { viewModel.channelUp() },
                onChannelDown = { viewModel.channelDown() },
                onVolumeUp = { viewModel.volumeUp() },
                onVolumeDown = { viewModel.volumeDown() },
                onOpenSheet = { sheet -> viewModel.openSheet(sheet) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B0D16),
                            Color(0xFF101322),
                            Color(0xFF090A11)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TV Ambient Screen Container
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Television Set
                    CrtTvScreen(
                        channel = uiState.currentChannel,
                        isPoweredOn = uiState.isPoweredOn,
                        isWarmingUp = uiState.isWarmingUp,
                        isChangingChannel = uiState.isChangingChannel,
                        crtSettings = uiState.crtSettings,
                        volume = uiState.volume,
                        isMuted = uiState.isMuted,
                        isCaptionsVisible = uiState.isCaptionsVisible,
                        latestCommentary = uiState.latestCommentary,
                        isCommentaryVisible = uiState.isCommentaryVisible,
                        userVotedOptionId = uiState.userVotedOptionId,
                        onVoteOption = { optId -> viewModel.voteInPoll(optId) },
                        onDirectSceneClick = { viewModel.openSheet(ActiveSheet.DIRECTOR) },
                        onCallInClick = { viewModel.openSheet(ActiveSheet.CALL_IN) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Audience Chat Stream Ticker
                    LiveAudienceChatStrip(
                        chatMessages = uiState.liveChatMessages,
                        onOpenDirector = { viewModel.openSheet(ActiveSheet.DIRECTOR) },
                        onTriggerRoast = { viewModel.triggerCommentaryRoast() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            // AI Studio Broadcaster Thinking Indicator
            AnimatedVisibility(
                visible = uiState.isAiThinking,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color = Color(0xF2121422),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF00E5FF),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = uiState.aiStatusMessage.ifEmpty { "AI Broadcaster synchronizing..." },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets for interactive features
    if (uiState.activeSheet != ActiveSheet.NONE) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeSheet() },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            when (uiState.activeSheet) {
                ActiveSheet.REMOTE -> {
                    FullTvRemoteSheet(
                        isPoweredOn = uiState.isPoweredOn,
                        volume = uiState.volume,
                        isMuted = uiState.isMuted,
                        isCaptionsVisible = uiState.isCaptionsVisible,
                        ttsEnabled = uiState.crtSettings.ttsVoice,
                        keypadInput = uiState.numericKeypadInput,
                        onTogglePower = { viewModel.togglePower() },
                        onKeypadDigit = { digit -> viewModel.inputKeypadDigit(digit) },
                        onKeypadSubmit = { viewModel.submitKeypadChannel() },
                        onKeypadClear = { viewModel.clearKeypadInput() },
                        onChannelUp = { viewModel.channelUp() },
                        onChannelDown = { viewModel.channelDown() },
                        onVolumeUp = { viewModel.volumeUp() },
                        onVolumeDown = { viewModel.volumeDown() },
                        onToggleMute = { viewModel.toggleMute() },
                        onToggleCaptions = { viewModel.toggleCaptions() },
                        onToggleTts = {
                            viewModel.updateCrtSettings { it.copy(ttsVoice = !it.ttsVoice) }
                        },
                        onTriggerRoast = { viewModel.triggerCommentaryRoast() },
                        onOpenDirector = { viewModel.openSheet(ActiveSheet.DIRECTOR) },
                        onOpenCallIn = { viewModel.openSheet(ActiveSheet.CALL_IN) },
                        onOpenGuide = { viewModel.openSheet(ActiveSheet.GUIDE) },
                        onOpenNewChannel = { viewModel.openSheet(ActiveSheet.CUSTOM_CHANNEL) },
                        onOpenSettings = { viewModel.openSheet(ActiveSheet.SETTINGS) }
                    )
                }

                ActiveSheet.DIRECTOR -> {
                    DirectorDialog(
                        channel = uiState.currentChannel,
                        isAiThinking = uiState.isAiThinking,
                        onDirectChoice = { choice -> viewModel.directNextScene(choice) },
                        onDismiss = { viewModel.closeSheet() }
                    )
                }

                ActiveSheet.CALL_IN -> {
                    CallInDialog(
                        channel = uiState.currentChannel,
                        isCallerStreaming = uiState.isCallerStreaming,
                        callerStreamText = uiState.callerStreamText,
                        onSendQuestion = { q -> viewModel.startCallIn(q) },
                        onDismiss = { viewModel.closeSheet() }
                    )
                }

                ActiveSheet.GUIDE -> {
                    TvGuideSheet(
                        channels = uiState.channels,
                        currentChannelIndex = uiState.currentChannelIndex,
                        onSelectChannel = { idx ->
                            viewModel.tuneToChannelByIndex(idx)
                            viewModel.closeSheet()
                        },
                        onOpenCreateChannel = { viewModel.openSheet(ActiveSheet.CUSTOM_CHANNEL) },
                        onDismiss = { viewModel.closeSheet() }
                    )
                }

                ActiveSheet.CUSTOM_CHANNEL -> {
                    ChannelCreatorDialog(
                        isAiThinking = uiState.isAiThinking,
                        onCreateChannel = { premise, genre -> viewModel.createChannel(premise, genre) },
                        onDismiss = { viewModel.closeSheet() }
                    )
                }

                ActiveSheet.SETTINGS -> {
                    CrtSettingsDialog(
                        crtSettings = uiState.crtSettings,
                        onUpdateSettings = { transform -> viewModel.updateCrtSettings(transform) },
                        onDismiss = { viewModel.closeSheet() }
                    )
                }

                ActiveSheet.NONE -> {}
            }
        }
    }
}

@Composable
private fun LiveAudienceChatStrip(
    chatMessages: List<ChatMessage>,
    onOpenDirector: () -> Unit,
    onTriggerRoast: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF141724),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF262C42))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF39FF14), CircleShape)
                    )
                    Text(
                        text = "LIVE AUDIENCE CHATTER",
                        color = Color(0xFF9098B2),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "🍿 ROAST SHOW",
                        color = Color(0xFFFF4081),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { onTriggerRoast() }
                    )
                    Text(text = "•", color = Color.Gray, fontSize = 9.sp)
                    Text(
                        text = "🎬 DIRECT",
                        color = Color(0xFFE040FB),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { onOpenDirector() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(chatMessages.takeLast(6)) { chat ->
                    Surface(
                        color = Color(0xFF1C2032),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(chat.colorHex).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = chat.username,
                                color = Color(chat.colorHex),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = chat.message,
                                color = Color.White,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
