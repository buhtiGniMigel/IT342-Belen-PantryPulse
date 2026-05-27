package edu.cit.belen.pantrypulse

import android.content.Context
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class InventoryRepository(private val context: Context) {
    private val pantryDao = PantryDatabase.getDatabase(context).pantryDao()
    private val apiService = ApiService.create(context)

    /**
     * Gets all inventory items. If [forceRefresh] is true or the local database is empty,
     * it will query the Spring Boot API, cache the results in Room, and return them.
     * Otherwise, it returns the local cached items instantly.
     */
    fun getAllItems(forceRefresh: Boolean = false): List<PantryItem> {
        val cached = pantryDao.getAllItems()
        if (!forceRefresh && cached.isNotEmpty()) {
            return cached
        }

        try {
            // Synchronous call for repository layer (usually offloaded to background thread by caller)
            val response: Response<List<PantryItem>> = apiService.getInventory().execute()
            if (response.isSuccessful && response.body() != null) {
                val remoteItems = response.body()!!
                // Sync cache: delete all old and insert new ones
                pantryDao.deleteAll()
                pantryDao.insertAll(remoteItems)
                return remoteItems
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback to local cache if network fails
        return pantryDao.getAllItems()
    }

    /**
     * Adds an item to the backend and inserts it into local Room cache on success.
     */
    fun addItem(name: String, category: String, quantity: Double, expiryDate: String): PantryItem? {
        try {
            val request = InventoryItemRequest(name, category, quantity, expiryDate)
            val response = apiService.addInventoryItem(request).execute()
            if (response.isSuccessful && response.body() != null) {
                val created = response.body()!!
                pantryDao.insertItem(created)
                return created
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Updates an item on the backend and updates the local Room cache on success.
     */
    fun updateItem(id: Long, name: String, category: String, quantity: Double, expiryDate: String): PantryItem? {
        try {
            val request = InventoryItemRequest(name, category, quantity, expiryDate)
            val response = apiService.updateInventoryItem(id, request).execute()
            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!
                pantryDao.insertItem(updated)
                return updated
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Deletes an item from the backend and deletes it from the local Room cache.
     */
    fun deleteItem(id: Long): Boolean {
        try {
            val response = apiService.deleteInventoryItem(id).execute()
            if (response.isSuccessful) {
                pantryDao.deleteItemById(id)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getExpiringItems(): List<PantryItem> {
        try {
            val response = apiService.getExpiringItems().execute()
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Fallback: in a fully local database fallback, we could query expiring items here.
        // For local simplicity, we'll return an empty list if offline.
        return emptyList()
    }
}
