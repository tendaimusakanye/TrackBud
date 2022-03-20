package com.tendai.musicx.ui

import android.app.Application
import com.tendai.musicx.di.AppComponent
import com.tendai.musicx.di.DaggerAppComponent

class MyApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}