package vinkovic.filip.musicplayer.ui.artists

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.song_list_item.view.*
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.data.Artist

class ArtistListAdapter(val context: Context,
                        val artists: List<Artist>) : RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>() {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    var onItemClickListener: ((Artist, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArtistViewHolder {
        val view = inflater.inflate(R.layout.song_list_item, parent, false)
        return ArtistViewHolder(view)
    }

    override fun getItemCount(): Int = artists.size

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {

        val artist = artists[position]

        holder.title.text = artist.name
        Glide.with(context).load(artist.artistArt).placeholder(R.drawable.album_art_placeholder).into(holder.image)

        val numberOfAlbums = context.resources.getQuantityString(R.plurals.albums, artist.numberOfAlbums, artist.numberOfAlbums)
        val numberOfSongs = context.resources.getQuantityString(R.plurals.songs, artist.numberOfSongs, artist.numberOfSongs)

        holder.subtitle.text = context.getString(R.string.artist_number_of_albums_and_songs, numberOfAlbums, numberOfSongs)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(artist, holder.image)
        }
    }


    class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView

        val title: TextView

        val subtitle: TextView

        init {
            image = view.image
            title = view.title
            subtitle = view.subtitle
        }
    }
}