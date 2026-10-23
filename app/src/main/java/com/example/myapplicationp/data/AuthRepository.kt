package com.example.myapplicationp.data

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

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
     * Autenticación con Google Firebase usando el ID de token de Google
     */
    fun signInWithGoogleCredential(idToken: String, callback: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                callback(true, "Accediendo con Google...\n¡Bienvenido!")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.localizedMessage ?: "Error al iniciar sesión con Google")
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