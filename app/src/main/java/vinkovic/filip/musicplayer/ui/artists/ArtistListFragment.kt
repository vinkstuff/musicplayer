package vinkovic.filip.musicplayer.ui.artists

import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_song_list.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent
import vinkovic.filip.musicplayer.dagger.modules.MusicInteractorModule
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.ui.artist_details.ArtistDetailsActivity
import vinkovic.filip.musicplayer.ui.artists.di.ArtistListModule
import vinkovic.filip.musicplayer.ui.base.BaseFragment
import javax.inject.Inject

class ArtistListFragment : BaseFragment(), ArtistListView {

    @Inject
    lateinit var presenter: ArtistListPresenter

    var adapter: ArtistListAdapter? = null

    var transitionOptions: ActivityOptionsCompat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        presenter.init()
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(ArtistListModule(this), MusicInteractorModule(getBaseActivity().contentResolver)).inject(this)
    }

    private fun init() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun openArtistDetails(artist: Artist) {
        ArtistDetailsActivity.startActivity(getBaseActivity(), artist, null)
    }

    override fun showArtistList(artists: List<Artist>) {
        adapter = ArtistListAdapter(context, artists)
        recyclerView.adapter = adapter
        adapter?.onItemClickListener = { artist, imageView ->
            initTransitionOptions(imageView)
            presenter.onArtistSelected(artist)
        }
    }

    fun initTransitionOptions(imageView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getBaseActivity(),
                    Pair.create(imageView, getString(R.string.transition_name_album_cover)))
        }
    }

}
