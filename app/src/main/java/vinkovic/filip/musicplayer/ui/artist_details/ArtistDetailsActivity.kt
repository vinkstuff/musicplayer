package vinkovic.filip.musicplayer.ui.artist_details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_artist_details.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.dagger.modules.MusicInteractorModule
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.ui.artist_details.albums.AlbumListFragment
import vinkovic.filip.musicplayer.ui.artist_details.di.ArtistDetailsModule
import vinkovic.filip.musicplayer.ui.base.BaseActivity
import vinkovic.filip.musicplayer.ui.songs.SongListFragment
import java.io.Serializable
import javax.inject.Inject

class ArtistDetailsActivity : BaseActivity(), ArtistDetailsView, TabLayout.OnTabSelectedListener {

    companion object {
        const val EXTRA_ARTIST = "artist"

        fun startActivity(activity: Activity, artist: Artist, transitionOptions: ActivityOptionsCompat?) {
            val intent = Intent(activity, ArtistDetailsActivity::class.java)
            intent.putExtra(EXTRA_ARTIST, artist as Serializable)
            activity.startActivity(intent, transitionOptions?.toBundle())
        }
    }

    @Inject
    lateinit var presenter: ArtistDetailsPresenter

    lateinit var artist: Artist

    var adapter: TabsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_details)

        initToolbar(toolbar, null, MenuIcon.BACK)

        artist = intent.getSerializableExtra(EXTRA_ARTIST) as Artist
        presenter.init(artist)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(ArtistDetailsModule(this), MusicInteractorModule(contentResolver)).inject(this)
    }

    override fun setArtistName(name: String) {
        collapsingToolbar.title = name
    }

    override fun setArtistCover(uri: Uri) {
        Glide.with(this).load(uri).placeholder(R.drawable.album_art_placeholder).into(artistCover)
    }

    override fun showTabs(tabTitles: Array<Int>) {
        adapter = TabsAdapter(supportFragmentManager, tabTitles)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = tabTitles.size
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager.currentItem = tab.position
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

    inner class TabsAdapter(fragmentManager: FragmentManager, val tabTitles: Array<Int>) : FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            when (tabTitles[position]) {
                R.string.songs -> return SongListFragment.newInstance(artist)
                R.string.albums -> return AlbumListFragment.newInstance(artist)
                else -> return SongListFragment()
            }
        }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getString(tabTitles[position])
        }
    }
}