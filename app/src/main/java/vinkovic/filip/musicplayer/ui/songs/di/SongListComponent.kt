package vinkovic.filip.musicplayer.ui.songs.di

import dagger.Subcomponent
import vinkovic.filip.musicplayer.ui.songs.SongListFragment

@Subcomponent(modules = arrayOf(SongListModule::class))
interface SongListComponent {

    fun inject(fragment: SongListFragment)
}
