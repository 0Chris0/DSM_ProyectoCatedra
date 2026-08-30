package com.example.myapplicationp

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationp.data.AuthRepository
import com.example.myapplicationp.databinding.ActivityLoginBinding

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

        // Subrayar el texto de "Crear usuario" para que se vea como enlace
        binding.tvCreateUser.paintFlags = binding.tvCreateUser.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Configurar el click del botón
        binding.btnLogin.setOnClickListener {
            validarEntradas()
        }

        binding.btnGoogle.setOnClickListener {
            authRepository.handleGoogleSignInResult("token_de_google_123") { exito, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                if (exito) {
                    sessionManager.saveSession("usuario.google@gmail.com", "123456", false)
                    irAMainActivity()
                }
            }
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

    private fun validarEntradas() {
        val usuario = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val recordarStatus = binding.cbRemember.isChecked // Estado del checkbox

        // validar que ponga cualquier nombre (que no esté vacío)
        if (usuario.isEmpty()) {
            binding.etEmail.error = "Ingresa tu usuario"
            binding.etEmail.requestFocus()
            return
        }

        // validar que ponga una contraseña válida
        if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            binding.etPassword.requestFocus()
            return
        }

        // PERSISTENCIA: Si pasa las validaciones, guarda los datos localmente
        sessionManager.saveSession(usuario, password, recordarStatus)

        // Navegar al Menú Principal (MainActivity)
        irAMainActivity()
    }

    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra el Login para que no se pueda volver atrás con el botón del celular
    }
}
