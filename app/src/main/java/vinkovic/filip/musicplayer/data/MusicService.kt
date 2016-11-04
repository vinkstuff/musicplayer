package vinkovic.filip.musicplayer.data

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import timber.log.Timber
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.ui.player.PlayerActivity
import java.util.*

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    interface MusicServiceListener {
        fun onPlayerStarted()
        fun onPlayerPaused()
        fun onSongChanged(song: Song)
    }

    var player = MediaPlayer()

    var songs: List<Song>? = null
        set(value) {
            field = value
            currentSongPosition = 0
        }

    var currentSong: Song? = null

    var currentSongPosition = 0

    val musicBind = MusicBinder()

    var songTitle: String? = ""

    val NOTIFY_ID = 1

    var shuffle = true

    var random = Random()

    var serviceListener: MusicServiceListener? = null

    override fun onCreate() {
        super.onCreate()

        initMusicPlayer()
    }

    fun initMusicPlayer() {
        player.setWakeMode(applicationContext,
                PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
    }

    fun setList(theSongs: ArrayList<Song>) {
        songs = theSongs
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return musicBind
    }

    fun playSong() {
        player.reset()

        val song = songs?.get(currentSongPosition) ?: return
        currentSong = song

        songTitle = song.title

        val trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id)

        try {

            player.setDataSource(applicationContext, trackUri)
        } catch (e: Exception) {
            Timber.e("Error setting data source", e)

            // If this song can't be played for some reason just skip to the next one. TODO: Maybe add an error message.
            playNext()
            return
        }

        player.prepareAsync()
    }

    fun setSong(songIndex: Int) {
        currentSongPosition = songIndex
    }

    override fun onCompletion(mp: MediaPlayer) {
        //check if playback has reached the end of a track
        if (player.currentPosition > 0) {
            mp.reset()
            playNext()
        } else {
            serviceListener?.onPlayerPaused()
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Timber.v("Playback Error")
        mp.reset()
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()

        serviceListener?.onSongChanged(currentSong!!)

        val notificationIntent = PlayerActivity.newIntent(this)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendInt = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = Notification.Builder(this)

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle)

        val notification = builder.build()

        startForeground(NOTIFY_ID, notification)
    }

    fun getSongProgress(): Float {
        if (currentSong == null) return 0f else return player.currentPosition.toFloat() / currentSong!!.duration.toFloat() * 100f
    }

    fun getSongDuration(): Int {
        return player.duration
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun pausePlayer() {
        player.pause()
        serviceListener?.onPlayerPaused()
    }

    fun seek(position: Int) {
        player.seekTo(position)
    }

    fun resume() {
        player.start()
        serviceListener?.onPlayerStarted()
    }

    fun playPrevious() {
        currentSongPosition--
        if (currentSongPosition < 0 && songs != null) {
            currentSongPosition = songs!!.size - 1
        }
        playSong()
    }

    fun playNext() {
        if (songs == null) {
            return
        }

        if (shuffle) {
            var newSong = currentSongPosition
            while (newSong == currentSongPosition) {
                newSong = random.nextInt(songs!!.size)
            }
            currentSongPosition = newSong
        } else {
            currentSongPosition++
            if (currentSongPosition >= songs!!.size) {
                currentSongPosition = 0
            }
        }
        playSong()
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        stopForeground(true)
    }
}