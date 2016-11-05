package vinkovic.filip.musicplayer.dagger.modules

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.data.interactors.MusicInteractor
import vinkovic.filip.musicplayer.data.interactors.impl.MusicInteractorImpl

@Module
class MusicInteractorModule(val contentResolver: ContentResolver) {

    @Provides
    fun contentResolver() = contentResolver

    @Provides
    fun interactor(interactor: MusicInteractorImpl): MusicInteractor = interactor
}