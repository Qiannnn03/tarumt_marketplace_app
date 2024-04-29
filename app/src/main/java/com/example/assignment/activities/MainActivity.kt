package com.example.assignment.activities

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.assignment.R
import com.example.assignment.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val navController = findNavController(R.id.fragment_container)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navView.setupWithNavController(navController)

        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

        // Listen for item clicks on the bottom navigation view
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.homeFragment, null, options)
                    true
                }

                R.id.navigation_search -> {
                    navController.navigate(R.id.searchFragment, null, options)
                    true
                }

                R.id.navigation_wishlist -> {
                    navController.navigate(R.id.searchFragment, null, options)
                    true
                }

                R.id.navigation_profile -> {
                    navController.navigate(R.id.myProfileFragment, null, options)
                    true
                }

                else -> false
            }
        }
            navController.addOnDestinationChangedListener { _, destination, _ ->
                // Handling the visibility of the bottom navigation and floating action button
                if (destination.id in arrayOf(
                        R.id.sellItemFragment,
                        R.id.productDetailsFragment3,
                        R.id.searchFragment,
                        R.id.loginFragment,
                        R.id.registerFragment,
                        R.id.checkoutFragment
                    )
                ) {
                    binding.nav.visibility = View.GONE
                    binding.floatingActionButton.visibility = View.GONE
                } else {
                    binding.nav.visibility = View.VISIBLE
                    binding.floatingActionButton.visibility = View.VISIBLE
                }

                // Ensure correct menu item is selected when navigating using other means than bottom navigation
                val menuItemId = when (destination.id) {
                    R.id.homeFragment -> R.id.navigation_home
                    R.id.searchFragment -> R.id.navigation_search
                    R.id.wishListFragment -> R.id.navigation_wishlist // Ensure you have a separate destination ID for wishlist
                    R.id.myProfileFragment -> R.id.navigation_profile
                    else -> null
                }
                menuItemId?.let {
                    binding.bottomNav.menu.findItem(it).isChecked = true
                }
            }
            binding.floatingActionButton.setOnClickListener {
                findNavController(R.id.fragment_container).navigate(R.id.sellItemFragment)
            }
        }
    }