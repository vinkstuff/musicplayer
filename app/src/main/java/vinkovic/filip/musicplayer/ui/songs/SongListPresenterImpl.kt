package vinkovic.filip.musicplayer.ui.songs

import vinkovic.filip.musicplayer.data.Album
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.ResponseListener
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import javax.inject.Inject

class SongListPresenterImpl
@Inject constructor(val view: SongListView,
                    val interactor: MusicInteractor) : SongListPresenter {

    var songs: List<Song>? = null

    override fun init() {
        interactor.getAllSongs(songsListener)
    }

    override fun init(artist: Artist) {
        interactor.getSongsByArtist(artist.id, songsListener)
    }

    override fun init(album: Album) {
        interactor.getSongsByAlbum(album.id, songsListener)
    }

    val songsListener: ResponseListener<List<Song>> = object : ResponseListener<List<Song>> {

        override fun onSuccess(response: List<Song>) {
            songs = response
            view.showSongList(response)
        }

        override fun onError(error: String) {
            view.showMessage(error)
        }
    }

    override fun cancel() {
        interactor.isCanceled = true
    }

    override fun onSongSelected(song: Song) {
        val playList = mutableListOf(song)
        playList += songs?.toList()!!
        view.playSongs(playList)
    }
}
