package com.tendai.musicx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tendai.musicx.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}

// TODO: Rename package musicx to trackbud