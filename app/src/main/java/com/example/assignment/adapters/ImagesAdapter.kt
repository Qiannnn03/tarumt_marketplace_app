package com.example.assignment.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R

class ImagesAdapter(
    private val context: Context,
    private val images: MutableList<Uri>,
    private val onImageRemoved: (Uri) -> Unit

) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val removeButton: ImageView = view.findViewById(R.id.removeImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = images[position]
        holder.imageView.setImageURI(imageUri)

        holder.removeButton.setOnClickListener {
            // Remove the image from the list
            val currentPosition = holder.adapterPosition
            val imageUri = images[currentPosition]
            images.removeAt(currentPosition)
            notifyItemRemoved(currentPosition)
            notifyItemRangeChanged(currentPosition, images.size)
            onImageRemoved(imageUri)
        }
    }

    override fun getItemCount(): Int = images.size
}
