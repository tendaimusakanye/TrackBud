package com.tendai.common.extensions

import android.database.Cursor

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
            result
        }
    }
}