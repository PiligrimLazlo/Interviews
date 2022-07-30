package ru.pl.astronomypictureoftheday.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.toDefaultFormattedDate(): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(this)
}