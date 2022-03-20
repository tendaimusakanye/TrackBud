package com.tendai.musicx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.tendai.musicx.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
// TODO: Make app color status bar
// TODO: Rename package musicx to track bud