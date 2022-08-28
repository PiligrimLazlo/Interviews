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
import javax.inject.Inject

class ImageManager @Inject constructor() {

    //user's saved photos
    fun getPublicImageFullPathFile(title: String): File {
        val fileName = getFormattedFileName(title)
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
    }

    //room caches photos
    fun getInternalImageFullPathFile(title: String, filesDir: File): File {
        val fileName = getFormattedFileName(title)
        return File(filesDir, fileName)
    }

    private fun getFormattedFileName(title: String): String {
        val formattedTitle = title.replace(Regex("[: ]"), "")
        return "NasaAPOD_$formattedTitle.jpg"
    }

    //todo Проблема: при ручном удалении фото с реального устройства (сяоми)
    //todo фото остается в памяти (в корзине), но метод File.exist() возвращает false.
    //todo И запись по этому пути выдает exception
    //todo Добавить временнУю метку к каждому фото при сохранении
    //todo Далее чтобы проверить лежит ли такая фотка в памяти, нужно проходить по всем фоткам
    //todo и сравнивать по регулярке что-то типо "имя файла, кроме цифр времени в конце"
    fun savePhoto(urlSource: String, absPathToSave: File): Bitmap? {
        var bitmap = loadPhotoFromCache(absPathToSave)
        if (bitmap == null) {
            bitmap = loadBitmapFromNet(urlSource)
            if (bitmap != null) {
                FileOutputStream(absPathToSave).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                }
            }

        }
        return bitmap
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

    fun scaleBitmapForWallpapers(bitmap: Bitmap): ScaledImageData {
        var bitmapScaled = bitmap
        val wallpaperHeight = Resources.getSystem().displayMetrics.heightPixels
        val wallpaperWidth = Resources.getSystem().displayMetrics.widthPixels

        val widthFactor = wallpaperWidth.toDouble() / bitmapScaled.width
        val heightFactor = wallpaperHeight.toDouble() / bitmapScaled.height
        //scale (grow) bitmap if it smaller than screen
        if (bitmapScaled.width < wallpaperWidth || bitmapScaled.height < wallpaperHeight) {
            if (widthFactor > heightFactor) {
                val newBitmapHeight = (bitmapScaled.height * widthFactor).toInt()
                bitmapScaled = bitmapScaled.scale(wallpaperWidth, newBitmapHeight, false)
            } else {
                val newBitmapWidth = (bitmapScaled.width * heightFactor).toInt()
                bitmapScaled = bitmapScaled.scale(newBitmapWidth, wallpaperHeight, false)
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
        return ScaledImageData(bitmapScaled, Rect(start.x, start.y, end.x, end.y))
    }
}

data class ScaledImageData(
    val bitmap: Bitmap,
    val rect: Rect
)