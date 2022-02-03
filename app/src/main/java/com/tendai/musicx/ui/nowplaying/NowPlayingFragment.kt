package com.tendai.musicx.ui.nowplaying

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentNowPlayingBinding
import com.tendai.musicx.ui.BaseFragment

class NowPlayingFragment : BaseFragment<FragmentNowPlayingBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentNowPlayingBinding.inflate(inflater, container, false)

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }

}