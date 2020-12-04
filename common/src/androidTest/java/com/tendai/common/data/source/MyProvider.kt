package com.tendai.common.data.source

import android.database.Cursor
import android.net.Uri
import android.test.mock.MockContentProvider


public class MyProvider : MockContentProvider() {
    private lateinit var queryResult: Cursor

    fun addQueryResult(expectedResult: Cursor) {
        this.queryResult = expectedResult
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = this.queryResult
}