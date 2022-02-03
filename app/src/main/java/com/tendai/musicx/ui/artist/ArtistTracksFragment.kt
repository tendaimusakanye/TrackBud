package com.tendai.musicx.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentArtistTracksBinding
import com.tendai.musicx.ui.BaseFragment

class ArtistTracksFragment : BaseFragment<FragmentArtistTracksBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding =
        FragmentArtistTracksBinding.inflate(inflater, container, attachToRoot)

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}