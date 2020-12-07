package com.example.lab2.db

import android.content.Context
import androidx.room.*

@TypeConverters(Converters::class)
@Database(entities = [Sequence::class, Interval::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): MainDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lab2db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}