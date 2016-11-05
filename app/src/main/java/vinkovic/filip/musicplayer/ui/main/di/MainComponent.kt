package vinkovic.filip.musicplayer.ui.main.di

import dagger.Subcomponent
import vinkovic.filip.musicplayer.ui.main.di.MainModule
import vinkovic.filip.musicplayer.ui.main.MainActivity

@Subcomponent(modules = arrayOf(MainModule::class))
interface MainComponent {
    fun inject(activity: MainActivity)
}
