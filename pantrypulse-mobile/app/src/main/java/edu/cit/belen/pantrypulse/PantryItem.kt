package edu.cit.belen.pantrypulse

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey val id: Long,
    val itemName: String,
    val category: String,
    val quantity: Double,
    val expiryDate: String, // format: YYYY-MM-DD
    val status: String // Fresh, Expiring, Expired
) : Serializable
