package vinkovic.filip.musicplayer.ui.artist_details

import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import javax.inject.Inject

class ArtistDetailsPresenterImpl
@Inject constructor(val view: ArtistDetailsView,
                    val musicInteractor: MusicInteractor) : ArtistDetailsPresenter {

    val tabTitles: Array<Int> = arrayOf(R.string.songs, R.string.albums)

    override fun init(artist: Artist) {
        view.setArtistName(artist.name)
        view.setArtistCover(artist.artistArt)
        view.showTabs(tabTitles)
    }

    override fun onSongSelected(song: Song) {
    }

    override fun onShuffleAllClicked() {
    }

    override fun cancel() {
    }
}