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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MediaViewModel by lazy {
        ViewModelProvider(this)[MediaViewModel::class.java]
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launchWhenStarted {
            viewModel.intentsChannel.send(MainIntents.ShowSongs)
        }
        renderIfPermissionGranted()

        binding.btnStart.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                viewModel.intentsChannel.send(MainIntents.PlayAudio(
                    path = "/storage/emulated/0/Download/Dndnha.Com.Wegz.El Ghasala.mp3"
                ))
            }
        }

        binding.btnPause.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                viewModel.intentsChannel.send(MainIntents.Pause)
            }
        }

        binding.btnStop.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                viewModel.intentsChannel.send(MainIntents.Stop)
            }
        }

    }

    private fun renderIfPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                        Log.d(TAG, "render: ${state.songs}")
                    }
                    is MainViewStates.CurrentPlaybackPosition -> {
                        lifecycleScope.launchWhenStarted {
                            Log.d(TAG, "render: ${state.timeInSec}")
                        }

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