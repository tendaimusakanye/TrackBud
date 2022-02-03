package com.tendai.musicx.ui.queue

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

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}