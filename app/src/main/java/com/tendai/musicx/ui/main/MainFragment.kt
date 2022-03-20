package com.tendai.musicx.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.tendai.musicx.R
import com.tendai.musicx.databinding.FragmentMainBinding
import com.tendai.musicx.extensions.checkSelfPermissionCompat
import com.tendai.musicx.extensions.requestPermissionsCompat
import com.tendai.musicx.extensions.shouldShowRequestPermissionRationaleCompat
import com.tendai.musicx.ui.BaseFragment
import com.tendai.musicx.ui.MyApp
import com.tendai.musicx.ui.artist.ArtistFragment
import com.tendai.musicx.ui.discover.DiscoverFragment
import com.tendai.musicx.ui.tracks.TracksFragment
import javax.inject.Inject

class MainFragment : BaseFragment<FragmentMainBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var adapter: MainAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApp).appComponent.mainComponent().create()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MainAdapter(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //read permission for now
        if (checkSelfPermissionCompat(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            setUpObservers()
        } else {
            requestStoragePermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpObservers()
            } else {
                Snackbar.make(binding.root, "Error with permission", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding = FragmentMainBinding.inflate(inflater, container, attachToRoot)

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // TODO: Show rationale  first
            requestPermissionsCompat(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun setUpObservers() {
        viewModel.rootId.observe(viewLifecycleOwner) {
            binding.run {
                viewPager.adapter = adapter

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    val tabTitles = arrayOf(
                        getString(R.string.title_discover),
                        getString(R.string.title_tracks),
                        getString(R.string.title_artists)
                    )
                    tab.text = tabTitles[position]
                }.attach()
            }
        }
    }
}

private class MainAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

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

private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 2345