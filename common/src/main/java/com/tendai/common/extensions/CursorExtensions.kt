package com.tendai.common.extensions

import android.database.Cursor

//function to map cursor results to a generic list which could either be an Album ,Track , Playlist
// or Artist
fun <T> Cursor.mapList(
    mapper: T
): List<T> {
    return this.use {
        if (it.moveToFirst()) {
            val result = arrayListOf<T>()
            do {
                result.add(mapper)
            } while (it.moveToNext())
            result
        } else {
            //return an empty read-only list either way.
            listOf()
        }
    }
}