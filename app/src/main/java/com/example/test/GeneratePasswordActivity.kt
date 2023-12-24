package com.example.test

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GeneratePasswordActivity  : AppCompatActivity() {

    private lateinit var algorithmSpinner: Spinner
    private lateinit var passwordNameEditText: EditText
    private lateinit var passwordLengthSeekBar: SeekBar
    private lateinit var passwordLengthLabel: TextView
    private lateinit var includeUppercaseCheckbox: CheckBox
    private lateinit var includeLowercaseCheckbox: CheckBox
    private lateinit var includeNumbersCheckbox: CheckBox
    private lateinit var includeSpecialCharsCheckbox: CheckBox
    private lateinit var generatePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_password)

        algorithmSpinner = findViewById(R.id.algorithmSpinner)
        passwordNameEditText = findViewById(R.id.passwordNameEditText)
        passwordLengthSeekBar = findViewById(R.id.passwordLengthSeekBar)
        passwordLengthLabel = findViewById(R.id.passwordLengthLabel)
        includeUppercaseCheckbox = findViewById(R.id.includeUppercaseCheckbox)
        includeLowercaseCheckbox = findViewById(R.id.includeLowercaseCheckbox)
        includeNumbersCheckbox = findViewById(R.id.includeNumbersCheckbox)
        includeSpecialCharsCheckbox = findViewById(R.id.includeSpecialCharsCheckbox)
        generatePasswordButton = findViewById(R.id.generatePasswordButton)

        algorithmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                handleAlgorithmSelection(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }

        generatePasswordButton.setOnClickListener {
            // Add logic to generate password based on selected options
            generatePassword()
        }
    }

    private fun handleAlgorithmSelection(position: Int) {
        when (position) {
            0 -> { // Random algorithm
                setVisibilityForOptions(View.VISIBLE)
            }
            1 -> { // Pronounceable algorithm
                setVisibilityForOptions(View.VISIBLE)
                includeNumbersCheckbox.visibility = View.GONE
                includeSpecialCharsCheckbox.visibility = View.GONE
            }
            2 -> { // Numbers algorithm
                setVisibilityForOptions(View.VISIBLE)
                includeNumbersCheckbox.visibility = View.GONE
                includeUppercaseCheckbox.visibility = View.GONE
                includeLowercaseCheckbox.visibility = View.GONE
                includeSpecialCharsCheckbox.visibility = View.GONE
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
        // Implement password generation logic based on selected options
        val passwordName = passwordNameEditText.text.toString()
        val passwordLength = passwordLengthSeekBar.progress + 4 // Adjusted to match the range 4 to 50
        val includeUppercase = includeUppercaseCheckbox.isChecked
        val includeLowercase = includeLowercaseCheckbox.isChecked
        val includeNumbers = includeNumbersCheckbox.isChecked
        val includeSpecialChars = includeSpecialCharsCheckbox.isChecked

        // Add your password generation logic here, using the selected options
        // For now, let's display a toast message with the generated password details
        val generatedPassword = "Generated password for $passwordName with length $passwordLength"
        Toast.makeText(this, generatedPassword, Toast.LENGTH_SHORT).show()
    }
}