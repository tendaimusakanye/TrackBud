package com.tendai.musicx.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentDiscoverBinding
import com.tendai.musicx.ui.BaseFragment

class DiscoverFragment : BaseFragment<FragmentDiscoverBinding>() {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentDiscoverBinding.inflate(inflater, container, attachToRoot)

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}