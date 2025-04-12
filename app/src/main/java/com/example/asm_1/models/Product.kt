package com.example.asm_1.models


data class Product(
    val id: String,
    val name: String,
    val image: String, // Changed to String to support URL images
    val rating: String,
    val distance: String,
    val price: String,
    val description: String = "Delicious food available at our restaurant. Made with fresh ingredients and prepared by our expert chefs.",
    val categoryId: String = "1"
)

