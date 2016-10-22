package vinkovic.filip.musicplayer.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.dagger.modules.MainModule
import vinkovic.filip.musicplayer.ui.artists.ArtistListFragment
import vinkovic.filip.musicplayer.ui.base.BaseActivity
import vinkovic.filip.musicplayer.ui.songs.SongListFragment
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView, EasyPermissions.PermissionCallbacks, TabLayout.OnTabSelectedListener {

    val RC_SETTINGS_SCREEN = 0
    val RC_READ_EXTERNAL_STORAGE = 1

    @Inject
    lateinit var presenter: MainPresenter

    var adapter: TabsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar(toolbar, null, MenuIcon.HAMBURGER)
        init()
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(MainModule(this)).inject(this)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search ->
                return true
            R.id.more ->
                return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    inner class TabsAdapter(fragmentManager: FragmentManager, val tabTitles: Array<Int>) : FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            when (tabTitles[position]) {
                R.string.songs -> return SongListFragment()
                R.string.artists -> return ArtistListFragment()
                R.string.playlists -> return SongListFragment()
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

    private fun init() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            presenter.init()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.read_external_storage_rationale),
                    RC_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, permissions: MutableList<String>?) {
        Timber.e("Permission denied")
        // Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, permissions)) {
            AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show()
        }
    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>?) {
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SETTINGS_SCREEN) {
            init()
        }
    }
}