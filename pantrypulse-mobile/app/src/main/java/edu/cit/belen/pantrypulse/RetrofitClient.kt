package edu.cit.belen.pantrypulse

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var cachedUrl: String? = null

    fun getClient(context: Context): Retrofit {
        val sharedPrefs = context.getSharedPreferences("PantryPulsePrefs", Context.MODE_PRIVATE)
        val serverUrl = sharedPrefs.getString("server_url", "http://10.232.90.249:8080") ?: "http://10.232.90.249:8080"
        
        // Ensure trailing slash for Retrofit base URL requirements
        val formattedUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"

        if (retrofit == null || cachedUrl != formattedUrl) {
            cachedUrl = formattedUrl
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    
                    // Retrieve JWT token stored in SharedPreferences
                    val token = sharedPrefs.getString("token", null)

                    val requestBuilder = original.newBuilder()
                    if (!token.isNullOrEmpty()) {
                        // Automatically attach JWT token for all authenticated endpoints!
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
                    
                    val request = requestBuilder
                        .method(original.method(), original.body())
                        .build()
                    chain.proceed(request)
                }
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(formattedUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}

