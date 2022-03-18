package com.example.mp3player.ui.home

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mp3player.data.model.MP3File
import com.example.mp3player.data.repository.AudioRepository
import com.example.mp3player.util.AudioController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaViewModel @Inject constructor(private val repository: AudioRepository) : ViewModel() {
    val intentsChannel = Channel<MainIntents>(Channel.BUFFERED)
    private val _state = MutableStateFlow<MainViewStates>(MainViewStates.Idle)
    val state: StateFlow<MainViewStates> = _state
    private val audioController: AudioController by lazy {
        AudioController(
            mp = MediaPlayer(),
            null
        )
    }

    init {
        processIntents()
    }

    private fun processIntents() {
        viewModelScope.launch {
            intentsChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is MainIntents.ShowSongs -> loadSongs()
                    is MainIntents.PlayAudio -> playAudio(intent.path)
                    is MainIntents.Stop -> stopAudio()
                    is MainIntents.Pause -> pauseAudio()
                }
            }
        }
    }


    private fun loadSongs() {
        viewModelScope.launch {
            repository.fetchSongs()
                .collect {
                _state.value = MainViewStates.LoadedSongs(it)
            }
        }
    }

    private fun playAudio(path: String) {
        audioController.changeDataSource(path)
        audioController.start()
        viewModelScope.launch {
            audioController.setOnTimeChangeListener().collect { time ->
                _state.value = MainViewStates.CurrentPlaybackPosition(time.div(1000))
            }
        }
    }

    private fun stopAudio() {
        audioController.stop()
    }

    private fun pauseAudio() {
        audioController.pause()
    }

    override fun onCleared() {
        super.onCleared()
        audioController.release()
    }

}