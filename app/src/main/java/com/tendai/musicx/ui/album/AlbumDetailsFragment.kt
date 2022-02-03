package com.tendai.musicx.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentAlbumDetailsBinding
import com.tendai.musicx.ui.BaseFragment

class AlbumDetailsFragment : BaseFragment<FragmentAlbumDetailsBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentAlbumDetailsBinding.inflate(inflater, container, attachToRoot)


    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}