package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.FontItem

@Database(entities = [FontItem::class], version = 1, exportSchema = false)
abstract class FontDatabase : RoomDatabase() {

    abstract fun fontDao(): FontDao

    companion object {
        @Volatile
        private var INSTANCE: FontDatabase? = null

        fun getDatabase(context: Context): FontDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FontDatabase::class.java,
                    "fontx_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
