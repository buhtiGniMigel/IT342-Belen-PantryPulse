package edu.cit.belen.pantrypulse

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable

// --- DATA MODELS ---
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val message: String,
    val token: String?,
    val email: String?,
    val firstName: String?
)

// Fixed field 'password' to match Spring Boot's transient password field
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

data class GoogleAuthRequest(val accessToken: String)

data class InventoryItemRequest(
    val itemName: String,
    val category: String,
    val quantity: Double,
    val expiryDate: String // YYYY-MM-DD
)

data class RecipeResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val image: String?, // image URL
    val ingredients: String?,
    val instructions: String?
) : Serializable

interface ApiService {
    @POST("/api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<ResponseBody>

    @POST("/api/auth/google")
    fun googleLogin(@Body request: GoogleAuthRequest): Call<LoginResponse>

    @GET("/api/inventory")
    fun getInventory(): Call<List<PantryItem>>

    @POST("/api/inventory")
    fun addInventoryItem(@Body item: InventoryItemRequest): Call<PantryItem>

    @PUT("/api/inventory/{id}")
    fun updateInventoryItem(@Path("id") id: Long, @Body item: InventoryItemRequest): Call<PantryItem>

    @DELETE("/api/inventory/{id}")
    fun deleteInventoryItem(@Path("id") id: Long): Call<ResponseBody>

    @GET("/api/inventory/expiring")
    fun getExpiringItems(): Call<List<PantryItem>>

    @GET("/api/recipes/suggest")
    fun getSuggestedRecipes(): Call<List<RecipeResponse>>

    companion object {
        private const val BASE_URL = "http://10.232.90.249:8080/"

        fun create(context: android.content.Context): ApiService {
            return RetrofitClient.getClient(context).create(ApiService::class.java)
        }
    }
}