package vinkovic.filip.musicplayer.data

interface ResponseListener<in T> {

    fun onSuccess(response: T)

    fun onError(error: String)
}