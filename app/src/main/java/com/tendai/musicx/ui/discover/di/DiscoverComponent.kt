package com.tendai.musicx.ui.discover.di

import com.tendai.musicx.ui.discover.DiscoverFragment
import dagger.Subcomponent

@Subcomponent(modules = [DiscoverModule::class])
interface DiscoverComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): DiscoverComponent
    }

    fun inject(fragment: DiscoverFragment)
}