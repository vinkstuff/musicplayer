package vinkovic.filip.musicplayer.data.interactors

import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.ResponseListener
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BaseInteractor

interface MusicInteractor : BaseInteractor {

    fun getAllSongs(listener: ResponseListener<List<Song>>)

    fun getSongById(songId: Long, listener: ResponseListener<List<Song>>)

    fun getSongsByArtist(artistId: Long, listener: ResponseListener<List<Song>>)

    fun getAlbum(albumId: Long, listener: ResponseListener<List<Song>>)

    fun getSongsByGenre(genre: String, listener: ResponseListener<List<Song>>)

    fun getAllArtists(listener: ResponseListener<List<Artist>>)
}
