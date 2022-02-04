package com.tendai.musicx.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.databinding.FragmentTracksBinding
import com.tendai.musicx.ui.BaseFragment

class TracksFragment : BaseFragment<FragmentTracksBinding>() {

    companion object {
        fun newInstance() = TracksFragment()
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentTracksBinding.inflate(inflater, container, attachToRoot)

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }

}

