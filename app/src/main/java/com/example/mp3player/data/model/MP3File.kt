package com.example.mp3player.data.model

data class MP3File(
    val name: String,
    val path: String,
    val artist: String,
    val duration: String? = null
)
