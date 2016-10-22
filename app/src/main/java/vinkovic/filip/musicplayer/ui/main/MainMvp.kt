package vinkovic.filip.musicplayer.ui.main

interface MainView {
    fun showTabs(tabTitles: Array<Int>)
}

interface MainPresenter {
    fun init()
}