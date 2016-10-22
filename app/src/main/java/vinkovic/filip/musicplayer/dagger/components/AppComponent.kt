package vinkovic.filip.musicplayer.dagger.components

import dagger.Component
import vinkovic.filip.musicplayer.MusicPlayerApplication
import vinkovic.filip.musicplayer.dagger.modules.AppModule
import vinkovic.filip.musicplayer.dagger.modules.MainModule
import vinkovic.filip.musicplayer.ui.songs.di.SongListComponent
import vinkovic.filip.musicplayer.ui.songs.di.SongListModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class
))
interface AppComponent {

    fun inject(app: MusicPlayerApplication)

    fun plus(module: MainModule): MainComponent

    fun plus(module: SongListModule): SongListComponent
}