package vinkovic.filip.musicplayer.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import vinkovic.filip.musicplayer.MusicPlayerApplication
import vinkovic.filip.musicplayer.dagger.components.AppComponent

abstract class BaseFragment: Fragment(), BaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(MusicPlayerApplication.instance.appComponent)
    }

    abstract fun injectDependencies(appComponent: AppComponent)

    fun getBaseActivity() = activity as BaseActivity

    override fun showProgress() {
        getBaseActivity().showProgress()
    }

    override fun hideProgress() {
        getBaseActivity().hideProgress()
    }

    override fun showMessage(message: String) {
        getBaseActivity().showMessage(message)
    }
}