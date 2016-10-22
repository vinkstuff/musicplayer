package vinkovic.filip.musicplayer.ui.artists

import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.ResponseListener
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import javax.inject.Inject

class ArtistListPresenterImpl
@Inject constructor(val view: ArtistListView,
                    val interactor: MusicInteractor) : ArtistListPresenter {

    override fun init() {
        interactor.getAllArtists(artistsListener)
    }

    val artistsListener: ResponseListener<List<Artist>> = object : ResponseListener<List<Artist>> {

        override fun onSuccess(response: List<Artist>) {
            view.showArtistList(response)
        }

        override fun onError(error: String) {
            view.showMessage(error)
        }
    }

    override fun onArtistSelected(id: Long) {
    }

    override fun cancel() {
        interactor.isCanceled = true
    }
}
