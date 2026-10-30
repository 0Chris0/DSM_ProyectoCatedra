package com.example.myapplicationp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplicationp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email ?: sessionManager.getSavedUsername() ?: "usuario@wichoburger.com"
        binding.tvUserEmail.text = email

        val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        binding.tvProfileName.text = name

        binding.optionEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Editar perfil próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.optionAddresses.setOnClickListener {
            Toast.makeText(requireContext(), "Direcciones guardadas: 1", Toast.LENGTH_SHORT).show()
        }

        binding.optionPayments.setOnClickListener {
            Toast.makeText(requireContext(), "Método de pago: Tarjeta terminada en 4092", Toast.LENGTH_SHORT).show()
        }

        binding.optionNotifications.setOnClickListener {
            Toast.makeText(requireContext(), "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogoutProfile.setOnClickListener {
            sessionManager.clearSession()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
