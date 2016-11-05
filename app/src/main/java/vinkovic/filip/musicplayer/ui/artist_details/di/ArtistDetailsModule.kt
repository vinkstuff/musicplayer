package vinkovic.filip.musicplayer.ui.artist_details.di

import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.ui.artist_details.ArtistDetailsPresenter
import vinkovic.filip.musicplayer.ui.artist_details.ArtistDetailsPresenterImpl
import vinkovic.filip.musicplayer.ui.artist_details.ArtistDetailsView

@Module
class ArtistDetailsModule(val view: ArtistDetailsView) {

    @Provides
    fun view() = view

    @Provides
    fun presenter(presenter: ArtistDetailsPresenterImpl): ArtistDetailsPresenter = presenter
}