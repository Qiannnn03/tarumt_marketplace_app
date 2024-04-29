package com.example.assignment.fragments.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment.R
import com.example.assignment.adapters.ProductAdapter
import com.example.assignment.data.Product
import com.example.assignment.databinding.FragmentFurnitureBinding
import com.google.firebase.firestore.FirebaseFirestore


class FurnitureFragment : Fragment() {
    private lateinit var binding: FragmentFurnitureBinding
    private val productList = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFurnitureBinding.inflate(inflater)


        binding.FurnitureRecyclerView.layoutManager = GridLayoutManager(context, 3)
        val adapter = ProductAdapter(productList)
        binding.FurnitureRecyclerView.adapter = adapter

        binding.progressBar.visibility = View.VISIBLE


        db.collection("products")
            .whereEqualTo("category", "Furniture")
            .get()
            .addOnSuccessListener { documents ->
                productList.clear() // Clear the list before adding new items
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                binding.FurnitureRecyclerView.adapter?.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()

            }

        adapter.onclick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment3,b)
        }

        return binding.root
    }







}