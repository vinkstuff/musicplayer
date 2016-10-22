package vinkovic.filip.musicplayer.data

import android.content.ContentUris
import android.net.Uri

class Song(val id: Long, val artist: String, val title: String, val album: String, val albumArt: Uri, val duration: Long) {
    val uri: Uri
        get() = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
}

class Artist(val id: Long, val name: String)