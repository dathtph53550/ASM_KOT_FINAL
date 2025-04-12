package com.example.asm_1.models

data class Order(
    val id: String? = null, // ID is auto-generated on the server
    val nameOrder: String,
    val addressOrder: String,
    val phoneOrder: String,
    val createAt: String = java.time.OffsetDateTime.now().toString(),
    val status: String = "pending",
    val order: List<OrderItem>
)

data class OrderItem(
    val name: String,
    val image: String,
    val price: String,
    val quantity: Int
) 