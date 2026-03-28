package edu.cit.belen.pantrypulse

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)
        val apiService = ApiService.create()

        btnRegister.setOnClickListener {
            val fName = findViewById<EditText>(R.id.etFirstName).text.toString()
            val lName = findViewById<EditText>(R.id.etLastName).text.toString()
            val email = findViewById<EditText>(R.id.etRegEmail).text.toString()
            val pass = findViewById<EditText>(R.id.etRegPassword).text.toString()

            if (fName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(fName, lName, email, pass)
            apiService.registerUser(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_LONG).show()
                        finish() // Returns to Login screen
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error: Email might exist", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Network Failure", Toast.LENGTH_SHORT).show()
                }
            })
        }

        tvBackToLogin.setOnClickListener { finish() }
    }
}