package ru.pl.astronomypictureoftheday.utils

import android.os.Environment
import java.io.File

object ImageManager {
    fun getImageFullPathFile(title: String): File {
        val formattedTitle = title.replace(Regex("[: ]"), "")
        val fileName = "NasaAPOD_$formattedTitle.jpg"
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
    }

}