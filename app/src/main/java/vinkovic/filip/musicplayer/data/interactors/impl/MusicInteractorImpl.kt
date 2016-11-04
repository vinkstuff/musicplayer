package vinkovic.filip.musicplayer.data.interactors.impl

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
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

    inner class SongsQueryHandler(val listener: ResponseListener<List<Song>>) : AsyncQueryHandler(contentResolver) {

        override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
            if (cursor == null) {
                if (!isCanceled) {
                    listener.onError("Error retrieving music")
                }
                return
            }
            if (!cursor.moveToFirst()) {
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
                songs.add(Song(
                        cursor.getLong(idColumn),
                        cursor.getString(artistColumn),
                        cursor.getString(titleColumn),
                        cursor.getString(albumColumn),
                        cursor.getLong(albumIdColumn),
                        cursor.getLong(durationColumn)))
            } while (cursor.moveToNext())

            cursor.close()

            if (!isCanceled) {
                listener.onSuccess(songs)
            }
        }
    }

    inner class ArtistsQueryHandler(val listener: ResponseListener<List<Artist>>) : AsyncQueryHandler(contentResolver) {

        override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
            if (cursor == null) {
                if (!isCanceled) {
                    listener.onError("Error retrieving artist list")
                }
                return
            }
            if (!cursor.moveToFirst()) {
                if (!isCanceled) {
                    listener.onError("No artists found!")
                }
                return
            }

            val artistNameColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
            val artistIdColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
            val numberOfTracksColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            val numberOfAlbumsColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)

            val artists: MutableList<Artist> = ArrayList()

            do {
                artists.add(Artist(
                        cursor.getLong(artistIdColumn),
                        cursor.getString(artistNameColumn),
                        cursor.getInt(numberOfAlbumsColumn),
                        cursor.getInt(numberOfTracksColumn)))
            } while (cursor.moveToNext())

            cursor.close()

            if (!isCanceled) {
                listener.onSuccess(artists)
            }
        }
    }

    private fun querySongs(selection: String = "", listener: ResponseListener<List<Song>>) {
        isCanceled = false
        val whereClause: String = MediaStore.Audio.Media.IS_MUSIC + " = 1" + selection
        SongsQueryHandler(listener)
                .startQuery(1,
                        null,
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        whereClause,
                        null,
                        MediaStore.Audio.Media.ARTIST_KEY + "," + MediaStore.Audio.Media.ALBUM_KEY + "," + MediaStore.Audio.Media.TRACK)
    }

    private fun queryArtists(selection: String = "", listener: ResponseListener<List<Artist>>) {
        isCanceled = false

        val projection = arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)

        ArtistsQueryHandler(listener)
                .startQuery(1,
                        null,
                        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        null,
                        MediaStore.Audio.Artists.ARTIST)
    }

    override fun getAllSongs(listener: ResponseListener<List<Song>>) {
        querySongs(listener = listener)
    }

    override fun getSongById(songId: Long, listener: ResponseListener<List<Song>>) {
        querySongs(MediaStore.Audio.Media._ID, listener)
    }

    override fun getSongsByArtist(artistId: Long, listener: ResponseListener<List<Song>>) {
        querySongs(MediaStore.Audio.Media.ARTIST_ID + " = $artistId", listener)
    }

    override fun getAlbum(albumId: Long, listener: ResponseListener<List<Song>>) {
        querySongs(MediaStore.Audio.Media.ALBUM_ID + " = $albumId", listener)
    }

    override fun getSongsByGenre(genre: String, listener: ResponseListener<List<Song>>) {
        querySongs(MediaStore.Audio.Genres.NAME + " = $genre", listener)
    }

    override fun getAllArtists(listener: ResponseListener<List<Artist>>) {
        queryArtists(listener = listener)
    }
}