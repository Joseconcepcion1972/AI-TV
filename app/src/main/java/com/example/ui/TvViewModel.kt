package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PresetChannels
import com.example.data.TvAiRepository
import com.example.data.TvAudioSynthesizer
import com.example.model.ActiveSheet
import com.example.model.ChatMessage
import com.example.model.Commentary
import com.example.model.CrtSettings
import com.example.model.TvChannel
import com.example.model.TvScene
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TvUiState(
    val isPoweredOn: Boolean = true,
    val isWarmingUp: Boolean = false,
    val channels: List<TvChannel> = PresetChannels.getDefaultChannels(),
    val currentChannelIndex: Int = 0,
    val isChangingChannel: Boolean = false,
    val crtSettings: CrtSettings = CrtSettings(),
    val volume: Int = 7,
    val isMuted: Boolean = false,
    val isCaptionsVisible: Boolean = true,
    val isCommentaryVisible: Boolean = true,
    val commentaryHistory: List<Commentary> = emptyList(),
    val latestCommentary: Commentary? = null,
    val activeSheet: ActiveSheet = ActiveSheet.NONE,
    val isAiThinking: Boolean = false,
    val aiStatusMessage: String = "",
    val callerStreamText: String = "",
    val isCallerStreaming: Boolean = false,
    val liveChatMessages: List<ChatMessage> = emptyList(),
    val userVotedOptionId: Int? = null,
    val statusNotification: String? = null,
    val numericKeypadInput: String = ""
) {
    val currentChannel: TvChannel
        get() = channels.getOrElse(currentChannelIndex) { channels.first() }
}

class TvViewModel(application: Application) : AndroidViewModel(application) {

    private val audio = TvAudioSynthesizer(application)
    private val aiRepository = TvAiRepository()

    private val _uiState = MutableStateFlow(TvUiState())
    val uiState: StateFlow<TvUiState> = _uiState.asStateFlow()

    init {
        seedInitialChat()
        seedInitialCommentary()
        startPeriodicLiveChat()
    }

    override fun onCleared() {
        super.onCleared()
        audio.release()
    }

    fun togglePower() {
        val next = !_uiState.value.isPoweredOn
        if (_uiState.value.crtSettings.soundEffects) {
            audio.playPowerPop(next)
        }
        if (next) {
            _uiState.update { it.copy(isPoweredOn = true, isWarmingUp = true) }
            viewModelScope.launch {
                delay(400)
                _uiState.update { it.copy(isWarmingUp = false) }
                speakCurrentDialogue()
            }
        } else {
            audio.stopSpeech()
            _uiState.update { it.copy(isPoweredOn = false, isWarmingUp = false) }
        }
    }

    fun channelUp() {
        if (!_uiState.value.isPoweredOn) return
        val nextIndex = (_uiState.value.currentChannelIndex + 1) % _uiState.value.channels.size
        changeToChannelIndex(nextIndex)
    }

    fun channelDown() {
        if (!_uiState.value.isPoweredOn) return
        val prevIndex = if (_uiState.value.currentChannelIndex - 1 < 0) {
            _uiState.value.channels.size - 1
        } else {
            _uiState.value.currentChannelIndex - 1
        }
        changeToChannelIndex(prevIndex)
    }

    fun tuneToChannel(channelNumber: Int) {
        val index = _uiState.value.channels.indexOfFirst { it.channelNumber == channelNumber }
        if (index != -1) {
            changeToChannelIndex(index)
        } else {
            showNotification("Channel $channelNumber not found in broadcast spectrum")
        }
    }

    fun tuneToChannelByIndex(index: Int) {
        if (index in _uiState.value.channels.indices) {
            changeToChannelIndex(index)
        }
    }

    private fun changeToChannelIndex(index: Int) {
        if (_uiState.value.isChangingChannel) return
        audio.stopSpeech()
        if (_uiState.value.crtSettings.soundEffects) {
            audio.playStaticBurst(durationMs = 280)
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isChangingChannel = true,
                    currentChannelIndex = index,
                    userVotedOptionId = null,
                    numericKeypadInput = ""
                )
            }
            delay(320)
            _uiState.update { it.copy(isChangingChannel = false) }
            speakCurrentDialogue()
            generateSceneCommentary()
        }
    }

    fun inputKeypadDigit(digit: Int) {
        if (_uiState.value.crtSettings.soundEffects) {
            audio.playRemoteClick()
        }
        val current = _uiState.value.numericKeypadInput
        val next = (current + digit).takeLast(2)
        _uiState.update { it.copy(numericKeypadInput = next) }

        if (next.length == 2) {
            val num = next.toIntOrNull()
            if (num != null) {
                tuneToChannel(num)
            }
        }
    }

    fun submitKeypadChannel() {
        val num = _uiState.value.numericKeypadInput.toIntOrNull()
        if (num != null) {
            tuneToChannel(num)
        }
        _uiState.update { it.copy(numericKeypadInput = "") }
    }

    fun clearKeypadInput() {
        _uiState.update { it.copy(numericKeypadInput = "") }
    }

    fun volumeUp() {
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()
        _uiState.update { it.copy(volume = (it.volume + 1).coerceAtMost(10), isMuted = false) }
    }

    fun volumeDown() {
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()
        _uiState.update { it.copy(volume = (it.volume - 1).coerceAtLeast(0)) }
    }

    fun toggleMute() {
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()
        val nextMute = !_uiState.value.isMuted
        _uiState.update { it.copy(isMuted = nextMute) }
        if (nextMute) {
            audio.stopSpeech()
        }
    }

    fun toggleCaptions() {
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()
        _uiState.update { it.copy(isCaptionsVisible = !it.isCaptionsVisible) }
    }

    fun toggleCommentary() {
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()
        _uiState.update { it.copy(isCommentaryVisible = !it.isCommentaryVisible) }
    }

    fun openSheet(sheet: ActiveSheet) {
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()
        _uiState.update { it.copy(activeSheet = sheet) }
    }

    fun closeSheet() {
        _uiState.update { it.copy(activeSheet = ActiveSheet.NONE) }
    }

    fun updateCrtSettings(transform: (CrtSettings) -> CrtSettings) {
        _uiState.update {
            val updated = transform(it.crtSettings)
            it.copy(crtSettings = updated)
        }
    }

    fun voteInPoll(optionId: Int) {
        if (_uiState.value.userVotedOptionId != null) return
        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()

        val currentScene = _uiState.value.currentChannel.currentScene
        val updatedOptions = currentScene.pollOptions.map { opt ->
            if (opt.id == optionId) opt.copy(votes = opt.votes + 1) else opt
        }

        val updatedScene = currentScene.copy(pollOptions = updatedOptions)
        val updatedChannels = _uiState.value.channels.toMutableList()
        val currentIndex = _uiState.value.currentChannelIndex
        updatedChannels[currentIndex] = updatedChannels[currentIndex].copy(currentScene = updatedScene)

        _uiState.update {
            it.copy(
                channels = updatedChannels,
                userVotedOptionId = optionId
            )
        }
        showNotification("Vote registered live on broadcast!")
    }

    /**
     * Choose director action: calls AI to reshape TV show narrative!
     */
    fun directNextScene(actionChoice: String) {
        if (actionChoice.isBlank()) return
        val channel = _uiState.value.currentChannel

        if (_uiState.value.crtSettings.soundEffects) {
            audio.playAlertBeep()
        }

        _uiState.update {
            it.copy(
                isAiThinking = true,
                aiStatusMessage = "Director command transmitting: '$actionChoice'...",
                activeSheet = ActiveSheet.NONE
            )
        }

        viewModelScope.launch {
            try {
                val result = aiRepository.generateNextScene(channel, actionChoice)
                result.onSuccess { nextScene ->
                    val updatedChannels = _uiState.value.channels.toMutableList()
                    val index = _uiState.value.currentChannelIndex
                    updatedChannels[index] = updatedChannels[index].copy(currentScene = nextScene)

                    _uiState.update {
                        it.copy(
                            channels = updatedChannels,
                            isAiThinking = false,
                            aiStatusMessage = "",
                            userVotedOptionId = null
                        )
                    }

                    showNotification("Scene redirected by Director: '${nextScene.title}'")
                    speakCurrentDialogue()
                    generateSceneCommentary()
                    addAudienceReaction(actionChoice)
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isAiThinking = false,
                            aiStatusMessage = ""
                        )
                    }
                    showNotification("Director signal error: ${err.localizedMessage ?: "Failed"}")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("TvViewModel", "Scene progression error", e)
                _uiState.update { it.copy(isAiThinking = false, aiStatusMessage = "") }
            }
        }
    }

    /**
     * Live Call-In to TV Host with streaming dialogue!
     */
    fun startCallIn(question: String) {
        if (question.isBlank()) return
        val channel = _uiState.value.currentChannel

        if (_uiState.value.crtSettings.soundEffects) audio.playRemoteClick()

        _uiState.update {
            it.copy(
                isCallerStreaming = true,
                callerStreamText = "Connecting live call to ${channel.hostName}..."
            )
        }

        viewModelScope.launch {
            try {
                val accumulated = StringBuilder()
                aiRepository.streamCallerInteraction(channel, question).collect { chunk ->
                    accumulated.append(chunk)
                    _uiState.update {
                        it.copy(callerStreamText = accumulated.toString())
                    }
                }
                _uiState.update { it.copy(isCallerStreaming = false) }

                if (_uiState.value.crtSettings.ttsVoice && !_uiState.value.isMuted) {
                    audio.speak(accumulated.toString(), pitch = 1.05f, rate = 1.0f)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("TvViewModel", "Call in error", e)
                _uiState.update {
                    it.copy(
                        isCallerStreaming = false,
                        callerStreamText = "Live line dropped: ${e.localizedMessage ?: "Connection interrupted"}"
                    )
                }
            }
        }
    }

    /**
     * Create brand-new user AI TV channel
     */
    fun createChannel(premise: String, genre: String) {
        if (premise.isBlank()) return
        val nextChannelNumber = (_uiState.value.channels.maxOfOrNull { it.channelNumber } ?: 10) + Random.nextInt(3, 9)

        _uiState.update {
            it.copy(
                isAiThinking = true,
                aiStatusMessage = "Synthesizing new TV frequency CH $nextChannelNumber...",
                activeSheet = ActiveSheet.NONE
            )
        }

        viewModelScope.launch {
            try {
                val result = aiRepository.createCustomChannel(premise, genre, nextChannelNumber)
                result.onSuccess { newChannel ->
                    val updated = _uiState.value.channels + newChannel
                    val newIndex = updated.lastIndex
                    _uiState.update {
                        it.copy(
                            channels = updated,
                            isAiThinking = false,
                            aiStatusMessage = ""
                        )
                    }
                    changeToChannelIndex(newIndex)
                    showNotification("New Channel Online: ${newChannel.name}")
                }.onFailure { err ->
                    _uiState.update { it.copy(isAiThinking = false, aiStatusMessage = "") }
                    showNotification("Channel tuning failed: ${err.localizedMessage ?: "Unknown error"}")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("TvViewModel", "Channel creation error", e)
                _uiState.update { it.copy(isAiThinking = false, aiStatusMessage = "") }
            }
        }
    }

    fun triggerCommentaryRoast() {
        generateSceneCommentary()
    }

    private fun generateSceneCommentary() {
        if (!_uiState.value.isCommentaryVisible) return
        val channel = _uiState.value.currentChannel
        val scene = channel.currentScene

        viewModelScope.launch {
            try {
                val result = aiRepository.generateMysteryCommentary(channel, scene)
                result.onSuccess { (speaker, text) ->
                    val commentary = Commentary(
                        id = "c_${System.currentTimeMillis()}",
                        speaker = speaker,
                        text = text
                    )
                    _uiState.update {
                        it.copy(
                            latestCommentary = commentary,
                            commentaryHistory = (listOf(commentary) + it.commentaryHistory).take(15)
                        )
                    }
                }
            } catch (ignored: Exception) {}
        }
    }

    private fun speakCurrentDialogue() {
        if (!_uiState.value.isPoweredOn || _uiState.value.isMuted || !_uiState.value.crtSettings.ttsVoice) return
        val scene = _uiState.value.currentChannel.currentScene
        audio.speak(scene.dialogue, pitch = 1.0f, rate = 1.05f)
    }

    private fun showNotification(msg: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(statusNotification = msg) }
            delay(3500)
            _uiState.update {
                if (it.statusNotification == msg) it.copy(statusNotification = null) else it
            }
        }
    }

    private fun seedInitialCommentary() {
        val initial = Commentary(
            id = "c_0",
            speaker = "Gizmo 🤖",
            text = "Welcome to AI TV! Grab your remote and direct the show!"
        )
        _uiState.update {
            it.copy(
                latestCommentary = initial,
                commentaryHistory = listOf(initial)
            )
        }
    }

    private fun seedInitialChat() {
        val seed = listOf(
            ChatMessage("1", "RetroGamer99", "VIP", "Channel 3 is wild tonight haha", 0xFF00E5FF),
            ChatMessage("2", "PizzaCrust", "MOD", "Someone direct the chef to use chili!", 0xFFFFB74D),
            ChatMessage("3", "CyberNova", "", "This CRT filter looks so real", 0xFF76FF03),
            ChatMessage("4", "CosmoFan", "SUB", "Saturday cartoons are the best", 0xFFFF4081)
        )
        _uiState.update { it.copy(liveChatMessages = seed) }
    }

    private fun addAudienceReaction(action: String) {
        val names = listOf("Starlight", "PixelPilot", "GlitchFox", "VaporWaveBoy", "LaserCat", "NeonKnight")
        val comments = listOf(
            "YOOO THEY ACTUALLY DID IT!",
            "OMG this storyline just got crazy",
            "Best director decision yet!",
            "Wait what happens to the suspect now??",
            "Peak television right here",
            "10/10 broadcast!!"
        )
        val newMsg = ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            username = names.random(),
            badge = if (Random.nextBoolean()) "LIVE" else "",
            message = comments.random(),
            colorHex = listOf(0xFF00E5FF, 0xFFFF4081, 0xFF76FF03, 0xFFFFEA00).random()
        )
        _uiState.update {
            it.copy(liveChatMessages = (it.liveChatMessages + newMsg).takeLast(25))
        }
    }

    private fun startPeriodicLiveChat() {
        viewModelScope.launch {
            val simulatedUsers = listOf(
                "StarGazer", "FluxCapacitor", "NeonDreamer", "SynthLord",
                "AnalogGhost", "TapeDeck", "MegaVolt", "CosmoFanatic"
            )
            val chatter = listOf(
                "Voted in the poll!",
                "Who's calling in next?",
                "Jack Stone is the coolest detective",
                "Chef Zargon needs to calm down lol",
                "Wait did you see that news ticker?!",
                "Turn the volume up!",
                "Best AI TV episode ever",
                "That plot twist came out of nowhere"
            )
            while (true) {
                delay(Random.nextLong(6000, 14000))
                if (_uiState.value.isPoweredOn && !_uiState.value.isChangingChannel) {
                    val msg = ChatMessage(
                        id = "auto_${System.currentTimeMillis()}",
                        username = simulatedUsers.random(),
                        badge = if (Random.nextInt(4) == 0) "FAN" else "",
                        message = chatter.random(),
                        colorHex = listOf(0xFF00E5FF, 0xFFFFB74D, 0xFF76FF03, 0xFFFF4081, 0xFFE040FB).random()
                    )
                    _uiState.update {
                        it.copy(liveChatMessages = (it.liveChatMessages + msg).takeLast(25))
                    }
                }
            }
        }
    }
}
