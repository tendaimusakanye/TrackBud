package com.tendai.musicx.ui.artist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.tendai.common.EXTRA_ARTIST_ID
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.databinding.FragmentArtistTracksBinding
import com.tendai.musicx.ui.BaseFragment
import com.tendai.musicx.ui.tracks.TrackAdapter
import javax.inject.Inject

class ArtistTracksFragment : BaseFragment<FragmentArtistTracksBinding>() {

    companion object {
        fun newInstance(artistId: Long): ArtistTracksFragment {
            val bundle = Bundle().apply {
                putLong(EXTRA_ARTIST_ID, artistId)
            }
            return ArtistTracksFragment().apply {
                arguments = bundle
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ArtistTracksViewModel> { viewModelFactory }
    private val adapter = TrackAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.artistTracksComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewArtistTracks.adapter = adapter

        val artistId = requireArguments().getLong(EXTRA_ARTIST_ID)

        viewModel.subscribe(artistId)
        viewModel.artistTracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding =
        FragmentArtistTracksBinding.inflate(inflater, container, attachToRoot)
}