package com.example.assignment.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.assignment.R
import com.example.assignment.adapters.ProductDetailsAdapter
import com.example.assignment.databinding.FragmentProductDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val adapter = ProductDetailsAdapter()
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product
        firestoreDb = FirebaseFirestore.getInstance()
        setupViewPager(product.imageUrls)
        //setup toolbar
        setupToolbar()

        // Fetch and update seller name
        fetchSellerName(product.sellerID) { sellerName ->

            binding.sellerName.text = ("Seller: " +sellerName) ?: "Unknown Seller"
        }

      binding.apply {


          productName.text = product.name
          productPrice.text = "RM${product.price}"
          tvCategoryTag.text = product.category
            tvProductDescription.text = product.description
            adapter.differ.submitList(product.imageUrls)
      }



        binding.viewPagerProductImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        binding.buyNowButton.setOnClickListener(){
            val b = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(R.id.action_productDetailsFragment3_to_checkoutFragment,b)
        }


    }


    private fun fetchSellerName(sellerID: String, onResult: (String?) -> Unit) {
        firestoreDb.collection("users").document(sellerID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val sellerName = document.getString("username")
                    onResult(sellerName)
                } else {
                    Toast.makeText(context, "Seller not found", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ProductDetailsFragment", "Error getting seller details: ", exception)
                Toast.makeText(context, "Failed to fetch seller details", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }



    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupViewPager(imageUrls: List<String>) {
        binding.viewPagerProductImages.adapter = adapter
        adapter.differ.submitList(imageUrls) // Assuming this submits data to your ViewPager's adapter.
        setupIndicators(imageUrls.size) // Now setupIndicators knows how many dots to display.
    }

    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                requireContext(),
                R.drawable.indicator_inactive // Create and use a drawable for inactive state
            ))
            indicators[i]?.layoutParams = layoutParams
            binding.llIndicators.addView(indicators[i])
        }

        setCurrentIndicator(0) // Set the first image as selected
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.llIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.llIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active) // Create and use a drawable for active state
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
                )
            }
        }
    }


}