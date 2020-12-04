package com.tendai.common.extensions

import com.tendai.common.data.source.local.DataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun <reified T> retrieveAlbum(
    scope: CoroutineScope,
    int: Int,
    albumDataSource: DataSource.Albums
): T? {
    var var1: T? = null
    return if (var1 == null) {
        scope.launch {
            var1 = albumDataSource.getAlbum(int) as? T
        }
        var1
    } else {
        var1
    }
}