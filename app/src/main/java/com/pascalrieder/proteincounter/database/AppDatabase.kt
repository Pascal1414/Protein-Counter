package com.pascalrieder.proteincounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pascalrieder.proteincounter.database.dao.DayDao
import com.pascalrieder.proteincounter.database.dao.ItemDao
import com.pascalrieder.proteincounter.database.models.Day
import com.pascalrieder.proteincounter.database.models.DayItem
import com.pascalrieder.proteincounter.database.models.converters.DateConverter
import com.pascalrieder.proteincounter.database.models.Item

@Database(entities = [Item::class,DayItem::class, Day::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun dayDao(): DayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "item_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
