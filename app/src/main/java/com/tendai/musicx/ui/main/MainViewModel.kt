package com.tendai.musicx.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tendai.common.ClientServiceConnection
import com.tendai.common.NO_ROOT
import javax.inject.Inject

class MainViewModel @Inject constructor(private val clientConnection: ClientServiceConnection) :
    ViewModel() {

    val rootId: LiveData<String> = Transformations.map(clientConnection.isConnected) { connected ->
        if (connected) clientConnection.rootMediaId
        else NO_ROOT
    }
}