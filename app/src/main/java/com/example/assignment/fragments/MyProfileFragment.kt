package com.example.assignment.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.assignment.R
import com.example.assignment.databinding.FragmentMyProfileBinding
import com.google.firebase.auth.FirebaseAuth

class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Set up click listener for the logout button
        binding.btnLogout.setOnClickListener {
            logout()
        }

        return binding.root
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
        // Navigate to the login screen
        findNavController().navigate(R.id.action_myProfileFragment_to_loginFragment)
    }

}