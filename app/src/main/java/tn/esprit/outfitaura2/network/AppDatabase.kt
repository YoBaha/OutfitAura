package tn.esprit.outfitaura2.network

import androidx.room.Database
import androidx.room.RoomDatabase
import tn.esprit.outfitaura2.models.Image

@Database(entities = [Image::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}
