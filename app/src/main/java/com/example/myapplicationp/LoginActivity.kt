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
/* CORREGIR ESTA PARTE DEL CODIGO, QUE GENERA ERROR AL INICIAR EN MOVIL
        // Evento clic para el enlace de Crear Usuario
        binding.tvCreateUser.setOnClickListener {
            mostrarDialogoRegistro()
        }
*/
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

        // Configurar el click del botón
        binding.btnLogin.setOnClickListener {
            validarEntradas()
        }

        /*
        binding.btnGoogle.setOnClickListener {
            authRepository.handleGoogleSignInResult("token_de_google_123") { exito, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                if (exito) {
                    sessionManager.saveSession("usuario.google@gmail.com", "123456", false)
                    irAMainActivity()
                }
            }
        }
        */

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

    private fun mostrarDialogoRegistro() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Crear Nueva Cuenta")

        // Inflar o crear un layout simple para el diálogo con dos inputs (Email y Password)
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputEmail = android.widget.EditText(this).apply {
            hint = "Correo electrónico"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        layout.addView(inputEmail)

        val inputPassword = android.widget.EditText(this).apply {
            hint = "Contraseña (mínimo 6 caracteres)"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT
        }
        layout.addView(inputPassword)

        builder.setView(layout)

        builder.setPositiveButton("Registrarse") { _, _ ->
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isNotEmpty() && password.length >= 6) {
                // Llamamos al método de registro real en Firebase
                authRepository.registerUser(email, password) { exito, mensaje ->
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    if (exito) {
                        // Opcional: autocompleta el campo de login principal con el nuevo usuario
                        binding.etEmail.setText(email)
                    }
                }
            } else {
                Toast.makeText(this, "Verifica que el correo sea válido y la contraseña tenga al menos 6 caracteres", Toast.LENGTH_LONG).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
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
