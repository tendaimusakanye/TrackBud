package com.tendai.musicx.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentPlaylistDetailsBinding
import com.tendai.musicx.ui.BaseFragment

class PlaylistDetailsFragment : BaseFragment<FragmentPlaylistDetailsBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding =
        FragmentPlaylistDetailsBinding.inflate(inflater, container, attachToRoot)

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}