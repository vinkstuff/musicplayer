package vinkovic.filip.musicplayer.ui.artists.di

import dagger.Subcomponent
import vinkovic.filip.musicplayer.ui.artists.ArtistListFragment

@Subcomponent(modules = arrayOf(ArtistListModule::class))
interface ArtistListComponent {

    fun inject(fragment: ArtistListFragment)
}