package com.example.myapplicationp.data

import android.util.Patterns

class AuthRepository
{
    /**
     * Rescuperacion de contraseña
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
        // simulacion de una respuesta existosa al email ingresado
        callback(true, "Se ha enviado un correo electrónico a $emailClean")
    }

    /**
     * Autenticacion con Google / Callback para Google Sign-In
     */
    fun handleGoogleSignInResult(idToken: String?, callback: (Boolean, String) -> Unit) {
        if (!idToken.isNullOrEmpty()) {
            callback(true, "Autenticación exitosa con Google")
        } else {
            callback(false, "Hubo un error al autenticar con Google")
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