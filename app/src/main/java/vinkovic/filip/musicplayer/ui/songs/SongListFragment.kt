package vinkovic.filip.musicplayer.ui.songs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_song_list.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BaseFragment
import vinkovic.filip.musicplayer.ui.songs.di.SongListModule
import javax.inject.Inject

class SongListFragment : BaseFragment(), SongListView {

    @Inject
    lateinit var presenter: SongListPresenter

    var adapter: SongListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        presenter.init()
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(SongListModule(this, getBaseActivity().contentResolver)).inject(this)
    }

    private fun init() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun showSongList(songs: List<Song>) {
        adapter = SongListAdapter(context, songs)
        recyclerView.adapter = adapter
    }

}
