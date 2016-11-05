package vinkovic.filip.musicplayer.ui.artists.di

import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.ui.artists.ArtistListPresenter
import vinkovic.filip.musicplayer.ui.artists.ArtistListPresenterImpl
import vinkovic.filip.musicplayer.ui.artists.ArtistListView

@Module
class ArtistListModule(val view: ArtistListView) {

    @Provides
    fun view() = view

    @Provides
    fun presenter(presenter: ArtistListPresenterImpl): ArtistListPresenter = presenter
}
