// MainActivity.kt
package edu.cit.belen.pantrypulse

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // PantryPulse Color Palette
    private val colorError = Color.parseColor("#EF4444")
    private val colorErrorLight = Color.parseColor("#FEF2F2")
    private val colorErrorText = Color.parseColor("#991B1B")
    private val colorSuccess = Color.parseColor("#10B981")
    private val colorSuccessLight = Color.parseColor("#F0FDF4")
    private val colorSuccessText = Color.parseColor("#166534")
    private val colorPrimary = Color.parseColor("#2E7D32")

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterLink: TextView
    private lateinit var llMessage: LinearLayout
    private lateinit var tvMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)
        llMessage = findViewById(R.id.llMessage)
        tvMessage = findViewById(R.id.tvMessage)

        // Hide message container initially
        llMessage.visibility = View.GONE

        val apiService = ApiService.create()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }

            // Show loading state
            setLoading(true)

            val request = LoginRequest(email, password)

            apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        showSuccess("Login Successful! Redirecting...")

                        // Delay navigation to show success message before switching screens
                        Handler(Looper.getMainLooper()).postDelayed({
                            // --- NAVIGATION UPDATED HERE ---
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Closes Login screen so user can't "back" into it
                        }, 1500)
                    } else {
                        when (response.code()) {
                            401 -> showError("Invalid credentials")
                            403 -> showError("Account is locked")
                            404 -> showError("User not found")
                            else -> showError("Login failed: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    setLoading(false)
                    showError("Network Error: ${t.message ?: "Please check your connection"}")
                }
            })
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Signing in..." else "Sign In"

        if (isLoading) {
            hideMessage()
        }
    }

    /**
     * Shows error alert using PantryPulse Error/Expired color (#EF4444)
     */
    private fun showError(message: String) {
        llMessage.apply {
            visibility = View.VISIBLE
            setBackgroundColor(colorErrorLight)
        }

        tvMessage.apply {
            text = message
            setTextColor(colorErrorText)
        }

        // Update icon container to red with X
        val iconContainer = llMessage.getChildAt(0) as FrameLayout
        iconContainer.setBackgroundColor(colorError)
        (iconContainer.getChildAt(0) as TextView).text = "×"
    }

    /**
     * Shows success alert using PantryPulse Success/Fresh color (#10B981)
     */
    private fun showSuccess(message: String) {
        llMessage.apply {
            visibility = View.VISIBLE
            setBackgroundColor(colorSuccessLight)
        }

        tvMessage.apply {
            text = message
            setTextColor(colorSuccessText)
        }

        // Update icon container to green with checkmark
        val iconContainer = llMessage.getChildAt(0) as FrameLayout
        iconContainer.setBackgroundColor(colorSuccess)
        (iconContainer.getChildAt(0) as TextView).text = "✓"
    }

    private fun hideMessage() {
        llMessage.visibility = View.GONE
    }
}