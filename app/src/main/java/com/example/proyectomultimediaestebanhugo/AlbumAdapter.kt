package com.example.proyectomultimediaestebanhugo

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    private val albums: List<Album>,
    private val onAlbumSelected: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.albumName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.textView.text = album.name
        holder.itemView.setOnClickListener { onAlbumSelected(album) }
    }

    override fun getItemCount() = albums.size
}
