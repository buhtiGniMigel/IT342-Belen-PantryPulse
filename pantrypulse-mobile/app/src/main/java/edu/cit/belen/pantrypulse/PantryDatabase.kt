package edu.cit.belen.pantrypulse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PantryItem::class], version = 1, exportSchema = false)
abstract class PantryDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao

    companion object {
        @Volatile
        private var INSTANCE: PantryDatabase? = null

        fun getDatabase(context: Context): PantryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PantryDatabase::class.java,
                    "pantry_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
