package com.example.mp3player.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mp3player.data.model.MP3File

class AudioDiff: DiffUtil.ItemCallback<MP3File>() {
    override fun areItemsTheSame(oldItem: MP3File, newItem: MP3File): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MP3File, newItem: MP3File): Boolean {
        return oldItem.id == newItem.id
    }
}