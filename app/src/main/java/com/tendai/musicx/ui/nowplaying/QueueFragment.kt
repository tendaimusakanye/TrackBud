package com.tendai.musicx.ui.nowplaying

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentQueueBinding
import com.tendai.musicx.ui.BaseFragment

class QueueFragment : BaseFragment<FragmentQueueBinding>() {

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentQueueBinding.inflate(inflater, container, attachToRoot)

}