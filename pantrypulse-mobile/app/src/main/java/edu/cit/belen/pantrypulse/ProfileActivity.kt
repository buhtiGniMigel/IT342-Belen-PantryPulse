package edu.cit.belen.pantrypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var btnProfileLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        btnProfileLogout = findViewById(R.id.btnProfileLogout)

        // Read cached user credentials
        val sharedPrefs = getSharedPreferences("PantryPulsePrefs", Context.MODE_PRIVATE)
        val firstName = sharedPrefs.getString("firstName", "PantryPulse User")
        val email = sharedPrefs.getString("email", "user@pantrypulse.com")

        tvProfileName.text = firstName
        tvProfileEmail.text = email

        btnProfileLogout.setOnClickListener {
            // Wipes session cache completely!
            val editor = sharedPrefs.edit()
            editor.clear()
            editor.apply()

            // Also sign out from Google Play Services!
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                GoogleSignIn.getClient(this, gso).signOut()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Redirect back to Login screen (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
