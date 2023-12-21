package com.example.test

import PasswordAdapter
import PasswordItem
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var errorEditText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        errorEditText = findViewById(R.id.passListError)

        lifecycleScope.launch {
            var passwordList = getPasswords()

            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            val layoutManager = LinearLayoutManager(this@MainActivity) // Replace with your actual activity name
            recyclerView.layoutManager = layoutManager

            val adapter = PasswordAdapter(passwordList)
            recyclerView.adapter = adapter
        }
    }


    private suspend fun getPasswords(): List<PasswordItem> {
        val url = "http://10.0.2.2:8000/api/get-password-list"

        val connectTimeout = 30000L // 30 seconds
        val readTimeout = 30000L // 30 seconds

        val client = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, java.util.concurrent.TimeUnit.MILLISECONDS).build()

        // Request Body
        val mediaType = MediaType.parse("application/json")
        val requestBody = RequestBody.create(mediaType, "{}")

        // HTTP Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + getAccessToken())
            .build()

        try {
            return withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()

                    if (!responseBody.isNullOrBlank()) {
                        convertJsonToPasswordList(responseBody)
                    } else {
                        errorEditText.text = "You have no passwords saved"
                        emptyList()
                    }
                } else {
                    errorEditText.text = "Error response"
                    emptyList()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }


    private fun convertJsonToPasswordList(jsonResponse: String): List<PasswordItem> {
        val passwordList = mutableListOf<PasswordItem>()

        try {
            val jsonArray = JSONArray(jsonResponse)

            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val value = jsonObject.getString("value")

                val passwordItem = PasswordItem(name, value)
                passwordList.add(passwordItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return passwordList
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", null)
    }
}