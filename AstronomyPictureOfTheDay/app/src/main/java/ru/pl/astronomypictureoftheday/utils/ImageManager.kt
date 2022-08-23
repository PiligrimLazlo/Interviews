package ru.pl.astronomypictureoftheday.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.Environment
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ImageManager {

    //user's saved photos
    fun getPublicImageFullPathFile(title: String): File {
        val formattedTitle = title.replace(Regex("[: ]"), "")
        val fileName = "NasaAPOD_$formattedTitle.jpg"
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
    }

    //room caches photos
    fun getInternalImageFullPathFile(title: String, filesDir: File): File {
        val formattedTitle = title.replace(Regex("[: ]"), "")
        val fileName = "NasaAPOD_$formattedTitle.jpg"
        return File(filesDir, fileName)
    }

    fun savePhoto(urlSource: String, absPathToSave: File) {
        var bitmap = loadPhotoFromCache(absPathToSave)
        if (bitmap == null) {
            bitmap = loadBitmapFromNet(urlSource)
            if (bitmap != null)
                FileOutputStream(absPathToSave).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                }
        }

    }

    fun deletePhoto(absPathToSave: File) {
        if (absPathToSave.exists()) {
            absPathToSave.delete()
        }
    }

    fun loadBitmapFromNet(urlSource: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val urlEntity = URL(urlSource)
            val inputStream = urlEntity.openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }


    fun loadPhotoFromCache(absPath: File): Bitmap? {
        return if (absPath.exists()) {
            BitmapFactory.decodeFile(absPath.absolutePath)
        } else {
            null
        }
    }

    fun scaleBitmapForWallpapers(bitmap: Bitmap): Rect {
        var bitmapScaled = bitmap
        val wallpaperHeight = Resources.getSystem().displayMetrics.heightPixels
        val wallpaperWidth = Resources.getSystem().displayMetrics.widthPixels

        val widthFactor = wallpaperWidth.toDouble() / bitmapScaled.width
        val heightFactor = wallpaperHeight.toDouble() / bitmapScaled.height
        //scale (grow) bitmap if it smaller than screen
        if (bitmapScaled.width < wallpaperWidth || bitmapScaled.height < wallpaperHeight) {
            bitmapScaled = if (widthFactor > heightFactor) {
                val newBitmapHeight = (bitmapScaled.height * widthFactor).toInt()
                bitmapScaled.scale(wallpaperWidth, newBitmapHeight, false)
            } else {
                val newBitmapWidth = (bitmapScaled.width * heightFactor).toInt()
                bitmapScaled.scale(newBitmapWidth, wallpaperHeight, false)
            }
        }
        //center cropping big image
        val start = Point(0, 0)
        val end = Point(bitmapScaled.width, bitmapScaled.height)

        if (bitmapScaled.width > wallpaperWidth) {
            start.x = (bitmapScaled.width - wallpaperWidth) / 2
            end.x = start.x + wallpaperWidth
        }
        if (bitmapScaled.height > wallpaperHeight) {
            start.y = (bitmapScaled.height - wallpaperHeight) / 2
            end.y = start.y + wallpaperHeight
        }
        return Rect(start.x, start.y, end.x, end.y)
    }

}