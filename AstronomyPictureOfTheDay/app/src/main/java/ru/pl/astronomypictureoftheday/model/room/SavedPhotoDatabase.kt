package ru.pl.astronomypictureoftheday.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.pl.astronomypictureoftheday.utils.RoomDateAdapter

@Database(version = 1, entities = [SavedPhotoDbEntity::class], exportSchema = false)
@TypeConverters(RoomDateAdapter::class)
abstract class SavedPhotoDatabase : RoomDatabase() {

    abstract fun savedPhotoDao(): SavedPhotoDao
}