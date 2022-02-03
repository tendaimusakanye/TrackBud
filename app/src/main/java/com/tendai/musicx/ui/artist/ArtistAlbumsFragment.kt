package com.tendai.musicx.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentArtistAlbumsBinding
import com.tendai.musicx.ui.BaseFragment

class ArtistAlbumsFragment : BaseFragment<FragmentArtistAlbumsBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding =
        FragmentArtistAlbumsBinding.inflate(inflater, container, attachToRoot)

    override fun setUp() {
        TODO("Not yet implemented")

    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}