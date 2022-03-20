package com.tendai.musicx.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tendai.musicx.R
import com.tendai.musicx.databinding.FragmentArtistBinding
import com.tendai.musicx.databinding.FragmentArtistDetailBinding
import com.tendai.musicx.ui.BaseFragment

class ArtistDetailFragment : BaseFragment<FragmentArtistDetailBinding>() {

    private val args: ArtistDetailFragmentArgs by navArgs()
    private val adapter = ArtistDetailsAdapter(args.artistId, this)

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentArtistBinding.inflate(inflater, container, attachToRoot)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                val tabTitles = arrayOf(
                    getString(R.string.title_tracks),
                    getString(R.string.title_albums),
                )
                tab.text = tabTitles[position]
            }.attach()
        }
    }
}

private class ArtistDetailsAdapter(private val artistId: Long, fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ArtistTracksFragment.newInstance(artistId)
            1 -> ArtistAlbumsFragment.newInstance(artistId)
            else -> throw IllegalArgumentException("The supplied position $position is invalid")
        }
    }

}