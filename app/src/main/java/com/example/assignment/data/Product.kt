package com.example.assignment.data

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id : String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category : String = "",
    val imageUrls: List<String> = listOf(),
    val sellerID : String = ""



): Parcelable
