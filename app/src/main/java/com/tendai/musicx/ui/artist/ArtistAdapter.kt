package com.tendai.musicx.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tendai.musicx.model.MediaItemData
import com.tendai.musicx.databinding.ItemArtistDetailBinding


class ArtistAdapter :
    ListAdapter<MediaItemData, ArtistAdapter.ViewHolder>(MediaItemData.diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(private val binding: ItemArtistDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mediaItem: MediaItemData) {

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemArtistDetailBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }

    }
}