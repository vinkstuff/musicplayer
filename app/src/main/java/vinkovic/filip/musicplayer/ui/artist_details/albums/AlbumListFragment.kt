package vinkovic.filip.musicplayer.ui.artist_details.albums

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
import vinkovic.filip.musicplayer.data.Album
import vinkovic.filip.musicplayer.data.Artist
import vinkovic.filip.musicplayer.ui.artist_details.albums.di.AlbumListModule
import vinkovic.filip.musicplayer.ui.base.BaseFragment
import javax.inject.Inject

class AlbumListFragment : BaseFragment(), AlbumListView {

    companion object {
        val EXTRA_ARTIST = "artist"

        fun newInstance(artist: Artist): AlbumListFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_ARTIST, artist)
            val fragment = AlbumListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var presenter: AlbumListPresenter

    var adapter: AlbumListAdapter? = null

    var transitionOptions: ActivityOptionsCompat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        presenter.init(arguments.getSerializable(EXTRA_ARTIST) as Artist)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(AlbumListModule(this), MusicInteractorModule(getBaseActivity().contentResolver)).inject(this)
    }

    private fun init() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun showAlbumList(albums: List<Album>) {
        adapter = AlbumListAdapter(context, albums)
        recyclerView.adapter = adapter
        adapter?.onItemClickListener = { album, imageView ->
            initTransitionOptions(imageView)
            presenter.onAlbumClicked(album)
        }
    }

    override fun openAlbumDetails(album: Album) {

    }

    fun initTransitionOptions(imageView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getBaseActivity(),
                    Pair.create(imageView, getString(R.string.transition_name_album_cover)))
        }
    }
}