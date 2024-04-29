package com.example.assignment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment.R
import com.example.assignment.adapters.SearchProductAdapter
import com.example.assignment.data.Product
import com.example.assignment.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: SearchProductAdapter
    private var lastSearchResults: List<Product>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentSearchBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        // Set up the search view
       val searchView = binding.searchView
        searchView.isIconified = false
        searchView.isFocusable = true
        searchView.isFocusableInTouchMode = true


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchProducts(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }

    private fun searchProducts(query: String) {
        val startQuery = query.toLowerCase()
        val endQuery = startQuery + '\uf8ff'  // A high code point in the Unicode range used for Firestore indexing

        firestore.collection("products")
            .orderBy("name")
            .startAt(startQuery)
            .endAt(endQuery)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(context, "No products found", Toast.LENGTH_SHORT).show()
                    adapter.updateData(emptyList())  // Clear adapter if no products are found

                } else {
                 val products = documents.toObjects(Product::class.java)
                    lastSearchResults = products  // Save the results

                    //show how many products are found
                    binding.textViewSearchResultTitle.text = "${products.size} Products Found"
                    adapter.updateData(products)  // Update adapter with the fetched products

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error searching products: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        adapter.onclick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment3,b)
        }
    }

    override fun onResume() {
        super.onResume()
        lastSearchResults?.let {
            adapter.updateData(it)  // Restore adapter data
            binding.textViewSearchResultTitle.text = "${it.size} Products Found"

            adapter.onclick = {
                val b = Bundle().apply { putParcelable("product", it) }
                findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment3,b)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchProductAdapter(emptyList())
        binding.recyclerViewSearchResults.adapter = adapter
        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(context) // Attach a layout manager

    }
}
