package com.example.mp3player.ui.home

sealed class MainIntents {
    object ShowSongs : MainIntents()
    data class PlayAudio(val path: String): MainIntents()
    object Pause: MainIntents()
    object Stop: MainIntents()
}