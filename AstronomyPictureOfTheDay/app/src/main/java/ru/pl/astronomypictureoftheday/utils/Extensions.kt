package ru.pl.astronomypictureoftheday.utils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.pl.astronomypictureoftheday.R
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDefaultFormattedDate(): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun Fragment.setAppBarTitle(title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.showAlertDialog(title: String, message: String) {
    AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
        .setMessage(message)
        .setTitle(title)
        .setCancelable(false)
        .setPositiveButton(
            "OK"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}