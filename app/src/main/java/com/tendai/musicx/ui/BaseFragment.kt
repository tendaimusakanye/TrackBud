package com.tendai.musicx.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<V : ViewBinding> : Fragment() {

    private var _binding: ViewBinding? =
        null  // TODO: 2/1/22 Why this fails when _binding is of type V
    protected val binding: V
        get() = _binding!! as V

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindFragment(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
        setUpObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): ViewBinding

    abstract fun setUp()

    abstract fun setUpObservers()
}
