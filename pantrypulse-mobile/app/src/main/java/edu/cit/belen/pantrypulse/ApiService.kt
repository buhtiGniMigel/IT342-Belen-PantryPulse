package edu.cit.belen.pantrypulse

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- DATA MODELS ---
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val message: String)

// This matches your Spring Boot User entity fields
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: String
)

interface ApiService {
    @POST("/api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<ResponseBody>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}