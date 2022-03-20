package com.tendai.musicx.model

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil

data class MediaItemData(
    var id: String,
    var name: String,
    var description: String,
    var albumArtUri: Uri?,
    var browsable: Boolean
) {
    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<MediaItemData>() {

            override fun areItemsTheSame(oldItem: MediaItemData, newItem: MediaItemData) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MediaItemData,
                newItem: MediaItemData
            ) = oldItem == newItem

        }
    }
}
