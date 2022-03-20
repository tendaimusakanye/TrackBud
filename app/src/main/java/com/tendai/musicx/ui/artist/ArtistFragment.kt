package com.tendai.musicx.ui.artist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.databinding.FragmentArtistBinding
import com.tendai.musicx.ui.BaseFragment
import javax.inject.Inject

class ArtistFragment : BaseFragment<FragmentArtistBinding>() {

    companion object {
        fun newInstance() = ArtistFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ArtistViewModel> { viewModelFactory }
    private val adapter = ArtistAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.artistComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewArtists.adapter = adapter

        viewModel.artists.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentArtistBinding.inflate(inflater, container, attachToRoot)
}