package vinkovic.filip.musicplayer.ui.artist_details.albums.di

import dagger.Subcomponent
import vinkovic.filip.musicplayer.dagger.modules.MusicInteractorModule
import vinkovic.filip.musicplayer.ui.artist_details.albums.AlbumListFragment

@Subcomponent(modules = arrayOf(
        AlbumListModule::class,
        MusicInteractorModule::class))
interface AlbumListComponent {

    fun inject(fragment: AlbumListFragment)
}
