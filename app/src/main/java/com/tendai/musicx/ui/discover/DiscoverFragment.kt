package com.tendai.musicx.ui.discover

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.databinding.FragmentDiscoverBinding
import com.tendai.musicx.ui.BaseFragment
import com.tendai.musicx.ui.album.AlbumAdapter
import com.tendai.musicx.ui.playlist.PlaylistAdapter
import javax.inject.Inject

class DiscoverFragment : BaseFragment<FragmentDiscoverBinding>() {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<DiscoverViewModel> { viewModelFactory }
    private val albumAdapter = AlbumAdapter()
    private val playlistAdapter = PlaylistAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.discoverComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            recyclerViewAlbums.adapter = albumAdapter
            recyclerViewPlaylist.adapter = playlistAdapter
        }

        viewModel.mediaItems.observe(viewLifecycleOwner) { mediaItems ->
            albumAdapter.submitList(mediaItems.take(5))
            playlistAdapter.submitList(mediaItems.takeLast(5))
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentDiscoverBinding.inflate(inflater, container, attachToRoot)
}