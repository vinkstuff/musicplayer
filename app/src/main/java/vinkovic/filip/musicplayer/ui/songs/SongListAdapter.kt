package vinkovic.filip.musicplayer.ui.songs

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
import vinkovic.filip.musicplayer.data.Song

class SongListAdapter(val context: Context,
                      val songs: List<Song>) : RecyclerView.Adapter<SongListAdapter.SongViewHolder>() {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    var onItemClickListener: ((Song, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongViewHolder {
        val view = inflater.inflate(R.layout.song_list_item, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {

        val song = songs[position]

        holder.title.text = song.title
        holder.artist.text = song.artist
        Glide.with(context).load(song.albumArt).placeholder(R.drawable.album_art_placeholder).into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(song, holder.image)
        }

    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView

        val title: TextView

        val artist: TextView

        init {
            image = view.image
            title = view.title
            artist = view.artist
        }
    }
}