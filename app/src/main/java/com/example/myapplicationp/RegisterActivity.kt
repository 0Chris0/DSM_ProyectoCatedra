package com.example.myapplicationp

import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationp.data.AuthRepository
import com.example.myapplicationp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Subrayar enlace de volver al login
        binding.tvBackToLogin.paintFlags = binding.tvBackToLogin.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Botón de registro
        binding.btnRegisterSubmit.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etRegisterEmail.error = "Ingresa tu correo"
                binding.etRegisterEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etRegisterPassword.error = "Mínimo 6 caracteres"
                binding.etRegisterPassword.requestFocus()
                return@setOnClickListener
            }

            // Llamada a Firebase para registrar
            authRepository.registerUser(email, password) { exito, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                if (exito) {
                    // Si se registra con éxito, regresamos al Login
                    finish()
                }
            }
        }

        // Enlace para volver al login si ya tiene cuenta
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}