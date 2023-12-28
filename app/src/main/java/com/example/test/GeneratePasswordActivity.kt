package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
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

class GeneratePasswordActivity : AppCompatActivity() {

    private lateinit var algorithmSpinner: Spinner
    private lateinit var passwordNameEditText: EditText
    private lateinit var passwordLengthSeekBar: SeekBar
    private lateinit var passwordLengthLabel: TextView
    private lateinit var includeUppercaseCheckbox: CheckBox
    private lateinit var includeLowercaseCheckbox: CheckBox
    private lateinit var includeNumbersCheckbox: CheckBox
    private lateinit var includeSpecialCharsCheckbox: CheckBox
    private lateinit var generatePasswordButton: Button
    private lateinit var textViewError: TextView
    private lateinit var algorithmName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_password)

        textViewError = findViewById(R.id.textViewError)
        algorithmSpinner = findViewById(R.id.algorithmSpinner)
        passwordNameEditText = findViewById(R.id.passwordNameEditText)
        passwordLengthSeekBar = findViewById(R.id.passwordLengthSeekBar)
        passwordLengthLabel = findViewById(R.id.passwordLengthLabel)
        includeUppercaseCheckbox = findViewById(R.id.includeUppercaseCheckbox)
        includeLowercaseCheckbox = findViewById(R.id.includeLowercaseCheckbox)
        includeNumbersCheckbox = findViewById(R.id.includeNumbersCheckbox)
        includeSpecialCharsCheckbox = findViewById(R.id.includeSpecialCharsCheckbox)
        generatePasswordButton = findViewById(R.id.generatePasswordButton)

        includeUppercaseCheckbox.isChecked = true

        includeUppercaseCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
            } else {
                includeLowercaseCheckbox.isChecked = true
            }
        }

        includeLowercaseCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
            } else {
                includeUppercaseCheckbox.isChecked = true
            }
        }

        algorithmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                handleAlgorithmSelection(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }

        passwordLengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                passwordLengthLabel.text = "Password Length: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Optional: Called when the user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Optional: Called when the user stops touching the SeekBar
            }
        })

        generatePasswordButton.setOnClickListener {
            val passwordName = passwordNameEditText.text.toString()

            if (passwordName.isEmpty()) {
                // Display an error message
                textViewError.text = "Password name cannot be empty"
            } else {
                textViewError.text = ""
                generatePassword()
            }
        }
    }

    private fun handleAlgorithmSelection(position: Int) {
        when (position) {
            0 -> { // Random algorithm
                setVisibilityForOptions(View.VISIBLE)
                algorithmName = "random"
            }

            1 -> { // Pronounceable algorithm
                setVisibilityForOptions(View.VISIBLE)
                includeNumbersCheckbox.visibility = View.GONE
                includeSpecialCharsCheckbox.visibility = View.GONE
                algorithmName = "pronounceable"
            }

            2 -> { // Numbers algorithm
                setVisibilityForOptions(View.VISIBLE)
                includeNumbersCheckbox.visibility = View.GONE
                includeUppercaseCheckbox.visibility = View.GONE
                includeLowercaseCheckbox.visibility = View.GONE
                includeSpecialCharsCheckbox.visibility = View.GONE
                algorithmName = "numbers"
            }
        }
    }

    private fun setVisibilityForOptions(visibility: Int) {
        passwordLengthSeekBar.visibility = visibility
        passwordLengthLabel.visibility = visibility
        includeUppercaseCheckbox.visibility = visibility
        includeLowercaseCheckbox.visibility = visibility
        includeNumbersCheckbox.visibility = visibility
        includeSpecialCharsCheckbox.visibility = visibility
    }

    private fun generatePassword() {
        val passwordName = passwordNameEditText.text.toString()
        val passwordLength =
            passwordLengthSeekBar.progress // Adjusted to match the range 4 to 50
        val includeUppercase = includeUppercaseCheckbox.isChecked
        val includeLowercase = includeLowercaseCheckbox.isChecked
        val includeNumbers = includeNumbersCheckbox.isChecked
        val includeSpecialChars = includeSpecialCharsCheckbox.isChecked

        val url = "http://10.0.2.2/api/create-password"

        val connectTimeout = 30000L // 30 seconds
        val readTimeout = 30000L // 30 seconds

        val client = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, java.util.concurrent.TimeUnit.MILLISECONDS).build()

        // Request Body
        val mediaType = MediaType.parse("application/json")
        val requestBody = RequestBody.create(
            mediaType,
            "{\"name\":\"$passwordName\",\"algorithm\":\"$algorithmName\",\"length\":\"$passwordLength\"" +
                    ",\"includeUppercase\":\"$includeUppercase\",\"includeLowercase\":\"$includeLowercase\"," +
                    "\"includeNumbers\":\"$includeNumbers\",\"includeSpecialChars\":\"$includeSpecialChars\"}"
        )

        // HTTP Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + getAccessToken())
            .build()

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                val responseBody = response.body()?.string()
                val json = JSONObject(responseBody)

                Log.v("a", "RESPONSE")
                Log.v("a", "RESPONSE")
                Log.v("a", "RESPONSE")
                Log.v("a", "RESPONSE")
                Log.v("a", response.toString())

                if (response.code() != 500) {
                    navigateToMainActivity()
                } else {
                    textViewError.text = passwordName + " password name is arleady taken"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", null)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the current activity if you don't want users to navigate back to it using the back button
    }
}