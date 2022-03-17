package com.example.mp3player.ui.home

import com.example.mp3player.data.model.MP3File

sealed class MainViewStates {
    object Idle : MainViewStates()
    data class LoadedSongs(val songs: List<MP3File>): MainViewStates()
    data class CurrentPlaybackPosition(val timeInSec: Int): MainViewStates()
}
