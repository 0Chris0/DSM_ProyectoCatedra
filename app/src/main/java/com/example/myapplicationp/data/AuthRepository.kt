package com.example.myapplicationp.data

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth

class AuthRepository
{
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Rescuperacion de contraseña Firebase
    */

    fun sendPasswordResetEmail(email: String, callback: (Boolean, String?) -> Unit) {
        val emailClean = email.trim()
        if (emailClean.isEmpty()) {
            callback(false, "Por favor, ingresa tu correo electrónico")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailClean).matches()) {
            callback(false, "Asegúrate de ingresar un correo electrónico válido")
            return
        }
        firebaseAuth.sendPasswordResetEmail(emailClean)
            .addOnSuccessListener {
                callback(true, "Se ha enviado un correo de recuperación a $emailClean")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.localizedMessage ?: "Error al enviar el correo")
            }
    }

    /**
     * Inicio de sesión con Firebase (Email y Password)
     */
    fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, "¡Bienvenido!")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.localizedMessage ?: "Error al iniciar sesión")
            }
    }

    /**
     * Registro real de nuevos usuarios en Firebase
     */
    fun registerUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, "¡Cuenta creada exitosamente!")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.localizedMessage ?: "Error al registrarse")
            }
    }

    /**
     * Integración básica para botón de GitHub
     */
    fun handleGitHubSignIn(callback: (Boolean, String) -> Unit) {
        callback(true, "Iniciando sesión con GitHub...")
    }

    /**
    * integración para botón de Outlook
    */
    fun handleOutlookSignIn(callback: (Boolean, String) -> Unit) {
        callback(true, "Iniciando sesión con Outlook...")
    }

}