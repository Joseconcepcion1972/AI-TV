package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class TvAudioSynthesizer(context: Context) : TextToSpeech.OnInitListener {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TvAudio", "Failed to init TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TvAudio", "US English TTS language not supported")
            } else {
                isTtsReady = true
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.05f)
            }
        }
    }

    fun speak(text: String, pitch: Float = 1.0f, rate: Float = 1.0f) {
        if (!isTtsReady) return
        try {
            tts?.setPitch(pitch)
            tts?.setSpeechRate(rate)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tv_speech_${System.currentTimeMillis()}")
            } else {
                @Suppress("DEPRECATION")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        } catch (e: Exception) {
            Log.e("TvAudio", "TTS speak failed", e)
        }
    }

    fun stopSpeech() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TvAudio", "TTS stop failed", e)
        }
    }

    fun playStaticBurst(durationMs: Int = 220, volume: Float = 0.35f) {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val sample = ((Random.nextFloat() * 2f - 1f) * Short.MAX_VALUE * volume).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                Log.e("TvAudio", "Static audio error", e)
            }
        }
    }

    fun playRemoteClick() {
        scope.launch {
            playTone(freq = 1950.0, durationMs = 30, volume = 0.25f)
        }
    }

    fun playPowerPop(turnOn: Boolean) {
        scope.launch {
            val sampleRate = 22050
            val durationMs = 120
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            val startFreq = if (turnOn) 200.0 else 1200.0
            val endFreq = if (turnOn) 1200.0 else 100.0

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val envelope = sin(progress * PI)
                val sample = (sin(2 * PI * currentFreq * (i.toDouble() / sampleRate)) * envelope * Short.MAX_VALUE * 0.4).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    fun playAlertBeep() {
        scope.launch {
            playTone(freq = 880.0, durationMs = 60, volume = 0.3f)
            kotlinx.coroutines.delay(40)
            playTone(freq = 1174.0, durationMs = 90, volume = 0.35f)
        }
    }

    private fun playTone(freq: Double, durationMs: Int, volume: Float = 0.3f) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Smooth attack and release envelope to prevent clicks
            val envelope = when {
                progress < 0.1 -> progress / 0.1
                progress > 0.8 -> (1.0 - progress) / 0.2
                else -> 1.0
            }
            val sample = (sin(2 * PI * freq * (i.toDouble() / sampleRate)) * envelope * Short.MAX_VALUE * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playPcmBuffer(buffer, sampleRate)
    }

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        var track: AudioTrack? = null
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufferSize, buffer.size * 2)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep((buffer.size.toDouble() / sampleRate * 1000).toLong() + 20)
        } catch (e: Exception) {
            Log.e("TvAudio", "Error playing PCM buffer", e)
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (ignored: Exception) {}
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (ignored: Exception) {}
    }
}
