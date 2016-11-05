package vinkovic.filip.musicplayer.ui.artist_details.albums

import vinkovic.filip.musicplayer.data.Album
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.ResponseListener
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import javax.inject.Inject

class AlbumListPresenterImpl
@Inject constructor(val view: AlbumListView,
                    val musicInteractor: MusicInteractor) : AlbumListPresenter {

    override fun init(artist: Artist) {
        musicInteractor.getAlbumsByArtist(artist.id, songsListener)
    }

    override fun onAlbumClicked(album: Album) {
        view.openAlbumDetails(album)
    }

    val songsListener: ResponseListener<List<Album>> = object : ResponseListener<List<Album>> {

        override fun onSuccess(response: List<Album>) {
            view.showAlbumList(response)
        }

        override fun onError(error: String) {
            view.showMessage(error)
        }
    }

    override fun cancel() {
        musicInteractor.isCanceled = true
    }
}