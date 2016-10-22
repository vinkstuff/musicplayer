package vinkovic.filip.musicplayer.dagger.modules

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import vinkovic.filip.musicplayer.MusicPlayerApplication
import vinkovic.filip.musicplayer.dagger.AppContext
import vinkovic.filip.musicplayer.data.PreferenceStore
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @AppContext
    @Singleton
    fun appContext(): Context = MusicPlayerApplication.instance

    @Provides
    @Singleton
    fun resources(@AppContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun preferenceStore(prefsStore: PreferenceStore): PreferenceStore = prefsStore
}