package vinkovic.filip.musicplayer.ui.artist_details.albums

import vinkovic.filip.musicplayer.data.Album
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.ui.base.BasePresenter
import vinkovic.filip.musicplayer.ui.base.BaseView

interface AlbumListView : BaseView {

    fun showAlbumList(albums: List<Album>)

    fun openAlbumDetails(album: Album)
}

interface AlbumListPresenter : BasePresenter {

    fun init(artist: Artist)

    fun onAlbumClicked(album: Album)
}