package vinkovic.filip.musicplayer.data

import android.content.ContentUris
import android.net.Uri
import java.io.Serializable

class Song(val id: Long, var artist: String, var title: String, val album: String, val albumId: Long, val duration: Long) : Serializable {

    init {
        if (artist == "<unknown>") artist = "Unknown"
        if (title == "<unknown>") title = "Unknown"
    }

    val uri: Uri
        get() = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

    val albumArt: Uri
        get() {
            val albumArtUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtContentUri = ContentUris.withAppendedId(albumArtUri, albumId)
            return albumArtContentUri
        }
}

class Album(val id: Long, var title: String, var artist: String, val year: Int, val numberOfSongs: Int) : Serializable {

    init {
        if (artist == "<unknown>") artist = "Unknown"
        if (title == "<unknown>") title = "Unknown"
    }

    val albumArt: Uri
        get() {
            val albumArtUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtContentUri = ContentUris.withAppendedId(albumArtUri, id)
            return albumArtContentUri
        }
}

class Artist(val id: Long, var name: String, val numberOfAlbums: Int, val numberOfSongs: Int) : Serializable {

    init {
        if (name == "<unknown>") name = "Unknown"
    }

    val uri: Uri
        get() = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

    val artistArt: Uri
        get() {
            val albumArtUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtContentUri = ContentUris.withAppendedId(albumArtUri, id)
            return albumArtContentUri
        }
}