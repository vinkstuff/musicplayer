package vinkovic.filip.musicplayer

import android.app.Application
import timber.log.Timber
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.dagger.components.DaggerAppComponent

class MusicPlayerApplication: Application() {

    companion object {
        lateinit var instance: MusicPlayerApplication
            private set
    }

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

        init()
    }

    private fun init() {
        // TODO Add CrashReportingTree for non-debug builds when Crashlytics is added.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}