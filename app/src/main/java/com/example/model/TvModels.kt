package com.example.model

data class PollOption(
    val id: Int,
    val text: String,
    val votes: Int = 0
)

data class TvScene(
    val title: String,
    val sceneDescription: String,
    val speaker: String,
    val dialogue: String,
    val choices: List<String> = emptyList(),
    val visualMood: String = "Normal",
    val pollQuestion: String = "",
    val pollOptions: List<PollOption> = emptyList(),
    val breakingNewsTicker: String = ""
)

data class TvChannel(
    val id: String,
    val channelNumber: Int,
    val name: String,
    val genre: String,
    val hostName: String,
    val hostTitle: String,
    val hostEmoji: String,
    val promptContext: String,
    val backdropRes: Int? = null,
    val currentScene: TvScene,
    val isCustom: Boolean = false
)

data class Commentary(
    val id: String,
    val speaker: String, // "Gizmo 🤖" or "Rusty 🦾"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val username: String,
    val badge: String,
    val message: String,
    val colorHex: Long
)

data class CrtSettings(
    val scanlines: Boolean = true,
    val curvature: Boolean = true,
    val flicker: Boolean = true,
    val phosphorGlow: Boolean = true,
    val retroAmberMode: Boolean = false,
    val aspectRatio4x3: Boolean = true,
    val soundEffects: Boolean = true,
    val ttsVoice: Boolean = false
)

enum class ActiveSheet {
    NONE,
    REMOTE,
    DIRECTOR,
    CALL_IN,
    GUIDE,
    CUSTOM_CHANNEL,
    SETTINGS
}
