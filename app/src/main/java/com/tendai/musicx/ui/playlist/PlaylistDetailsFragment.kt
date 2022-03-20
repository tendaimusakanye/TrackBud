package com.tendai.musicx.ui.playlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.databinding.FragmentPlaylistDetailsBinding
import com.tendai.musicx.ui.BaseFragment
import com.tendai.musicx.ui.tracks.TrackAdapter
import javax.inject.Inject

class PlaylistDetailsFragment : BaseFragment<FragmentPlaylistDetailsBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<PlaylistDetailsViewModel> { viewModelFactory }
    private val adapter = TrackAdapter()
    private val args: PlaylistDetailsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.playlistDetailsComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding) {
            toolbarPlaylist.title = args.playlistName
            recyclerPlaylistTracks.adapter = adapter
        }

        viewModel.subscribe(args.playlistId)
        viewModel.tracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding =
        FragmentPlaylistDetailsBinding.inflate(inflater, container, attachToRoot)
}