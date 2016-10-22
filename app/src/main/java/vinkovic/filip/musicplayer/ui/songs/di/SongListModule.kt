package vinkovic.filip.musicplayer.ui.songs.di

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import vinkovic.filip.musicplayer.data.interactors.impl.MusicInteractorImpl
import vinkovic.filip.musicplayer.ui.songs.SongListPresenter
import vinkovic.filip.musicplayer.ui.songs.SongListPresenterImpl
import vinkovic.filip.musicplayer.ui.songs.SongListView

@Module
class SongListModule(val view: SongListView,
                     val contentResolver: ContentResolver) {

    @Provides
    fun view() = view

    @Provides
    fun contentResolver() = contentResolver

    @Provides
    fun presenter(presenter: SongListPresenterImpl): SongListPresenter = presenter

    @Provides
    fun interactor(interactor: MusicInteractorImpl): MusicInteractor = interactor
}
