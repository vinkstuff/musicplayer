package vinkovic.filip.musicplayer.ui.artists.di

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import vinkovic.filip.musicplayer.data.interactors.impl.MusicInteractorImpl
import vinkovic.filip.musicplayer.ui.artists.ArtistListPresenter
import vinkovic.filip.musicplayer.ui.artists.ArtistListPresenterImpl
import vinkovic.filip.musicplayer.ui.artists.ArtistListView

@Module
class ArtistListModule(val view: ArtistListView,
                       val contentResolver: ContentResolver) {

    @Provides
    fun view() = view

    @Provides
    fun contentResolver() = contentResolver

    @Provides
    fun presenter(presenter: ArtistListPresenterImpl): ArtistListPresenter = presenter

    @Provides
    fun interactor(interactor: MusicInteractorImpl): MusicInteractor = interactor
}
