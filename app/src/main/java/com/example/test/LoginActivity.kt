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
    private lateinit var registerTextView: TextView

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI elements
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        errorEditText = findViewById(R.id.textViewError)
        loginButton = findViewById(R.id.buttonLogin)
        registerTextView = findViewById(R.id.textViewRegister)

        registerTextView.setOnClickListener {
            navigateToRegisterActivity()
        }

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
        val url = "http://10.0.2.2/login"

        val connectTimeout = 30000L // 30 seconds
        val readTimeout = 30000L // 30 seconds

        val client = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, java.util.concurrent.TimeUnit.MILLISECONDS).build()

        // Request Body
        val mediaType = MediaType.parse("application/json")
        val requestBody = RequestBody.create(mediaType, "{\"email\":\"$username\",\"password\":\"$password\"}")

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
                val responseBody = response.body()?.string()
                val json = JSONObject(responseBody)

                if (response.isSuccessful) {

                    if (!responseBody.isNullOrBlank()) {

                        val token = json.getString("token")

                        saveToken(token)
                        navigateToMainActivity()
                    } else {
                        errorEditText.text = json.getString("message")
                    }
                } else {
                    errorEditText.text = json.getString("message")
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

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the current activity if you don't want users to navigate back to it using the back button
    }
}
