package com.tendai.musicx.ui.album

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
import com.tendai.musicx.databinding.FragmentAlbumDetailsBinding
import com.tendai.musicx.ui.BaseFragment
import com.tendai.musicx.ui.tracks.TrackAdapter
import javax.inject.Inject

class AlbumDetailsFragment : BaseFragment<FragmentAlbumDetailsBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<AlbumDetailsViewModel> { viewModelFactory }
    private val args: AlbumDetailsFragmentArgs by navArgs()
    private val adapter = TrackAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.albumDetailsComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding) {
            recyclerViewAlbumDetails.adapter = adapter
            textviewAlbumArtist.text = args.albumArtist
            textviewAlbumName.text = args.albumName
        }

        viewModel.subscribe(args.albumId)
        viewModel.tracksInAlbum.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentAlbumDetailsBinding.inflate(inflater, container, attachToRoot)
}