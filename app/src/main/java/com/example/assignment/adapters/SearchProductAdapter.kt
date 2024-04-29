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

class SearchProductAdapter(var productList: List<Product>
) :
    RecyclerView.Adapter<SearchProductAdapter.SearchProductViewHolder>() {

    class SearchProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewIcon)
        val categoryTextView : TextView = view.findViewById(R.id.textViewCategory)
        val nameTextView: TextView = view.findViewById(R.id.textViewProductName)
        val priceTextView: TextView = view.findViewById(R.id.textViewProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return SearchProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {
        val product = productList[position]
        // Load the image with Glide
        Glide.with(holder.itemView.context)
            .load(product.imageUrls.first()) // Load the first image URL
            .into(holder.imageView)
        holder.nameTextView.text = product.name
        holder.categoryTextView.text = product.category
        holder.priceTextView.text = "RM${product.price}"

        holder.itemView.setOnClickListener {
            onclick?.invoke(product)
        }

    }
    fun updateData(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
    override fun getItemCount() = productList.size

    var onclick: ((Product) -> Unit)? = null
}
