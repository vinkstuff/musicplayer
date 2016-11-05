package vinkovic.filip.musicplayer.dagger.components

import dagger.Component
import vinkovic.filip.musicplayer.MusicPlayerApplication
import vinkovic.filip.musicplayer.dagger.modules.AppModule
import vinkovic.filip.musicplayer.dagger.modules.MusicInteractorModule
import vinkovic.filip.musicplayer.ui.artist_details.albums.di.AlbumListComponent
import vinkovic.filip.musicplayer.ui.artist_details.albums.di.AlbumListModule
import vinkovic.filip.musicplayer.ui.artist_details.di.ArtistDetailsComponent
import vinkovic.filip.musicplayer.ui.artist_details.di.ArtistDetailsModule
import vinkovic.filip.musicplayer.ui.artists.di.ArtistListComponent
import vinkovic.filip.musicplayer.ui.artists.di.ArtistListModule
import vinkovic.filip.musicplayer.ui.main.di.MainComponent
import vinkovic.filip.musicplayer.ui.main.di.MainModule
import vinkovic.filip.musicplayer.ui.player.di.PlayerComponent
import vinkovic.filip.musicplayer.ui.player.di.PlayerModule
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

    fun plus(songListModule: SongListModule, musicInteractorModule: MusicInteractorModule): SongListComponent

    fun plus(module: ArtistListModule, musicInteractorModule: MusicInteractorModule): ArtistListComponent

    fun plus(module: PlayerModule): PlayerComponent

    fun plus(module: ArtistDetailsModule, musicInteractorModule: MusicInteractorModule): ArtistDetailsComponent

    fun plus(module: AlbumListModule, musicInteractorModule: MusicInteractorModule): AlbumListComponent
}