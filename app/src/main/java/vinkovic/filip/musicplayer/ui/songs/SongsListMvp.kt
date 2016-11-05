package vinkovic.filip.musicplayer.ui.songs

import vinkovic.filip.musicplayer.data.Album
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BasePresenter
import vinkovic.filip.musicplayer.ui.base.BaseView

interface SongListView: BaseView {

    fun showSongList(songs: List<Song>)

    fun playSongs(songs: List<Song>)
}

interface SongListPresenter: BasePresenter {

    fun init()

    fun init(artist: Artist)

    fun init(album: Album)

    fun onSongSelected(song: Song)
}
