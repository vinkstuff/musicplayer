package vinkovic.filip.musicplayer.ui.artist_details

import android.net.Uri
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BasePresenter
import vinkovic.filip.musicplayer.ui.base.BaseView

interface ArtistDetailsView : BaseView {

    fun setArtistName(name: String)

    fun setArtistCover(uri: Uri)

    fun showTabs(tabTitles: Array<Int>)
}

interface ArtistDetailsPresenter : BasePresenter {

    fun init(artist: Artist)

    fun onSongSelected(song: Song)

    fun onShuffleAllClicked()
}