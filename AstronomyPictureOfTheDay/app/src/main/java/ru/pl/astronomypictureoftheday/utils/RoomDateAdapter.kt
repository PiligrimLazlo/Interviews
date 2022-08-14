package ru.pl.astronomypictureoftheday.utils

import androidx.room.TypeConverter
import java.util.*

class RoomDateAdapter {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}