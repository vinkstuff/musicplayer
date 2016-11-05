package vinkovic.filip.musicplayer.ui.songs

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
import vinkovic.filip.musicplayer.data.Song
import vinkovic.filip.musicplayer.ui.base.BaseFragment
import vinkovic.filip.musicplayer.ui.player.PlayerActivity
import vinkovic.filip.musicplayer.ui.songs.di.SongListModule
import javax.inject.Inject

class SongListFragment : BaseFragment(), SongListView {

    companion object {

        val EXTRA_ARTIST = "artist"
        val EXTRA_ALBUM = "album"

        fun newInstance(artist: Artist): SongListFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_ARTIST, artist)
            val fragment = SongListFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(album: Album): SongListFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_ALBUM, album)
            val fragment = SongListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var presenter: SongListPresenter

    var adapter: SongListAdapter? = null

    var transitionOptions: ActivityOptionsCompat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        if (arguments != null) {
            if (arguments.containsKey(EXTRA_ARTIST)) {
                presenter.init(arguments.getSerializable(EXTRA_ARTIST) as Artist)
            } else if (arguments.containsKey(EXTRA_ALBUM)) {
                presenter.init(arguments.getSerializable(EXTRA_ALBUM) as Album)
            }
        } else {
            presenter.init()
        }
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(SongListModule(this), MusicInteractorModule(getBaseActivity().contentResolver)).inject(this)
    }

    private fun init() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun showSongList(songs: List<Song>) {
        adapter = SongListAdapter(context, songs)
        recyclerView.adapter = adapter
        adapter?.onItemClickListener = { song, imageView ->
            initTransitionOptions(imageView)
            presenter.onSongSelected(song)
        }
    }

    override fun playSongs(songs: List<Song>) {
        PlayerActivity.startActivity(getBaseActivity(), songs, null)
    }

    fun initTransitionOptions(imageView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getBaseActivity(),
                    Pair.create(imageView, getString(R.string.transition_name_album_cover)))
        }
    }
}
