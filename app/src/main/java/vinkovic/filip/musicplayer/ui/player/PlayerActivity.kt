package vinkovic.filip.musicplayer.ui.player

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityOptionsCompat
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_player.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.data.MusicService
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BaseActivity
import vinkovic.filip.musicplayer.ui.player.di.PlayerModule
import java.io.Serializable
import javax.inject.Inject

class PlayerActivity : BaseActivity(), PlayerView, Runnable, MusicService.MusicServiceListener {

    companion object {
        const val EXTRA_SONGS = "songs"

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            return intent
        }

        fun newIntent(context: Context, songs: List<Song>): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(EXTRA_SONGS, songs as Serializable)
            return intent
        }

        fun startActivity(activity: Activity, songs: List<Song>, transitionOptions: ActivityOptionsCompat?) {
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra(EXTRA_SONGS, songs as Serializable)
            activity.startActivity(intent, transitionOptions?.toBundle())
        }
    }

    @Inject
    lateinit var presenter: PlayerPresenter

    lateinit var musicService: MusicService

    var serviceIntent: Intent? = null

    var progressHandler = Handler()

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
            musicService = (binder as MusicService.MusicBinder).service
            musicService.serviceListener = this@PlayerActivity
            presenter.onServiceConnected(musicService.isPlaying())
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService.serviceListener = null
            presenter.onServiceDisconnected()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        serviceIntent = Intent(this, MusicService::class.java)

        init()

        presenter.init(intent.getSerializableExtra(EXTRA_SONGS) as List<Song>?)

        startService(serviceIntent)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(PlayerModule(this)).inject(this)
    }

    override fun onResume() {
        super.onResume()
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        musicService.serviceListener = null
        unbindService(serviceConnection)
    }

    override fun onStop() {
        super.onStop()
    }

    private fun init() {
        btnPlay.setOnClickListener {
            if (musicService.isPlaying() == true) {
                musicService.pausePlayer()
                btnPlay.setState(PlayPauseButton.State.PLAY)
            } else {
                musicService.resume()
                btnPlay.setState(PlayPauseButton.State.PAUSE)
            }
        }

        btnPrevious.setOnClickListener { musicService.playPrevious() }
        btnNext.setOnClickListener { musicService.playNext() }
    }

    override fun onPlayerStarted() {
        btnPlay.setState(PlayPauseButton.State.PAUSE)
        progressHandler.postDelayed(this, 1000)
    }

    override fun onPlayerPaused() {
        btnPlay.setState(PlayPauseButton.State.PLAY)
        progressHandler.removeCallbacks(this)
    }

    override fun onSongChanged(song: Song) {
        presenter.onSongStarted(song)
    }

    override fun run() {
        updateUI()
    }

    override fun getCurrentPlaylist(): List<Song>? {
        return musicService.songs
    }

    override fun getCurrentSong(): Song? {
        return musicService.currentSong
    }

    override fun getCurrentSongIndex(): Int {
        return musicService.currentSongPosition
    }

    override fun updateUI() {
        progressHandler.postDelayed(this, 1000)
        albumCover.setValue(musicService.getSongProgress())
    }

    override fun updatePlayButton() {
        if (musicService.isPlaying() == true) {
            btnPlay.setState(PlayPauseButton.State.PAUSE)
        } else {
            btnPlay.setState(PlayPauseButton.State.PLAY)
        }
    }

    override fun playSongs(songs: List<Song>) {
        musicService.songs = songs
        musicService.playSong()
    }

    override fun setSongTitle(title: String) {
        songTitle.text = title
    }

    override fun setArtist(artist: String) {
        songArtist.text = artist
    }

    override fun setAlbumCover(uri: Uri) {
        Glide.with(this).load(uri).placeholder(R.drawable.album_art_placeholder).dontAnimate().into(albumCover)
    }

    override fun setProgress(progress: Float) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
