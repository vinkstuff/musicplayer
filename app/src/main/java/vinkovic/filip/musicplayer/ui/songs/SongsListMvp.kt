package vinkovic.filip.musicplayer.ui.songs

import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BasePresenter
import vinkovic.filip.musicplayer.ui.base.BaseView

interface SongListView: BaseView {

    fun showSongList(songs: List<Song>)

    fun playSongs(songs: List<Song>)
}

interface SongListPresenter: BasePresenter {

    fun init()

    fun onSongSelected(song: Song)
}
