package ru.pl.astronomypictureoftheday.data.room

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.pl.astronomypictureoftheday.utils.RoomDateAdapter

@Database(version = 1, entities = [PhotoDbModel::class], exportSchema = false)
@TypeConverters(RoomDateAdapter::class)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDbDao


    companion object {
        @Volatile
        private var INSTANCE: PhotoDatabase? = null

        fun getInstance(application: Application): PhotoDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application,
                    PhotoDatabase::class.java,
                    "favorite_photos_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

    }
}