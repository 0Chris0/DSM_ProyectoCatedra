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
import java.util.UUID

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

        // Boton Google --------------------------------------------------------------------------
        binding.btnGoogle.setOnClickListener {
            singInWithGoogle()
        }


        // Botón GitHub --------------------------------------------------------------------------
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

    // Metodo para iniciar sesion con Google -----------------------------------------------------------------------------
    private fun singInWithGoogle() {
        // Nonce de seguridad requerido por Google Credential Manager
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray(Charsets.UTF_8)
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)   //digest es un arreglo de bytes
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }    //hasedNonce es un string que contiene el hash del nonce


        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("394308242017-rhe5ak1uhcuu3bbdptkchfptohgrvssf.apps.googleusercontent.com")  //client_id_3
            .setFilterByAuthorizedAccounts(false) //es para que no filtre por cuentas de Google
            .setNonce(hashedNonce)  //para evitar ataques de replay
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)  //este nos sirve para guardar las credenciales en el dispositivo

        lifecycleScope.launch { // Lanzamos la solicitud en una corrutina vinculada al ciclo de vida
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleGoogleCredentialResponse(result)
            }
            catch (e: GetCredentialException) { //en caso de error, mostramos un mensaje
                Toast.makeText(this@LoginActivity, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }


    }   //fin de la funcion singInWithGoogle() 👌
    private fun handleGoogleCredentialResponse(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken

                        // Enviamos el token real a nuestro AuthRepository
                        authRepository.signInWithGoogleCredential(googleIdToken) { exito, mensaje ->
                            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                            if (exito) {
                                sessionManager.saveSession(googleIdTokenCredential.id, "", true)
                                irAMainActivity()
                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Toast.makeText(this, "Error al procesar las credenciales", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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

        // Autenticación real contra Firebase
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
