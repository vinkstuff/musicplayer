package vinkovic.filip.musicplayer.ui.player

import android.net.Uri
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BasePresenter

interface PlayerView {

    fun playSongs(songs: List<Song>)

    fun setSongTitle(title: String)

    fun setArtist(artist: String)

    fun setAlbumCover(uri: Uri)

    fun setProgress(progress: Float)

    fun updateUI()

    fun updatePlayButton()

    fun getCurrentPlaylist(): List<Song>?

    fun getCurrentSongIndex(): Int

    fun getCurrentSong(): Song?
}

interface PlayerPresenter : BasePresenter {

    fun init(songs: List<Song>?)

    fun onServiceConnected(isPlaying: Boolean)

    fun onServiceDisconnected()

    fun onSongStarted(song: Song)

    fun onPlayClicked()

    fun onPauseClicked()

    fun onNextClicked()

    fun onPreviousClicked()
}