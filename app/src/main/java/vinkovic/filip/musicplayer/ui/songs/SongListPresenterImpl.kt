package vinkovic.filip.musicplayer.ui.songs

import vinkovic.filip.musicplayer.data.ResponseListener
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import javax.inject.Inject

class SongListPresenterImpl
@Inject constructor(val view: SongListView,
                    val interactor: MusicInteractor) : SongListPresenter {


    override fun init() {
        interactor.getAllSongs(songsListener)
    }

    val songsListener: ResponseListener<List<Song>> = object : ResponseListener<List<Song>> {

        override fun onSuccess(response: List<Song>) {
            view.showSongList(response)
        }

        override fun onError(error: String) {
            view.showMessage(error)
        }
    }

    override fun cancel() {
        interactor.isCanceled = true
    }

    override fun onSongSelected(id: Long) {
    }
}
