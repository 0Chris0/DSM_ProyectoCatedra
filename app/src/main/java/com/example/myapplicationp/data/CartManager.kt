package com.example.myapplicationp.data

data class CartItem(
    val name: String,
    val price: Double,
    var quantity: Int,
    val description: String = "Carne de res, lechuga, tomate, cebolla, queso cheddar."
)

object CartManager {
    private val items = mutableListOf<CartItem>()

    fun addItem(name: String, price: Double, quantity: Int, description: String = "Carne de res, lechuga, tomate, cebolla, queso cheddar.") {
        val existing = items.find { it.name == name }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(CartItem(name, price, quantity, description))
        }
    }

    fun removeItem(item: CartItem) {
        items.remove(item)
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity > 0) {
            item.quantity = newQuantity
        } else {
            items.remove(item)
        }
    }

    fun getItems(): List<CartItem> = items

    fun clearCart() {
        items.clear()
    }

    fun getTotalItemsCount(): Int {
        return items.sumOf { it.quantity }
    }

    fun getSubtotal(): Double {
        return items.sumOf { it.price * it.quantity }
    }
}
