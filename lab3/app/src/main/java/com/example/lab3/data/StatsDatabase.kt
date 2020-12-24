package com.example.lab3.data

import android.content.Context
import androidx.room.*

@TypeConverters(DateConverter::class)
@Database(entities = [StatsEntity::class], version = 1)
abstract class StatsDatabase : RoomDatabase() {
    abstract fun getDao(): StatsDao
    companion object {
        @Volatile
        private var INSTANCE: StatsDatabase? = null

        fun getDatabase(context: Context): StatsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StatsDatabase::class.java,
                    "lab3statsdb"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}