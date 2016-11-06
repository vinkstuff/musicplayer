package vinkovic.filip.musicplayer.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.support.v7.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
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

    val ACTION_PREV_SONG = "previous"
    val ACTION_NEXT_SONG = "next"
    val ACTION_PAUSE = "pause"

    var player = MediaPlayer()

    var songs: List<Song>? = null
        set(value) {
            field = value
            currentSongPosition = 0
        }

    var currentSong: Song? = null

    var currentSongPosition = 0

    val musicBind = MusicBinder()

    val NOTIFY_ID = 1

    var shuffle = true

    var random = Random()

    var serviceListener: MusicServiceListener? = null

    var largeIconHeight: Int? = 0

    var largeIconWidth: Int? = 0

    lateinit var notificationManager: NotificationManager

    lateinit var notificationBuilder: NotificationCompat.Builder

    var progressHandler = Handler()

    var progressRunnable = object : Runnable {
        override fun run() {
            notificationBuilder.setProgress(100, getSongProgress().toInt(), false)
            notificationManager.notify(NOTIFY_ID, notificationBuilder.build())
            progressHandler.postDelayed(this, 1000)
        }
    }
    
    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(applicationContext)

        initMusicPlayer()
    }

    fun initMusicPlayer() {
        player.setWakeMode(applicationContext,
                PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)

        largeIconHeight = resources.getDimension(android.R.dimen.notification_large_icon_height).toInt()
        largeIconWidth = resources.getDimension(android.R.dimen.notification_large_icon_width).toInt()
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
            onPlayerPaused()
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Timber.v("Playback Error")
        mp.reset()
        return false
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_NEXT_SONG -> playNext()
            ACTION_PREV_SONG -> playPrevious()
            ACTION_PAUSE -> if (isPlaying()) pausePlayer() else resume()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()

        serviceListener?.onSongChanged(currentSong!!)

        val notificationIntent = PlayerActivity.newIntent(this)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendInt = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = notificationBuilder
                .setSmallIcon(R.drawable.ic_play)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_skip_previous, "", createActionIntent(ACTION_PREV_SONG))
                .addAction(R.drawable.ic_pause, "", createActionIntent(ACTION_PAUSE))
                .addAction(R.drawable.ic_skip_next, "", createActionIntent(ACTION_NEXT_SONG))
                .setContentTitle(currentSong!!.title)
                .setContentText(currentSong!!.artist)
                .setContentIntent(pendInt)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.album_art_placeholder)).build()

        startForeground(NOTIFY_ID, notification)

        startNotificationProgressUpdate()

        Glide.with(this).load(currentSong?.albumArt).asBitmap().placeholder(R.drawable.album_art_placeholder).dontAnimate()
                .into(object : SimpleTarget<Bitmap>(largeIconWidth!!, largeIconHeight!!) {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        notificationBuilder.setLargeIcon(resource)
                        notificationManager.notify(NOTIFY_ID, notificationBuilder.build())
                        Glide.clear(this)
                    }
                })
    }

    fun createActionIntent(action: String): PendingIntent {
        val serviceIntent = Intent(this, MusicService::class.java)
        serviceIntent.action = action
        return PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun startNotificationProgressUpdate() {
        progressHandler.postDelayed(progressRunnable, 1000)
    }

    fun stopNotificationProgressUpdate() {
        progressHandler.removeCallbacksAndMessages(null)
    }

    fun onPlayerResumed() {
        serviceListener?.onPlayerStarted()

        val pauseAction = notificationBuilder.mActions.elementAt(1)
        pauseAction.icon = R.drawable.ic_pause
        notificationBuilder.mActions[1] = pauseAction
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build())

        startNotificationProgressUpdate()
    }

    fun onPlayerPaused() {
        serviceListener?.onPlayerPaused()

        val playAction = notificationBuilder.mActions.elementAt(1)
        playAction.icon = R.drawable.ic_play
        notificationBuilder.mActions[1] = playAction
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build())

        stopNotificationProgressUpdate()
    }

    /**
     * Returns the time progress of the currently playing song in percentage 0 - 100.
     */
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
        onPlayerPaused()
    }

    fun seek(position: Int) {
        player.seekTo(position)
    }

    fun resume() {
        player.start()
        onPlayerResumed()
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