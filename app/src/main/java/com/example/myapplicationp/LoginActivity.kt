package com.example.myapplicationp

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conectar ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. REQUISITO AUTOLOGIN: Si ya inició sesión antes, se salta el Login automáticamente
        if (sessionManager.isLoggedIn()) {
            irAMainActivity()
            return
        }

        // 2. REQUISITO RECORDAR DATOS: Si el checkbox estaba activo, autocompleta los campos
        if (sessionManager.isRememberMeActive()) {
            binding.etEmail.setText(sessionManager.getSavedUsername())
            binding.etPassword.setText(sessionManager.getSavedPassword())
            binding.cbRemember.isChecked = true // Asegúrate de que el id de tu CheckBox en el XML sea cbRemember
        }

        // Subrayar el texto de "Crear usuario" para que se vea como enlace
        binding.tvCreateUser.paintFlags = binding.tvCreateUser.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Configurar el click del botón
        binding.btnLogin.setOnClickListener {
            validarEntradas()
        }
    }

    private fun validarEntradas() {
        val usuario = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val recordarStatus = binding.cbRemember.isChecked // Estado del checkbox

        // 1. Validar que ponga cualquier nombre (que no esté vacío)
        if (usuario.isEmpty()) {
            binding.etEmail.error = "Ingresa tu usuario"
            binding.etEmail.requestFocus()
            return
        }

        // 2. Validar que ponga una contraseña válida
        if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            binding.etPassword.requestFocus()
            return
        }

        // 3. REQUISITO PERSISTENCIA: Si pasa las validaciones, guarda los datos localmente
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
