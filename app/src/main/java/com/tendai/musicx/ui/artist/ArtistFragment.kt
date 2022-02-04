package com.tendai.musicx.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentArtistBinding
import com.tendai.musicx.ui.BaseFragment

class ArtistFragment : BaseFragment<FragmentArtistBinding>() {

    companion object {
        fun newInstance() = ArtistFragment()
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentArtistBinding.inflate(inflater, container, attachToRoot)

}