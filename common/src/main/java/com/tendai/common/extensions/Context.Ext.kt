package com.tendai.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import com.tendai.common.R
import com.tendai.common.source.local.getAlbumArtUri
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

fun Context.getAlbumArt(albumId: Long): Bitmap {
    var inputStream: InputStream? = null
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, getAlbumArtUri(albumId))
            return ImageDecoder.decodeBitmap(source)
        }
        inputStream = contentResolver.openInputStream(getAlbumArtUri(albumId))
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        return BitmapFactory.decodeResource(resources, R.drawable.ic_placeholder_art)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        return BitmapFactory.decodeResource(resources, R.drawable.ic_placeholder_art)
    } finally {
        inputStream?.close()
    }
}