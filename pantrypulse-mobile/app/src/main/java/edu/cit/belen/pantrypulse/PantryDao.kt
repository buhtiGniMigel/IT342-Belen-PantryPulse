package edu.cit.belen.pantrypulse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY expiryDate ASC")
    fun getAllItems(): List<PantryItem>

    @Query("SELECT * FROM pantry_items WHERE id = :id LIMIT 1")
    fun getItemById(id: Long): PantryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<PantryItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: PantryItem)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    fun deleteItemById(id: Long)

    @Query("DELETE FROM pantry_items")
    fun deleteAll()
}
