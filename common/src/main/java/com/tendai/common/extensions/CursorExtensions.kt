package com.tendai.common.extensions

import android.database.Cursor

//function to map cursor results to a generic list which could either be an Album ,Track , Playlist
// or Artist
// Seems as if a lambda is lazily invoked. So passing in  function is pretty sweet.
// appropriate results compared to the jvm implementation
fun <T> Cursor.mapList(
    mapper: (cursor: Cursor)-> T
): List<T> {
    val result = arrayListOf<T>()
    return this.use {
        if (this.moveToFirst()) {
            this.run {
                do {
                    result.add(mapper.invoke(this))
                } while (this.moveToNext())
            }
            result
        } else {
            //return an empty arrayList either way.
            listOf()
        }
    }
}