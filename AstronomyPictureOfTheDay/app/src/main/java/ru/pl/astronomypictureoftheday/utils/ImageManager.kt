package ru.pl.astronomypictureoftheday.utils

import android.os.Environment
import java.io.File

object ImageManager {
    fun getImageFullPathFile(title: String): File {
        val fileName = "NasaAPOD_$title.jpg"
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
    }

}