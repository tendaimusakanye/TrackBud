package com.tendai.common.extensions

import android.database.Cursor

//function to map cursor results to a generic list which could either be an Album ,Track , Playlist
// or Artist
 fun <T> Cursor.mapList(
    cursor: Cursor,
    mapper: T
): List<T> {
    val result = arrayListOf<T>()
    return cursor.use {
        if (cursor.moveToFirst()) {
            do {
                result.add(mapper)
            } while (cursor.moveToNext())
            result
        } else {
            //return an empty arrayList either way.
            result
        }
    }
}