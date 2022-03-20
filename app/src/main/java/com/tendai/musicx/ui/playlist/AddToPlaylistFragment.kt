package com.tendai.musicx.ui.playlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.databinding.FragmentAddToPlaylistBinding
import com.tendai.musicx.ui.tracks.TrackAdapter
import javax.inject.Inject

class AddToPlaylistFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<AddToPlaylistViewModel> { viewModelFactory }
    private val adapter = TrackAdapter()
    private val binding get() = _binding!!

    private var _binding: FragmentAddToPlaylistBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.addToPlaylistComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewAddToPlaylist.adapter = adapter

        viewModel.tracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

// TODO: Move Playlist package into discover