package com.example.mp3player.data.repository

import android.annotation.SuppressLint
import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.mp3player.data.model.MP3File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepository @Inject constructor(private val application: Application) {


    suspend fun fetchSongs(): Flow<List<MP3File>> {
        return flow {
            val storageUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            emit(prepareSongs(storageUri))
        }
    }

    @SuppressLint("Recycle", "Range")
    private fun prepareSongs(storageUri: Uri): List<MP3File> {
        val songsList = mutableListOf<MP3File>()
        val cr = application.contentResolver
        // The location of external storage of audios
        val audioDuration: String?
        val audioPath: String
        // The Columns to be fetched from the content resolver
        val projection = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            audioPath = MediaStore.MediaColumns.RELATIVE_PATH
            audioDuration = MediaStore.Audio.AudioColumns.DURATION
            listOf(
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.ArtistColumns.ARTIST
            )
        } else {
            audioPath = MediaStore.Audio.Media.DATA
            audioDuration = null
            listOf(
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.Audio.ArtistColumns.ARTIST
            )
        }
        val c: Cursor? = cr.query(
            storageUri,
            projection.toTypedArray(),
            null,
            null,
            null
        )

        if (c != null) {
            while (c.moveToNext()) {
                val songName = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val songPath = c.getString(c.getColumnIndex(audioPath))
                val songDuration: String? = audioDuration?.let { c.getString(c.getColumnIndex(it)) }
                val artistName = c.getString(c.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))

                val mp3Audio = MP3File(
                    name = songName,
                    path = songPath,
                    duration = songDuration,
                    artist = artistName
                )

                songsList.add(mp3Audio)
            }
        }
        // Recursive, Condition point
        if (storageUri != MediaStore.Audio.Media.INTERNAL_CONTENT_URI){
            prepareSongs(MediaStore.Audio.Media.INTERNAL_CONTENT_URI)
        }
        c?.close()
        return songsList
    }
}