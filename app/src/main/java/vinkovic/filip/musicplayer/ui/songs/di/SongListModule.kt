package vinkovic.filip.musicplayer.ui.songs.di

import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.ui.songs.SongListPresenter
import vinkovic.filip.musicplayer.ui.songs.SongListPresenterImpl
import vinkovic.filip.musicplayer.ui.songs.SongListView

@Module
class SongListModule(val view: SongListView) {

    @Provides
    fun view() = view

    @Provides
    fun presenter(presenter: SongListPresenterImpl): SongListPresenter = presenter
}
