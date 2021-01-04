package com.tendai.common.extensions

import android.database.Cursor

//function to map cursor results to a generic list which could either be an Album ,Track , Playlist
// or Artist
// Passing a lambda as it is lazily invoked like a callback/ Anonymous inner class. So passing in
// function returns the appropriate results
// appropriate results compared to the jvm implementation
fun <T> Cursor.mapList(
    mapper: (cursor: Cursor) -> T
): List<T> {
    val result = arrayListOf<T>()
    return this.use {
        if (this.moveToFirst()) {
            do {
                result.add(mapper.invoke(this))
            } while (this.moveToNext())
            result
        } else {
            //return an empty readOnlyList either way.
            listOf()
        }
    }
}