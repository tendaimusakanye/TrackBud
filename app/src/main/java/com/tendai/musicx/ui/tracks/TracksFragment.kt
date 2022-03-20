package com.tendai.musicx.ui.tracks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.databinding.FragmentTracksBinding
import com.tendai.musicx.ui.BaseFragment
import javax.inject.Inject

class TracksFragment : BaseFragment<FragmentTracksBinding>() {

    companion object {
        fun newInstance() = TracksFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TracksViewModel> { viewModelFactory }
    private val adapter = TrackAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.tracksComponent().create()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewArtists.adapter = adapter

        viewModel.tracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentTracksBinding.inflate(inflater, container, attachToRoot)
}

