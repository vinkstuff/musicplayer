package vinkovic.filip.musicplayer.ui.artist_details.di

import dagger.Subcomponent
import vinkovic.filip.musicplayer.dagger.modules.MusicInteractorModule
import vinkovic.filip.musicplayer.ui.artist_details.ArtistDetailsActivity

@Subcomponent(modules = arrayOf(
        ArtistDetailsModule::class,
        MusicInteractorModule::class))
interface ArtistDetailsComponent {

    fun inject(activity: ArtistDetailsActivity)
}