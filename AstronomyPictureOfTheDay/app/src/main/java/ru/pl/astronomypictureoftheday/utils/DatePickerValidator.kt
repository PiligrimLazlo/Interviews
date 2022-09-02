package ru.pl.astronomypictureoftheday.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.*
import kotlin.math.max

class DatePickerValidator: CalendarConstraints.DateValidator {

    val minDateLong = GregorianCalendar(1995, 6, 16).time.time
    val maxDateLong = Calendar.getInstance().timeInMillis

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun isValid(date: Long): Boolean {
        return date in minDateLong until maxDateLong
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<DatePickerValidator> {
            override fun createFromParcel(source: Parcel?): DatePickerValidator {
                return DatePickerValidator()
            }

            override fun newArray(size: Int): Array<DatePickerValidator> {
                return arrayOf<DatePickerValidator>()
            }

        }
    }
}