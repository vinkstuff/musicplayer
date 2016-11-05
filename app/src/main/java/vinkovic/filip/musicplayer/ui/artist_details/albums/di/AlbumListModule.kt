package vinkovic.filip.musicplayer.ui.artist_details.albums.di

import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.ui.artist_details.albums.AlbumListPresenter
import vinkovic.filip.musicplayer.ui.artist_details.albums.AlbumListPresenterImpl
import vinkovic.filip.musicplayer.ui.artist_details.albums.AlbumListView

@Module
class AlbumListModule(val view: AlbumListView) {

    @Provides
    fun view() = view

    @Provides
    fun presenter(presenter: AlbumListPresenterImpl): AlbumListPresenter = presenter
}