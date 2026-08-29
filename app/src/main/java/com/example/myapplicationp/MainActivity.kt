package com.example.myapplicationp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // REQUISITO LOGOUT: Botón para borrar los datos guardados
        binding.btnLogoutMain.setOnClickListener { // Asegúrate de que el id de tu botón en la pantalla principal sea btnLogoutMain
            sessionManager.clearSession() // Borra la memoria local

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
