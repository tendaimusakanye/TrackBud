package com.tendai.musicx.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tendai.musicx.model.MediaItemData
import com.tendai.musicx.databinding.ItemPlaylistBinding

class PlaylistAdapter :
    ListAdapter<MediaItemData, PlaylistAdapter.ViewHolder>(MediaItemData.diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mediaItem: MediaItemData) {

        }

        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPlaylistBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}