package com.example.myapplicationp.data

object FavoritesManager {
    private val favoriteIds = mutableSetOf<String>()

    fun toggleFavorite(id: String): Boolean {
        return if (favoriteIds.contains(id)) {
            favoriteIds.remove(id)
            false
        } else {
            favoriteIds.add(id)
            true
        }
    }

    fun isFavorite(id: String): Boolean = favoriteIds.contains(id)
}
