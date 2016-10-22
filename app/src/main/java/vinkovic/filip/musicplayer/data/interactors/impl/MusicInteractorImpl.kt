package vinkovic.filip.musicplayer.data.interactors.impl

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import timber.log.Timber
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.ResponseListener
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import java.util.*
import javax.inject.Inject

class MusicInteractorImpl
@Inject constructor(val contentResolver: ContentResolver) : MusicInteractor {

    override var isCanceled: Boolean = false
        set(value) {
            field = value
        }

    inner class QueryHandler(val listener: ResponseListener<List<Song>>) : AsyncQueryHandler(contentResolver) {

        override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {

            Timber.d("Query finished. " + if (cursor == null) "Returned NULL." else "Returned a cursor.")

            if (cursor == null) {
                Timber.e("Failed to retrieve music: cursor is null :-(")

                if (!isCanceled) {
                    listener.onError("Error retrieving music")
                }
                return
            }
            if (!cursor.moveToFirst()) {
                Timber.e("Failed to move cursor to first row (no query results).")

                if (!isCanceled) {
                    listener.onError("No music found!")
                }
                return
            }

            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)

            val songs: MutableList<Song> = ArrayList()

            do {
                val albumArtUri = Uri.parse("content://media/external/audio/albumart")
                val albumArtContentUri = ContentUris.withAppendedId(albumArtUri, cursor.getLong(albumIdColumn))

                songs.add(Song(
                        cursor.getLong(idColumn),
                        cursor.getString(artistColumn),
                        cursor.getString(titleColumn),
                        cursor.getString(albumColumn),
                        albumArtContentUri,
                        cursor.getLong(durationColumn)))
            } while (cursor.moveToNext())

            cursor.close()

            Timber.d("Done querying media. Song number: " + songs.size)

            if (!isCanceled) {
                listener.onSuccess(songs)
            }
        }
    }

    private fun query(selection: String = "", listener: ResponseListener<List<Song>>) {
        isCanceled = false
        val whereClause: String = MediaStore.Audio.Media.IS_MUSIC + " = 1" + selection
        QueryHandler(listener)
                .startQuery(1,
                        null,
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        whereClause,
                        null,
                        MediaStore.Audio.Media.ARTIST_KEY + "," + MediaStore.Audio.Media.ALBUM_KEY + "," + MediaStore.Audio.Media.TRACK)
    }

    override fun getAllSongs(listener: ResponseListener<List<Song>>) {
        query(listener = listener)
    }

    override fun getSongsByArtist(artistId: Long, listener: ResponseListener<List<Song>>) {
        query(MediaStore.Audio.Media.ARTIST_ID + " = $artistId", listener)
    }

    override fun getAlbum(albumId: Long, listener: ResponseListener<List<Song>>) {
        query(MediaStore.Audio.Media.ALBUM_ID + " = $albumId", listener)
    }

    override fun getSongsByGenre(genre: String, listener: ResponseListener<List<Song>>) {
        query(MediaStore.Audio.Genres.NAME + " = $genre", listener)
    }

    override fun getAllArtists(listener: ResponseListener<List<Artist>>) {

    }
}