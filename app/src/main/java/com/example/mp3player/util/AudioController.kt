package com.example.mp3player.util

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalStateException


open class AudioController(private val mp: MediaPlayer, private var path: String?) {
    private var isStarted = false

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        try {
            path?.let {
                mp.setDataSource(it)
                mp.prepare()
            }
        } catch (e: IllegalStateException) {

        }

    }

    fun start() {
        if (!isStarted)
            mp.setOnPreparedListener {
                mp.start()
                isStarted = true
            } else {
            mp.start()
        }
    }

    fun pause() {
        Log.d("_RENDER_MAIN", "pause: ${mp.isPlaying}")
        if (mp.isPlaying) {
            mp.pause()
            Log.d("_RENDER_MAIN", "pause: ${mp.isPlaying}")
        }

    }

    fun stop() {
        if (mp.isPlaying) {
            mp.pause()
            mp.seekTo(0)
            Log.d("_RENDER_MAIN", "pause: ${mp.isPlaying}")

        }

    }

    fun release() {
        mp.release()
    }

    fun changeDataSource(newPath: String) {
        path = newPath
        preparePlayer()
    }

    suspend fun setOnTimeChangeListener(): Flow<Int> {
        return flow {
            while (isPlaying()) {
                emit(mp.currentPosition)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun isPlaying() = mp.currentPosition <= mp.duration
}