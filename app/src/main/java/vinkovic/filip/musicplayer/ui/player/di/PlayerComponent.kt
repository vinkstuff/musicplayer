package vinkovic.filip.musicplayer.ui.player.di

import dagger.Subcomponent
import vinkovic.filip.musicplayer.ui.player.PlayerActivity

@Subcomponent(modules = arrayOf(PlayerModule::class))
interface PlayerComponent {

    fun inject(activity: PlayerActivity)
}