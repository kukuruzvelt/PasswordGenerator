package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.util.Log
import android.widget.TextView
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
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
            }
            else{
                errorEditText.text = ""
                makeLoginRequest(username, password)
            }
        }
    }

    private fun makeLoginRequest(username: String, password: String) {
        val url = "https://10.0.2.2/login"

        val client = OkHttpClient();

        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Use coroutines for asynchronous networking
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // Use the safe call operator ?. to handle nullability
                    SSLUtils.disableSSLCertificateChecking()
                    val responseBody = response.body()?.string()

                    // Check if the response body is not null before proceeding
                    if (!responseBody.isNullOrBlank()) {
                        val json = JSONObject(responseBody)
                        val token = json.getString("jwt_token")

                        // Save the token as needed (e.g., in SharedPreferences)
                        saveToken(token)

                        // Perform any additional actions with the token or navigate to the next screen
                        // Once the token is saved, you can navigate the user to the main activity
                        navigateToMainActivity()
                    } else {
                        // Handle the case where the response body is null or blank
                        // ...
                        errorEditText.text = "No response"
                    }
                } else {
                    // Handle unsuccessful response
                    // ...
                    errorEditText.text = "Error response"
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

    object SSLUtils {
        fun disableSSLCertificateChecking() {
            try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                            return arrayOf()
                        }
                    }
                )

                val sc = SSLContext.getInstance("SSL")
                sc.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
                HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
    }

}
