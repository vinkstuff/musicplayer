package vinkovic.filip.musicplayer.ui.artist_details.albums

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.song_list_item.view.*
import timber.log.Timber
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.data.Album

class AlbumListAdapter(val context: Context,
                       val albums: List<Album>) : RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>() {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    var onItemClickListener: ((Album, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumViewHolder {
        val view = inflater.inflate(R.layout.song_list_item, parent, false)
        return AlbumViewHolder(view)
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {

        val album = albums[position]

        holder.title.text = album.title
        Glide.with(context).load(album.albumArt).placeholder(R.drawable.album_art_placeholder)
                .listener(object : RequestListener<Uri, GlideDrawable> {
                    override fun onException(e: Exception?, model: Uri?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        Timber.e(e, "Error loading album cover")
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: Uri?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(holder.image)

        holder.subtitle.text = if (album.year != 0) album.year.toString() else ""

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(album, holder.image)
        }
    }


    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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