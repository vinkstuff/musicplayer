package vinkovic.filip.musicplayer.dagger.components

import dagger.Subcomponent
import vinkovic.filip.musicplayer.dagger.modules.MainModule
import vinkovic.filip.musicplayer.ui.main.MainActivity

@Subcomponent(modules = arrayOf(MainModule::class))
interface MainComponent {
    fun inject(activity: MainActivity)
}
