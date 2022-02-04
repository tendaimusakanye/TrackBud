package com.tendai.musicx.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tendai.musicx.databinding.FragmentMainBinding
import com.tendai.musicx.ui.artist.ArtistFragment
import com.tendai.musicx.ui.discover.DiscoverFragment
import com.tendai.musicx.ui.tracks.TracksFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val adapter = MainAdapter(this)

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentMainBinding.inflate(inflater, container, attachToRoot)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        }
    }

    override fun setUp() {
        TODO("Not yet implemented")
    }

    override fun setUpObservers() {
        TODO("Not yet implemented")
    }
}

private class MainAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DiscoverFragment.newInstance()
            1 -> TracksFragment.newInstance()
            2 -> ArtistFragment.newInstance()
            else -> throw IllegalArgumentException("The position {$position} is invalid.")
        }
    }
}