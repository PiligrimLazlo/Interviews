package ru.pl.astronomypictureoftheday.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDefaultFormattedDate(): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun Fragment.setAppBarTitle(title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}