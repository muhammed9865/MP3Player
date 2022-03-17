package com.example.mp3player.util

import android.media.MediaPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalStateException


open class AudioController(private val mp: MediaPlayer, private var path: String?) {
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
        mp.setOnPreparedListener { player ->
            player.start()
        }
    }

    fun pause() {
        mp.setOnPreparedListener { player ->
            player.pause()
        }
    }

    fun stop() {
        mp.setOnPreparedListener { player ->
            player.stop()
            player.release()
        }
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