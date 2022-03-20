package com.tendai.musicx.ui.nowplaying

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentMediaControlsBinding
import com.tendai.musicx.ui.BaseFragment

class MediaControlsFragment : BaseFragment<FragmentMediaControlsBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentMediaControlsBinding.inflate(inflater, container, attachToRoot)
}