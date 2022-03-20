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
import com.tendai.musicx.databinding.FragmentArtistAlbumsBinding
import com.tendai.musicx.ui.BaseFragment
import javax.inject.Inject

class ArtistAlbumsFragment : BaseFragment<FragmentArtistAlbumsBinding>() {

    companion object {
        fun newInstance(artistId: Long): ArtistAlbumsFragment {
            val bundle = Bundle().apply {
                putLong(EXTRA_ARTIST_ID, artistId)
            }
            return ArtistAlbumsFragment().apply {
                arguments = bundle
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ArtistAlbumsViewModel> { viewModelFactory }
    private val adapter = ArtistAlbumsAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.artistAlbumsComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewArtistAlbums.adapter = adapter

        val artistId = requireArguments().getLong(EXTRA_ARTIST_ID)

        viewModel.subscribe(artistId)
        viewModel.artistAlbums.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding =
        FragmentArtistAlbumsBinding.inflate(inflater, container, attachToRoot)
}