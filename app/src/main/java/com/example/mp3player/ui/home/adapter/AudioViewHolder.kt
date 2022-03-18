package com.example.mp3player.ui.home.adapter

import android.media.MediaPlayer
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mp3player.R
import com.example.mp3player.data.model.MP3File
import com.example.mp3player.databinding.ListItemAudioBinding
import com.example.mp3player.util.AudioController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

class AudioViewHolder(
    private val binding: ListItemAudioBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    private var isPlaying = false
    private val ac: AudioController by lazy {
        AudioController(mp = MediaPlayer(), null)
    }


    fun bind(audio: MP3File) {
        binding.apply {
            artistNameTv.text = audio.artist
            audioTitleTv.text = audio.name
        }
        toggleStartButton(audio)
        toggleStopButton()
    }

    private fun initializeTimeListener() {
        CoroutineScope(Dispatchers.IO).launch {
            ac.emitAudioDuration().collectLatest { time ->
                MainScope().launch {
                    Log.d(TAG, "audio duration: $time")
                    binding.audioPlaybackDuration.max = time
                }
            }

            ac.setOnTimeChangeListener().collect { time ->
               MainScope().launch {
                   Log.d(TAG, "audio playback: $time")
                   binding.audioPlaybackDuration.progress = time
               }
            }
        }
    }


    private fun toggleStartButton(audio: MP3File) {
        binding.startPauseBtn.setOnClickListener {
            if (!isPlaying) {
                isPlaying = true
                ac.changeDataSource(audio.path)
                ac.start()
                initializeTimeListener()
                toggleButtonImage(isPlaying)
                toggleIndicators(true)
            } else {
                isPlaying = false
                ac.pause()
                toggleButtonImage(isPlaying)
            }

        }
    }

    private fun toggleStopButton() {
        binding.stopBtn.setOnClickListener {
            ac.stop()
            toggleIndicators(false)
            toggleButtonImage(false)
        }
    }

    private fun toggleIndicators(isPlaying: Boolean) {
        binding.apply {
            circularProgressIndicator3.visibility = if (isPlaying) View.VISIBLE else View.INVISIBLE
            audioPlaybackDuration.visibility = if (isPlaying) View.VISIBLE else View.GONE
            stopBtn.visibility = if (isPlaying) View.VISIBLE else View.GONE
        }
    }

    private fun toggleButtonImage(isPlaying: Boolean) {
        val drawable =
            if (isPlaying) R.drawable.ic_baseline_pause_circle_filled_24 else R.drawable.ic_baseline_play_circle_filled_24
        Glide.with(itemView.context)
            .load(itemView.context.getDrawable(drawable))
            .into(binding.startPauseBtn)
    }

    companion object {
        private const val TAG = "_RENDER_MAIN"
    }


}