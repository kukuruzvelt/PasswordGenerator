package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorEditText: TextView
    private lateinit var loginButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v("Tag", "ON CREATE LOGIN")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI elements
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        errorEditText = findViewById(R.id.textViewError)
        loginButton = findViewById(R.id.buttonLogin)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                // Display an error message
                errorEditText.text = "Username or password cannot be empty"
            } else {
                errorEditText.text = ""
                makeLoginRequest(username, password)
            }
        }
    }

    private fun makeLoginRequest(username: String, password: String) {
        val url = "http://192.168.50.205/api/docs"

        val client = OkHttpClient.Builder().build()

        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            GlobalScope.launch(Dispatchers.IO) {

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()

                    if (!responseBody.isNullOrBlank()) {
                        val json = JSONObject(responseBody)
                        val token = json.getString("token")

                        saveToken(token)
                        navigateToMainActivity()
                    } else {
                        errorEditText.text = "No response"
                    }
                } else {
                    errorEditText.text = "Error response"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the current activity if you don't want users to navigate back to it using the back button
    }
}
