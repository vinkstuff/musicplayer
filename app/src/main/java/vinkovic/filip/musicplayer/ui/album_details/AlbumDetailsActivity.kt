package vinkovic.filip.musicplayer.ui.album_details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_album_details.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.data.Album
import vinkovic.filip.musicplayer.ui.base.BaseActivity
import vinkovic.filip.musicplayer.ui.songs.SongListFragment
import java.io.Serializable

class AlbumDetailsActivity : BaseActivity() {

    companion object {
        val EXTRA_ALBUM = "album"

        fun startActivity(activity: Activity, album: Album, transitionOptions: ActivityOptionsCompat?) {
            val intent = Intent(activity, AlbumDetailsActivity::class.java)
            intent.putExtra(EXTRA_ALBUM, album as Serializable)
            activity.startActivity(intent, transitionOptions?.toBundle())
        }
    }

    lateinit var album: Album

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)

        init()
    }

    fun init() {
        initToolbar(toolbar, null, MenuIcon.BACK)

        album = intent.getSerializableExtra(EXTRA_ALBUM) as Album

        supportFragmentManager.beginTransaction()
                .add(R.id.container, SongListFragment.newInstance(album))
                .commit()

        collapsingToolbar.title = album.title
        Glide.with(this).load(album.albumArt).placeholder(R.drawable.album_art_placeholder).into(albumCover)
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

    override fun injectDependencies(appComponent: AppComponent) {
    }
}