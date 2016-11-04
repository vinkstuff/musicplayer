package vinkovic.filip.musicplayer.ui.player.di

import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.ui.player.PlayerPresenter
import vinkovic.filip.musicplayer.ui.player.PlayerPresenterImpl
import vinkovic.filip.musicplayer.ui.player.PlayerView

@Module
class PlayerModule(val view: PlayerView) {

    @Provides
    fun view() = view

    @Provides
    fun presenter(presenter: PlayerPresenterImpl): PlayerPresenter = presenter
}