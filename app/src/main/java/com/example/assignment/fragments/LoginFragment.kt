package com.example.assignment.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.assignment.R
import com.example.assignment.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        fAuth = FirebaseAuth.getInstance();

        // Link to Login Page
        binding.signup.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Set up text change listeners for EditText fields
        binding.loginEmail.addTextChangedListener(object : TextWatcher {
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

        binding.loginPassword.addTextChangedListener(object : TextWatcher {
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

        // Login Button
        binding.btnLogin.setOnClickListener(){
            val email : String = binding.loginEmail.text.toString().trim()
            val password : String = binding.loginPassword.text.toString().trim()

            // Validate email and password
            if (isValidEmail(email) && isValidPassword(password)) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.GONE
                // Authenticate the user with email and password
                fAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login successful
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                            // Navigate to the next screen
                            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_homeFragment)

                        } else {
                            // Login failed
                            Toast.makeText(requireContext(), "Incorrect Email or Password. Please Try again", Toast.LENGTH_SHORT).show()
                        }
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.visibility = View.VISIBLE
                    }
            }
        }

        return binding.root
    }

    // Email Validation
    private fun isValidEmail(email: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            binding.layoutEmail.error = "Email is required"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.error = "Invalid email address"
            false
        } else {
            binding.layoutEmail.error = null
            true
        }
    }

    // Password Validation
    private fun isValidPassword(password: String): Boolean {
        return if (TextUtils.isEmpty(password)) {
            binding.layoutPassword.error = "Password is required"
            false
        } else {
            binding.layoutPassword.error = null
            true
        }
    }

}