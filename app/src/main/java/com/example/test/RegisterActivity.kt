package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var initialsEditText: EditText
    private lateinit var errorEditText: TextView
    private lateinit var registerButton: Button

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI elements
        usernameEditText = findViewById(R.id.editTextUsername)
        initialsEditText = findViewById(R.id.editTextInitials)
        passwordEditText = findViewById(R.id.editTextPassword)
        errorEditText = findViewById(R.id.textViewError)
        registerButton = findViewById(R.id.buttonRegister)


        // Set click listener for the login button
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val initials = initialsEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty() || initials.isEmpty()) {
                // Display an error message
                errorEditText.text = "Username, initials or password cannot be empty"
            } else {
                errorEditText.text = ""
                makeRegisterRequest(username, password, initials)
            }
        }
    }

    private fun makeRegisterRequest(username: String, password: String, initials: String) {
        val url = "http://10.0.2.2:8000/api/register"

        val connectTimeout = 30000L // 30 seconds
        val readTimeout = 30000L // 30 seconds

        val client = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, java.util.concurrent.TimeUnit.MILLISECONDS).build()

        // Request Body
        val mediaType = MediaType.parse("application/json")
        val requestBody = RequestBody.create(
            mediaType,
            "{\"email\":\"$username\",\"initials\":\"$initials\",\"password\":\"$password\"}"
        )

        // HTTP Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            GlobalScope.launch(Dispatchers.IO) {

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    navigateToLoginActivity()
                } else {
                    errorEditText.text = "Error response"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the current activity if you don't want users to navigate back to it using the back button
    }
}