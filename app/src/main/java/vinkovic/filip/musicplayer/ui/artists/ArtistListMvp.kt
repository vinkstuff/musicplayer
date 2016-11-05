package vinkovic.filip.musicplayer.ui.artists

import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.ui.base.BasePresenter
import vinkovic.filip.musicplayer.ui.base.BaseView

interface ArtistListView : BaseView {

    fun showArtistList(artists: List<Artist>)

    fun openArtistDetails(artist: Artist)
}

interface ArtistListPresenter : BasePresenter {

    fun init()

    fun onArtistSelected(artist: Artist)
}