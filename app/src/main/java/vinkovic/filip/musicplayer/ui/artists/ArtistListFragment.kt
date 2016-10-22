package vinkovic.filip.musicplayer.ui.artists

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_song_list.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.ui.artists.di.ArtistListModule
import vinkovic.filip.musicplayer.ui.base.BaseFragment
import vinkovic.filip.musicplayer.ui.songs.ArtistListAdapter
import javax.inject.Inject

class ArtistListFragment : BaseFragment(), ArtistListView {

    @Inject
    lateinit var presenter: ArtistListPresenter

    var adapter: ArtistListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        presenter.init()
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(ArtistListModule(this, getBaseActivity().contentResolver)).inject(this)
    }

    private fun init() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun showArtistList(artists: List<Artist>) {
        adapter = ArtistListAdapter(context, artists)
        recyclerView.adapter = adapter
    }

}
