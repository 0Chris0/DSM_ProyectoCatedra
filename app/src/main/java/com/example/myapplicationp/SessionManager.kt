package com.example.myapplicationp

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_ME = "rememberMe"
    }

    fun saveSession(username: String, email: String, rememberMe: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe)
        if (rememberMe) {
            editor.putString(KEY_USERNAME, username)
            editor.putString(KEY_PASSWORD, email)
        } else {
            editor.remove(KEY_USERNAME)
            editor.remove(KEY_PASSWORD)
        }
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isRememberMeActive(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)
    fun getSavedUsername(): String? = prefs.getString(KEY_USERNAME, "")
    fun getSavedPassword(): String? = prefs.getString(KEY_PASSWORD, "")

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
