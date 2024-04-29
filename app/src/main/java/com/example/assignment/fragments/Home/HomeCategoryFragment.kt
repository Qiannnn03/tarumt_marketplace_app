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
import com.example.assignment.databinding.FragmentHomeCategoryBinding
import com.google.firebase.firestore.FirebaseFirestore


class HomeCategoryFragment : Fragment() {
    private lateinit var binding: FragmentHomeCategoryBinding
    private val productList = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeCategoryBinding.inflate(inflater)


        // Set up RecyclerView

        binding.mainCategoryRecyclerView.layoutManager = GridLayoutManager(context, 3)
        val adapter = ProductAdapter(productList)
        binding.mainCategoryRecyclerView.adapter = adapter


        binding.progressBar.visibility = View.VISIBLE

        // Fetch products from Firestore
        db.collection("products").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                productList.clear()
                for (document in task.result) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            } else {
                // Handle error
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()

            }
            // Hide ProgressBar after loading
            binding.progressBar.visibility = View.GONE
        }

        adapter.onclick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment3,b)
        }

        return binding.root
    }




}