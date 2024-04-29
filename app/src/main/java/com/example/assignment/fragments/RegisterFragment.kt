package com.example.assignment.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.assignment.R
import com.example.assignment.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        // Set up navigation with the toolbar
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        // Link to Login Page
        binding.signin.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Set up text change listeners for EditText fields
        binding.registerEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isValidEmail(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

        binding.regsiterPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isValidPassword(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

        binding.registerUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isValidUsername(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

        binding.registerContactNum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isValidContactNumber(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

        binding.registerCfPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isValidConfirmPassword(binding.regsiterPassword.text.toString(), s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })

        // Register Button
        binding.btnRegister.setOnClickListener {
            val email: String = binding.registerEmail.text.toString().trim()
            val password: String = binding.regsiterPassword.text.toString().trim()
            val confirmPassword: String = binding.registerCfPassword.text.toString().trim()
            val username: String = binding.registerUsername.text.toString().trim()
            val contactNumber: String = binding.registerContactNum.text.toString().trim()

            if (isValidUsername(username) && isValidEmail(email) && isValidContactNumber(contactNumber) && isValidPassword(password) && isValidConfirmPassword(password, confirmPassword)) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.visibility = View.GONE
                fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        val userMap = hashMapOf(
                            "email" to email,
                            "username" to username,
                            "contactNumber" to contactNumber
                        )
                        // Add user information to Firebase Firestore database
                        fStore.collection("users").document(user!!.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                // Success message
                                Log.d("RegisterFragment", "User profile created for $userMap")
                                Toast.makeText(requireContext(), "Register successfully!", Toast.LENGTH_SHORT).show()
                                binding.progressBar.visibility = View.GONE
                                binding.btnRegister.visibility = View.VISIBLE
                                navController.navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                            .addOnFailureListener { exception ->
                                // Error handling for Firebase Firestore
                                Toast.makeText(requireContext(), "Register failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                binding.progressBar.visibility = View.GONE
                                binding.btnRegister.visibility = View.VISIBLE
                            }
                    }
                    .addOnFailureListener { exception ->
                        // User creation failed, handle the error
                        Toast.makeText(requireContext(), "Register failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.visibility = View.VISIBLE
                    }
            }
        }

        return binding.root
    }

    // Username Validation
    private fun isValidUsername(username: String): Boolean {
        return if (TextUtils.isEmpty(username)) {
            binding.layoutUsername.error = "Username is required"
            // Set the top margin to 0 to collapse the space
            val params = binding.layoutEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 20
            binding.layoutEmail.layoutParams = params
            false
        } else if (username.length < 5) {
            binding.layoutUsername.error = "Username must be at least 5 characters long"
            // Set the top margin to 0 to collapse the space
            val params = binding.layoutEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 20
            binding.layoutEmail.layoutParams = params
            false
        } else if (username.contains(Regex("\\s"))) {
            binding.layoutUsername.error = "Username cannot contain space"
            // Set the top margin to 0 to collapse the space
            val params = binding.layoutEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 20
            binding.layoutEmail.layoutParams = params
            false
        } else {
            // Clear the error if the username is valid
            binding.layoutUsername.error = null
            // Reset the top margin to its original value
            val params = binding.layoutEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 0
            binding.layoutEmail.layoutParams = params
            true
        }
    }

    // Email Validation
    private fun isValidEmail(email: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            binding.layoutEmail.error = "Email is required"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.error = "Invalid email address"
            false
        } else if (!email.matches("[A-Za-z]+-[A-Za-z]{2}[0-9]{2}@student\\.tarc\\.edu\\.my".toRegex())) {
            binding.layoutEmail.error = "Only TARUMT student email addresses allowed."
            false
        } else {
            binding.layoutEmail.error = null // Clear the error
            true
        }
    }

    // Contact Number Validation
    private fun isValidContactNumber(contactNumber: String): Boolean {
        return if (TextUtils.isEmpty(contactNumber)) {
            binding.layoutContactNum.error = "Contact number is required"
            false
        } else if (contactNumber.startsWith('0')) {
            binding.layoutContactNum.error = "Contact number must not start with 0"
            false
        } else if (!contactNumber.matches(Regex("\\d+"))) {
            binding.layoutContactNum.error = "Contact number must contain only digits"
            false
        }else if (contactNumber.length !in 9..10) {
            binding.layoutContactNum.error = "Contact number must be 9 or 10 digits long"
            return false
        } else {
            // Clear the error if the contact number is valid
            binding.layoutContactNum.error = null
            true
        }
    }

    // Password Validation
    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!\\[\\]{}|:;<>?,./\\-_()'*\"`~])(?=\\S+\$).{8,}\$".toRegex()

        return if (TextUtils.isEmpty(password)) {
            binding.layoutPassword.error = "Password is required"
            false
        } else if (password.length < 8) {
            binding.layoutPassword.error = "Password must be at least 8 characters long"
            false
        } else {
            var errorMessage: String? = null

            if (!password.matches(passwordRegex)) {
                errorMessage = "Password must contain "
                if (!password.any { it.isDigit() }) {
                    errorMessage += "at least one digit, "
                }
                if (!password.any { it.isLowerCase() }) {
                    errorMessage += "at least one lowercase letter, "
                }
                if (!password.any { it.isUpperCase() }) {
                    errorMessage += "at least one uppercase letter, "
                }
                if (!password.any { it in "@#\$%^&+=" }) {
                    errorMessage += "at least one special character, "
                }
                errorMessage = errorMessage.removeSuffix(", ") // Remove the trailing comma and space
            }

            binding.layoutPassword.error = errorMessage
            errorMessage == null
        }
    }

    // Confirm Password Validation
    private fun isValidConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (password != confirmPassword) {
            binding.layoutCfPassword.error = "Passwords do not match"
            false
        } else {
            binding.layoutCfPassword.error = null
            true
        }
    }
}