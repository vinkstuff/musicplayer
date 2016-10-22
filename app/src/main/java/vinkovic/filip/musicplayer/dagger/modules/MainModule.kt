package vinkovic.filip.musicplayer.dagger.modules

import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.ui.main.MainPresenter
import vinkovic.filip.musicplayer.ui.main.MainPresenterImpl
import vinkovic.filip.musicplayer.ui.main.MainView

@Module
class MainModule(val view: MainView) {

    @Provides
    fun mainView(): MainView = view

    @Provides
    fun mainPresenter(presenter: MainPresenterImpl): MainPresenter = presenter
}