package com.example.assignment.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment.R
import com.example.assignment.adapters.ImagesAdapter
import com.example.assignment.data.Product
import com.example.assignment.databinding.FragmentSellItemBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SellItemFragment : Fragment() {
    private lateinit var binding: FragmentSellItemBinding
    private var selectedImages = mutableListOf<Uri>()
    private lateinit var imagesAdapter: ImagesAdapter
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSellItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupImagePicker()

        binding.submitProductButton.setOnClickListener {
            // Show the progress bar and hide the submit button
            binding.progressBar.visibility = View.VISIBLE
            binding.submitProductButton.visibility = View.GONE


            val name = binding.productNameEditText.text.toString().trim()
            val description = binding.productDescEditText.text.toString().trim()
            val price = binding.productPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val category = binding.categorySpinner.selectedItem.toString()

            if (selectedImages.isNotEmpty()) {
                uploadImages(selectedImages) { imageUrls ->
                    addProduct(name, description, price, category, imageUrls)
                }
            } else {
                // Handle the case where no images are selectedsd
                Toast.makeText(context, "Please select at least one image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupRecyclerView() {

        imagesAdapter = ImagesAdapter(requireContext(), selectedImages) { removedUri ->
            selectedImages.remove(removedUri)

        }

        binding.selectedPhotosRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
        }
    }

    private fun setupImagePicker() {
        val selectImagesActivityResult = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            // Identify new URIs by filtering out those already in selectedImages
            val newUris = uris.filterNot { selectedImages.contains(it) }

            // Add only new URIs to the selectedImages
            if (newUris.isNotEmpty()) {
                selectedImages.addAll(newUris)
                imagesAdapter.notifyDataSetChanged()
            }

            // Notify the user if some selected images were already added
            if (newUris.size < uris.size) {
                Snackbar.make( binding.root,"Same images cannot be added.", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.addPhotoButton.setOnClickListener {
            selectImagesActivityResult.launch("image/*")
        }
    }

    private fun uploadImages(images: List<Uri>, onComplete: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("product_images")
        val uploadedImageUrls = mutableListOf<String>()
        images.forEach { uri ->
            val fileRef = storageRef.child(uri.lastPathSegment ?: "image-${System.currentTimeMillis()}")
            fileRef.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result.toString()
                    uploadedImageUrls.add(downloadUrl)
                    if (uploadedImageUrls.size == images.size) {
                        onComplete(uploadedImageUrls)
                    }
                } else {
                    // Handle failure
                    Toast.makeText(context, "Image upload failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addProduct(name: String, description: String, price: Double, category: String, imageUrls: List<String>) {

        val sellerID = FirebaseAuth.getInstance().currentUser?.uid
        if (sellerID == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.loginFragment)
        }else{
            val newProductRef = db.collection("products").document()
            val productWithId = Product(newProductRef.id, name, description, price, category, imageUrls,sellerID)

            // Set the product object with the ID in the Firestore document
            newProductRef.set(productWithId)
                .addOnSuccessListener {
                    // Hide the progress bar and show the submit button again
                    binding.progressBar.visibility = View.GONE
                    binding.submitProductButton.visibility = View.VISIBLE
                    // Notify the user of the successful addition
                    Toast.makeText(context, "Product added successfully", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.homeFragment)
                }
                .addOnFailureListener { e ->
                    // Hide the progress bar and show the submit button again
                    binding.progressBar.visibility = View.GONE
                    binding.submitProductButton.visibility = View.VISIBLE
                    // Notify the user of the failure to add the product
                    Toast.makeText(context, "Error adding product: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }



    }



}