package com.example.myapplicationp

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.myapplicationp.data.AuthRepository
import com.example.myapplicationp.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    private val authRepository = AuthRepository() //instanciando AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conectar ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // AUTOLOGIN: Si ya inició sesión antes, se salta el Login automáticamente
        if (sessionManager.isLoggedIn()) {
            irAMainActivity()
            return
        }

        // rECORDAR DATOS: Si el checkbox estaba activo, autocompleta los campos
        if (sessionManager.isRememberMeActive()) {
            binding.etEmail.setText(sessionManager.getSavedUsername())
            binding.etPassword.setText(sessionManager.getSavedPassword())
            binding.cbRemember.isChecked = true
        }

        // subyara el texto como un link web
        binding.tvForgotPassword.paintFlags = binding.tvForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // event_clic para recuperar contraseña
        binding.tvForgotPassword.setOnClickListener {
            val emailIngresado = binding.etEmail.text.toString()

            // llamando a la simulación de Recuperar contraseña dentro de AuthRepository
            authRepository.sendPasswordResetEmail(emailIngresado) { exito, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                if (exito)
                {
                    binding.etEmail.text?.clear()   //limpia el campo de email/usuario
                }
            }
        }

        // subyara para que se muestre como un link
        binding.tvCreateUser.paintFlags = binding.tvCreateUser.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Evento clic para el enlace de Crear Usuario
        binding.tvCreateUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configurar el click del botón
        binding.btnLogin.setOnClickListener {
            validarEntradas()
        }

        // Botón Google con Credential Manager y Firebase
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Botón GitHub
        binding.btnGithub.setOnClickListener {
            authRepository.handleGitHubSignIn { exito, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                if (exito) {
                    sessionManager.saveSession("usuario_de_github", "123456", false)
                    irAMainActivity()
                }
            }
        }

        // Botón Outlook
        binding.btnOutlook.setOnClickListener {
            authRepository.handleOutlookSignIn { exito, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                if (exito) {
                    sessionManager.saveSession("usuario.outlook@outlook.com", "123456", false)
                    irAMainActivity()
                }
            }
        }

    }

    private fun signInWithGoogle() {
        val serverClientId = getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleGoogleCredential(result)
            } catch (e: GetCredentialException) {
                Toast.makeText(this@LoginActivity, "Error Google Sign-In: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleCredential(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val email = googleIdTokenCredential.id

                authRepository.signInWithGoogleToken(googleIdToken, email) { exito, mensaje ->
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                    if (exito) {
                        sessionManager.saveSession(email, "google_auth", false)
                        irAMainActivity()
                    }
                }
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(this, "Error al procesar credencial de Google", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Tipo de credencial no soportado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarEntradas() {
        val usuario = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val recordarStatus = binding.cbRemember.isChecked

        if (usuario.isEmpty()) {
            binding.etEmail.error = "Ingresa tu correo"
            binding.etEmail.requestFocus()
            return
        }

        if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            binding.etPassword.requestFocus()
            return
        }

        // Autenticación real contra Firebase Auth
        authRepository.loginUser(usuario, password) { exito, mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            if (exito) {
                // Guardar localmente si el CheckBox de recordar estaba activo
                sessionManager.saveSession(usuario, password, recordarStatus)
                irAMainActivity()
            }
        }
    }

    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra el Login para que no se pueda volver atrás con el botón del celular
    }
}
