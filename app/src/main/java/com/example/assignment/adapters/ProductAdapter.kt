package com.example.assignment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.R
import com.example.assignment.data.Product

class ProductAdapter(private val productList: List<Product>
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView = view.findViewById(R.id.productImageView)
        val nameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val priceTextView: TextView = view.findViewById(R.id.productPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        // Load the image with Glide
        Glide.with(holder.itemView.context)
            .load(product.imageUrls.first()) // Load the first image URL
            .into(holder.imageView)
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "RM${product.price}"

        holder.itemView.setOnClickListener {
            onclick?.invoke(product)
        }

    }

    override fun getItemCount() = productList.size

    var onclick: ((Product) -> Unit)? = null
}
