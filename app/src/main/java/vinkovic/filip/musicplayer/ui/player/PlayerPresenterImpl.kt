package vinkovic.filip.musicplayer.ui.player

import vinkovic.filip.musicplayer.data.Song
import javax.inject.Inject

class PlayerPresenterImpl
@Inject constructor(val view: PlayerView) : PlayerPresenter {

    var songs: List<Song>? = null

    var resetPlayer = false

    override fun init(songs: List<Song>?) {
        if (songs != null) resetPlayer = true
        this.songs = songs
    }

    override fun onServiceConnected(isPlaying: Boolean) {
        if (songs != null && resetPlayer) {
            view.playSongs(songs!!)
            showSongDetails(songs!!.first())
        } else {
            songs = view.getCurrentPlaylist()
            showSongDetails(view.getCurrentSong()!!)
        }

        resetPlayer = false

        view.updateUI()
        view.updatePlayButton()
    }

    private fun showSongDetails(song: Song) {
        view.setAlbumCover(song.albumArt)
        view.setSongTitle(song.title)
        view.setArtist(song.artist)
    }

    override fun onServiceDisconnected() {
    }

    override fun onSongStarted(song: Song) {
        showSongDetails(song)
        view.updateUI()
        view.updatePlayButton()
    }

    override fun onPlayClicked() {
    }

    override fun onPauseClicked() {
    }

    override fun onNextClicked() {
    }

    override fun onPreviousClicked() {
    }

    override fun cancel() {
    }
}
