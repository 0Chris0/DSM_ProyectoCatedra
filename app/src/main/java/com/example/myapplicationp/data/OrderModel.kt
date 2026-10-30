package com.example.myapplicationp.data

data class OrderModel(
    val orderId: String = "",
    val userEmail: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "En Preparación 🍔"
)
