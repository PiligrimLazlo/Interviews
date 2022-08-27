package ru.pl.astronomypictureoftheday.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.pl.astronomypictureoftheday.utils.RoomDateAdapter

@Database(version = 1, entities = [PhotoDbModel::class], exportSchema = false)
@TypeConverters(RoomDateAdapter::class)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDbDao
}