package com.example.mp3player.data.model

import androidx.room.PrimaryKey

data class MP3File(
    val name: String,
    val path: String,
    val artist: String,
    val duration: Int? = null,
    var isPlaying: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
