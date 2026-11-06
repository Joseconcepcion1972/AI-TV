package com.example.data

import android.util.Log
import com.example.model.PollOption
import com.example.model.TvChannel
import com.example.model.TvScene
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import kotlin.random.Random

private const val TAG = "TvAiRepository"

class TvAiRepository {

    // Scene Generation Schema
    private val sceneSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Short catchy episode or scene title"),
            "sceneDescription" to Schema.string(description = "Action and visual setting description (1-2 sentences)"),
            "speaker" to Schema.string(description = "Character or host speaking"),
            "dialogue" to Schema.string(description = "Spoken line or broadcast narration (1-3 sentences)"),
            "choices" to Schema.array(
                items = Schema.string(),
                description = "2 to 3 interactive next actions or choices for the director/viewer"
            ),
            "visualMood" to Schema.string(description = "Visual mood tone, e.g. Cyberpunk Neon, Culinary Drama, Noir Shadow, Cosmic Wonder"),
            "pollQuestion" to Schema.string(description = "A fun live viewer vote question related to this scene"),
            "pollOption1" to Schema.string(description = "First poll choice"),
            "pollOption2" to Schema.string(description = "Second poll choice"),
            "breakingNewsTicker" to Schema.string(description = "Humorous or dramatic bottom-screen crawler ticker text")
        )
    )

    // Custom Channel Schema
    private val channelSchema = Schema.obj(
        properties = mapOf(
            "channelName" to Schema.string(description = "Catchy TV Channel title"),
            "genre" to Schema.string(description = "Specific TV genre"),
            "hostName" to Schema.string(description = "Name of the main show anchor or protagonist"),
            "hostTitle" to Schema.string(description = "Role title e.g. Anchor, Head Chef, Grand Alchemist"),
            "hostEmoji" to Schema.string(description = "A single representative emoji for the host"),
            "initialSceneTitle" to Schema.string(description = "Opening scene title"),
            "initialDescription" to Schema.string(description = "Opening scene stage description"),
            "initialSpeaker" to Schema.string(description = "First speaking character"),
            "initialDialogue" to Schema.string(description = "First dramatic line spoken"),
            "choice1" to Schema.string(description = "First branching choice for the viewer"),
            "choice2" to Schema.string(description = "Second branching choice for the viewer"),
            "ticker" to Schema.string(description = "Bottom screen news ticker")
        )
    )

    // Generative models initialized lazily per Firebase AI Logic guidelines
    private val sceneModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = sceneSchema
                temperature = 0.85f
            }
        )
    }

    private val channelModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = channelSchema
                temperature = 0.9f
            }
        )
    }

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.9f
            }
        )
    }

    /**
     * Advance the TV broadcast storyline based on viewer director choice or custom action.
     */
    suspend fun generateNextScene(
        channel: TvChannel,
        userDirectorInput: String
    ): Result<TvScene> {
        return try {
            val prompt = """
                You are broadcasting the live TV show: '${channel.name}' (Genre: ${channel.genre}).
                Show Host/Lead: ${channel.hostName} (${channel.hostTitle}).
                Channel Context: ${channel.promptContext}.
                
                Current Scene Title: ${channel.currentScene.title}
                Current Dialogue: "${channel.currentScene.dialogue}"
                
                The viewer has just pressed the Director Remote and triggered this action:
                "$userDirectorInput"
                
                Generate the immediate next broadcast scene in high TV entertainment value with dramatic dialogue, visual mood, 2-3 interactive next choices, a live viewer poll question with 2 choices, and a witty bottom crawler ticker.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                sceneModel.generateContent(prompt)
            }

            val jsonText = response.text ?: throw IllegalStateException("AI returned empty response")
            val json = JSONObject(jsonText)

            val choicesArray = json.optJSONArray("choices")
            val choicesList = mutableListOf<String>()
            if (choicesArray != null) {
                for (i in 0 until choicesArray.length()) {
                    choicesList.add(choicesArray.getString(i))
                }
            }
            if (choicesList.isEmpty()) {
                choicesList.add("Investigate the strange sound")
                choicesList.add("Confront the intruder head-on")
            }

            val pollQ = json.optString("pollQuestion", "What should happen next?")
            val opt1 = json.optString("pollOption1", "Stay calm")
            val opt2 = json.optString("pollOption2", "Run away!")

            val nextScene = TvScene(
                title = json.optString("title", "Breaking Broadcast"),
                sceneDescription = json.optString("sceneDescription", "The broadcast continues with unexpected twists."),
                speaker = json.optString("speaker", channel.hostName),
                dialogue = json.optString("dialogue", "Things are taking an unexpected turn here live in the studio!"),
                choices = choicesList,
                visualMood = json.optString("visualMood", "Dramatic"),
                pollQuestion = pollQ,
                pollOptions = listOf(
                    PollOption(id = 1, text = opt1, votes = Random.nextInt(120, 850)),
                    PollOption(id = 2, text = opt2, votes = Random.nextInt(110, 800))
                ),
                breakingNewsTicker = json.optString("breakingNewsTicker", "LIVE BROADCAST /// AI TV INTERACTIVE FEED")
            )

            Result.success(nextScene)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Scene generation timed out", e)
            Result.failure(Exception("Broadcast signal timed out. Please try another director command."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Scene generation failed", e)
            Result.failure(e)
        }
    }

    /**
     * Live caller dialogue stream where user speaks to the TV host.
     */
    fun streamCallerInteraction(
        channel: TvChannel,
        callerQuestion: String
    ): Flow<String> = flow {
        val prompt = """
            You are '${channel.hostName}' (${channel.hostTitle}), currently on-air hosting the TV show '${channel.name}'.
            Genre: ${channel.genre}.
            Host Persona & Context: ${channel.promptContext}.
            
            A live home viewer has just dialed into the TV hotline and asked:
            "$callerQuestion"
            
            Answer the caller LIVE on air! Be completely in-character, entertaining, charismatic, reactive, and concise (2-3 sentences max). Address the caller as "caller" or "viewer from home".
        """.trimIndent()

        withTimeout(60_000L) {
            chatModel.generateContentStream(prompt).collect { chunk ->
                chunk.text?.let { text ->
                    emit(text)
                }
            }
        }
    }

    /**
     * Generate quick MST3K-style live commentary roast from couch robots.
     */
    suspend fun generateMysteryCommentary(
        channel: TvChannel,
        scene: TvScene
    ): Result<Pair<String, String>> {
        return try {
            val isGizmo = Random.nextBoolean()
            val commentator = if (isGizmo) "Gizmo 🤖" else "Rusty 🦾"
            val prompt = """
                You are $commentator, a snarky, funny robot watching television on the couch like in Mystery Science Theater 3000.
                You are currently watching: '${channel.name}' (${channel.genre}).
                Scene on TV: "${scene.title}" - ${scene.sceneDescription}.
                Line on TV: ${scene.speaker}: "${scene.dialogue}".
                
                Write ONE hilarious, sharp, punchy one-liner roast or riff about what just happened on TV. (Maximum 20 words).
            """.trimIndent()

            val response = withTimeout(45_000L) {
                chatModel.generateContent(prompt)
            }
            val text = response.text?.trim() ?: "Pass the popcorn, this is getting weird!"
            Result.success(Pair(commentator, text))
        } catch (e: TimeoutCancellationException) {
            Result.failure(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate a brand-new user-crafted channel.
     */
    suspend fun createCustomChannel(
        premise: String,
        genre: String,
        channelNumber: Int
    ): Result<TvChannel> {
        return try {
            val prompt = """
                Create a full television channel and premiere show based on:
                Premise: "$premise"
                Genre: "$genre"
                Channel Number: $channelNumber
                
                Output structured TV channel metadata, charismatic host persona, and the initial dramatic opening scene.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                channelModel.generateContent(prompt)
            }

            val jsonText = response.text ?: throw IllegalStateException("Model returned empty text")
            val json = JSONObject(jsonText)

            val channelName = json.optString("channelName", "Ch $channelNumber - Custom Network")
            val hostName = json.optString("hostName", "Alex Vance")
            val hostTitle = json.optString("hostTitle", "Host")
            val hostEmoji = json.optString("hostEmoji", "📺")

            val initialScene = TvScene(
                title = json.optString("initialSceneTitle", "Premiere Broadcast"),
                sceneDescription = json.optString("initialDescription", "The brand-new channel goes live for the first time."),
                speaker = json.optString("initialSpeaker", hostName),
                dialogue = json.optString("initialDialogue", "Welcome viewers to our inaugural broadcast!"),
                choices = listOf(
                    json.optString("choice1", "Start the main feature"),
                    json.optString("choice2", "Take calls from viewers")
                ),
                visualMood = genre,
                pollQuestion = "Are you tuned in for the premiere?",
                pollOptions = listOf(
                    PollOption(1, "Loving it!", Random.nextInt(200, 700)),
                    PollOption(2, "Needs more explosions", Random.nextInt(150, 600))
                ),
                breakingNewsTicker = json.optString("ticker", "NEW STATION ONLINE: $channelName /// TUNE IN NOW")
            )

            val channel = TvChannel(
                id = "custom_${System.currentTimeMillis()}",
                channelNumber = channelNumber,
                name = channelName,
                genre = json.optString("genre", genre),
                hostName = hostName,
                hostTitle = hostTitle,
                hostEmoji = hostEmoji,
                promptContext = premise,
                backdropRes = null,
                currentScene = initialScene,
                isCustom = true
            )

            Result.success(channel)
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Channel creation timed out. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
