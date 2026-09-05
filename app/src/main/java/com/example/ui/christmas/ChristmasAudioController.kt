package com.example.ui.christmas

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import com.example.R

internal class ChristmasAudioController(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var musicPlayer: MediaPlayer? = null
    private var focusRequest: AudioFocusRequest? = null

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val cardFlipSound = soundPool.load(appContext, R.raw.christmas_card_flip, 1)

    fun enterChristmasGame() {
        if (musicPlayer != null) {
            resume()
            return
        }
        requestFocus()
        musicPlayer = MediaPlayer.create(appContext, R.raw.christmas_music)?.apply {
            isLooping = true
            setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
            start()
        }
    }

    fun playCardFlip(volume: Float = .72f) {
        soundPool.play(cardFlipSound, volume, volume, 1, 0, 1f)
    }

    fun pause() {
        musicPlayer?.runCatching { if (isPlaying) pause() }
    }

    fun resume() {
        requestFocus()
        musicPlayer?.runCatching { if (!isPlaying) start() }
    }

    fun leaveChristmasGame() {
        musicPlayer?.runCatching {
            if (isPlaying) stop()
            release()
        }
        musicPlayer = null
        soundPool.release()
        abandonFocus()
    }

    private fun requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener { change ->
                    when (change) {
                        AudioManager.AUDIOFOCUS_LOSS,
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> musicPlayer?.setVolume(.20f, .20f)
                        AudioManager.AUDIOFOCUS_GAIN -> {
                            musicPlayer?.setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
                            resume()
                        }
                    }
                }
                .build()
                .also { focusRequest = it }
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let(audioManager::abandonAudioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
        focusRequest = null
    }

    companion object {
        const val MUSIC_VOLUME = .65f
    }
}
