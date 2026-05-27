package edu.cit.belen.pantrypulse

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val colorError = Color.parseColor("#EF4444")
    private val colorErrorLight = Color.parseColor("#FEF2F2")
    private val colorErrorText = Color.parseColor("#991B1B")
    private val colorSuccess = Color.parseColor("#10B981")
    private val colorSuccessLight = Color.parseColor("#F0FDF4")
    private val colorSuccessText = Color.parseColor("#166534")

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleLogin: Button
    private lateinit var tvRegisterLink: TextView
    private lateinit var llMessage: LinearLayout
    private lateinit var tvMessage: TextView

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Automatic login if token already exists!
        val sharedPrefs = getSharedPreferences("PantryPulsePrefs", Context.MODE_PRIVATE)
        if (sharedPrefs.getString("token", null) != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)
        llMessage = findViewById(R.id.llMessage)
        tvMessage = findViewById(R.id.tvMessage)

        llMessage.visibility = View.GONE

        // Server Settings Gear Icon Setup
        val ivSettings: ImageView = findViewById(R.id.ivSettings)
        ivSettings.setOnClickListener {
            val serverPrefs = getSharedPreferences("PantryPulsePrefs", Context.MODE_PRIVATE)
            val currentUrl = serverPrefs.getString("server_url", "http://10.232.90.249:8080")

            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(60, 40, 60, 20)
            }
            val input = EditText(this).apply {
                setText(currentUrl)
                setSingleLine(true)
                hint = "http://10.232.90.249:8080"
            }
            container.addView(input)

            AlertDialog.Builder(this)
                .setTitle("Configure Server URL")
                .setMessage("Enter your backend server's base URL (e.g. your host computer's IP address like http://10.232.90.249:8080):")
                .setView(container)
                .setPositiveButton("Save") { _, _ ->
                    val newUrl = input.text.toString().trim()
                    if (newUrl.isNotEmpty()) {
                        serverPrefs.edit().putString("server_url", newUrl).apply()
                        showSuccess("Server URL updated successfully!")
                    }
                }
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Reset Default") { _, _ ->
                    serverPrefs.edit().putString("server_url", "http://10.232.90.249:8080").apply()
                    showSuccess("Reset to default server URL.")
                }
                .show()
        }

        // Google Sign-In configuration
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("139752023449-c3nslgtpghn017tgg1qd8ahs6ubkopao.apps.googleusercontent.com") // your web client ID
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }

            setLoading(true)
            val request = LoginRequest(email, password)

            val currentApiService = ApiService.create(this)
            currentApiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    setLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        
                        // Cache authentication data in SharedPreferences!
                        val editor = sharedPrefs.edit()
                        editor.putString("token", body.token ?: "mock-jwt-token")
                        editor.putString("email", body.email ?: email)
                        editor.putString("firstName", body.firstName ?: "User")
                        editor.apply()

                        showSuccess("Login Successful! Redirecting...")
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        }, 1500)
                    } else {
                        showError("Invalid credentials or unverified email")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    setLoading(false)
                    showError("Network Error: ${t.localizedMessage ?: "Connection failed"}")
                }
            })
        }

        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    handleGoogleSignInResult(account)
                } else {
                    showError("Google authentication failed")
                }
            } catch (e: ApiException) {
                showError("Google Sign-In Error: ${e.localizedMessage ?: "Failed"}")
            }
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        setLoading(true)
        
        // Fetch OAuth access token in a background thread
        Thread {
            try {
                val scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"
                val accessToken = GoogleAuthUtil.getToken(this@MainActivity, account.account!!, scope)
                
                runOnUiThread {
                    val apiService = ApiService.create(this@MainActivity)
                    apiService.googleLogin(GoogleAuthRequest(accessToken)).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            setLoading(false)
                            if (response.isSuccessful && response.body() != null) {
                                val body = response.body()!!
                                val sharedPrefs = getSharedPreferences("PantryPulsePrefs", Context.MODE_PRIVATE)
                                val editor = sharedPrefs.edit()
                                editor.putString("token", body.token ?: "mock-jwt-token")
                                editor.putString("email", body.email ?: account.email)
                                editor.putString("firstName", body.firstName ?: account.givenName)
                                editor.apply()

                                showSuccess("Google Sign-in Successful!")
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                                    finish()
                                }, 1500)
                            } else {
                                showError("OAuth link failed on server: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            setLoading(false)
                            showError("Google auth sync failed: ${t.localizedMessage}")
                        }
                    })
                }
            } catch (e: Exception) {
                runOnUiThread {
                    setLoading(false)
                    showError("Failed to retrieve Google token: ${e.localizedMessage}")
                }
            }
        }.start()
    }

    private fun setLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnGoogleLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Signing in..." else "Sign In"
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