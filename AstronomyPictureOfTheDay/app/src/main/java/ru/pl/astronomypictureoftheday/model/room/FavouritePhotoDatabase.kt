package ru.pl.astronomypictureoftheday.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.utils.RoomDateAdapter

@Database(version = 1, entities = [FavouritePhoto::class], exportSchema = false)
@TypeConverters(RoomDateAdapter::class)
abstract class FavouritePhotoDatabase : RoomDatabase() {

    abstract fun favouritePhotoDao(): FavouritePhotoDao
}