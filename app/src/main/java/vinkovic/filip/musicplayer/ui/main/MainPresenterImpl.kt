package vinkovic.filip.musicplayer.ui.main

import vinkovic.filip.musicplayer.R
import javax.inject.Inject

class MainPresenterImpl
@Inject constructor(val view: MainView): MainPresenter {

    val tabTitles: Array<Int> = arrayOf(R.string.songs, R.string.artists, R.string.playlists)

    override fun init() {
        view.showTabs(tabTitles)
    }
}
