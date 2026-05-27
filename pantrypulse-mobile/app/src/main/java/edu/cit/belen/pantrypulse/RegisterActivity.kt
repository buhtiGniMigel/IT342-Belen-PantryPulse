package edu.cit.belen.pantrypulse

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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val colorError = Color.parseColor("#EF4444")
    private val colorErrorLight = Color.parseColor("#FEF2F2")
    private val colorErrorText = Color.parseColor("#991B1B")
    private val colorSuccess = Color.parseColor("#10B981")
    private val colorSuccessLight = Color.parseColor("#F0FDF4")
    private val colorSuccessText = Color.parseColor("#166534")

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etRegEmail: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvBackToLogin: TextView
    private lateinit var llMessage: LinearLayout
    private lateinit var tvMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
        llMessage = findViewById(R.id.llMessage)
        tvMessage = findViewById(R.id.tvMessage)

        llMessage.visibility = View.GONE

        btnRegister.setOnClickListener {
            val fName = etFirstName.text.toString().trim()
            val lName = etLastName.text.toString().trim()
            val email = etRegEmail.text.toString().trim()
            val pass = etRegPassword.text.toString().trim()

            when {
                fName.isEmpty() -> {
                    showError("Please enter your first name")
                    return@setOnClickListener
                }
                lName.isEmpty() -> {
                    showError("Please enter your last name")
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    showError("Please enter your email address")
                    return@setOnClickListener
                }
                pass.length < 8 -> {
                    showError("Password must be at least 8 characters")
                    return@setOnClickListener
                }
            }

            setLoading(true)
            val request = RegisterRequest(fName, lName, email, pass)

            val currentApiService = ApiService.create(this)
            currentApiService.registerUser(request).enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        showSuccess("Registration Successful! Please check your email to verify.")
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 2500)
                    } else {
                        when (response.code()) {
                            409 -> showError("Email already exists")
                            400 -> showError("Invalid data provided")
                            else -> showError("Registration failed: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    setLoading(false)
                    showError("Network Error: ${t.localizedMessage ?: "Connection failed"}")
                }
            })
        }

        tvBackToLogin.setOnClickListener { finish() }
    }

    private fun setLoading(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        btnRegister.text = if (isLoading) "Creating account..." else "Create Account"
        if (isLoading) hideMessage()
    }

    private fun showError(message: String) {
        llMessage.apply {
            visibility = View.VISIBLE
            setBackgroundColor(colorErrorLight)
        }
        tvMessage.apply {
            text = message
            setTextColor(colorErrorText)
        }
        val iconContainer = llMessage.getChildAt(0) as FrameLayout
        iconContainer.setBackgroundColor(colorError)
        (iconContainer.getChildAt(0) as TextView).text = "×"
    }

    private fun showSuccess(message: String) {
        llMessage.apply {
            visibility = View.VISIBLE
            setBackgroundColor(colorSuccessLight)
        }
        tvMessage.apply {
            text = message
            setTextColor(colorSuccessText)
        }
        val iconContainer = llMessage.getChildAt(0) as FrameLayout
        iconContainer.setBackgroundColor(colorSuccess)
        (iconContainer.getChildAt(0) as TextView).text = "✓"
    }

    private fun hideMessage() {
        llMessage.visibility = View.GONE
    }
}