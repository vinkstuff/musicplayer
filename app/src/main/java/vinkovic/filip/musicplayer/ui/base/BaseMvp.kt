package vinkovic.filip.musicplayer.ui.base

interface BaseView {
    fun showProgress()
    fun hideProgress()
    fun showMessage(message: String)
}

interface BasePresenter {
    fun cancel()
}

interface BaseInteractor {

    var isCanceled: Boolean
}