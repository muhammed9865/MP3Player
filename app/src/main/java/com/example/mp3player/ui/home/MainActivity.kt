package com.example.mp3player.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mp3player.databinding.ActivityMainBinding
import com.example.mp3player.ui.home.adapter.AudioAdapter
import com.example.mp3player.ui.home.adapter.ControlAudio
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MediaViewModel by lazy {
        ViewModelProvider(this)[MediaViewModel::class.java]
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }
    private val mAdapter: AudioAdapter by lazy {
        AudioAdapter()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launchWhenStarted {
            viewModel.intentsChannel.send(MainIntents.ShowSongs)
        }
        renderIfPermissionGranted()

        binding.audioRv.adapter = mAdapter

    }

    private fun renderIfPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            render()
        }else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                123)
        }
    }

    private fun render() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when(state) {
                    is MainViewStates.Idle -> {}
                    is MainViewStates.LoadedSongs -> {
                        mAdapter.submitList(state.songs)
                    }
                    is MainViewStates.CurrentPlaybackPosition -> {
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                render()
            }
        }
    }

    companion object {
        private const val TAG = "_RENDER_MAIN"
    }
}