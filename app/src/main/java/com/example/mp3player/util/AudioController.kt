package com.example.mp3player.util

import android.media.MediaPlayer
import android.util.Log
import com.example.mp3player.ui.home.adapter.PlayerStates
import kotlinx.coroutines.flow.*
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
                Log.d("_AudioController", "start: $path")
                mp.start()
                isStarted = true

            } else {
            mp.start()
        }
    }

    fun pause() {
        if (mp.isPlaying) {
            mp.pause()
        }
    }

    fun stop() {
        mp.stop()
        mp.reset()
        isStarted = false
    }

    fun release() {
        isStarted = false
        mp.release()
    }

    fun changeDataSource(newPath: String) {
        path = newPath
        preparePlayer()
    }


    suspend fun setOnTimeChangeListener(): Flow<Int> {
       return flow {
                do{
                    emit(mp.currentPosition.div(1000))
                    kotlinx.coroutines.delay(1000)
                }while (isPlaying())
       }
        }

    suspend fun emitAudioDuration(): Flow<Int> {
        return flow {
            emit(mp.duration.div(1000))
        }
    }

    private fun isPlaying() = mp.isPlaying
}