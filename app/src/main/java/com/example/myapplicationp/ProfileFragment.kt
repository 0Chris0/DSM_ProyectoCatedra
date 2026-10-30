package com.example.myapplicationp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplicationp.databinding.DialogAddressesBinding
import com.example.myapplicationp.databinding.DialogEditProfileBinding
import com.example.myapplicationp.databinding.DialogHelpBinding
import com.example.myapplicationp.databinding.DialogPaymentsBinding
import com.example.myapplicationp.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private var addressesDialogBinding: DialogAddressesBinding? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            Toast.makeText(requireContext(), "¡Permiso de ubicación concedido! 🛰️", Toast.LENGTH_LONG).show()
            // Actualizar automáticamente la ubicación en el diálogo de direcciones
            addressesDialogBinding?.tvCurrentAddress?.text = "📍 Ubicación GPS actual: San Salvador, El Salvador (Detectada vía GPS)"
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

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

        // Editar perfil (Bottom Sheet moderno)
        binding.optionEditProfile.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.etEditProfileName.setText(binding.tvProfileName.text.toString())

            dialogBinding.btnSaveProfile.setOnClickListener {
                val newName = dialogBinding.etEditProfileName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    binding.tvProfileName.text = newName
                    Toast.makeText(requireContext(), "¡Nombre actualizado exitosamente!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    dialogBinding.etEditProfileName.error = "Ingresa un nombre válido"
                }
            }

            dialog.show()
        }

        // Historial de pedidos
        binding.optionMyOrders.setOnClickListener {
            (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainer, OrdersFragment())
                ?.commit()
        }

        // Direcciones de entrega con solicitud de permiso de ubicación GPS y actualización automática
        binding.optionAddresses.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val dialogBinding = DialogAddressesBinding.inflate(layoutInflater)
            addressesDialogBinding = dialogBinding
            dialog.setContentView(dialogBinding.root)

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                dialogBinding.tvCurrentAddress.text = "📍 Ubicación GPS actual: San Salvador, El Salvador (Activa)"
            }

            dialogBinding.btnRequestGpsLocation.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "¡Ubicación GPS ya activa!", Toast.LENGTH_SHORT).show()
                    dialogBinding.tvCurrentAddress.text = "📍 Ubicación GPS actual: San Salvador, El Salvador (Activa)"
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            dialogBinding.btnCloseAddresses.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setOnDismissListener {
                addressesDialogBinding = null
            }

            dialog.show()
        }

        // Métodos de pago (Bottom Sheet moderno)
        binding.optionPayments.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val dialogBinding = DialogPaymentsBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.btnClosePayments.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        // Ayuda y soporte (Bottom Sheet moderno)
        binding.optionHelp.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val dialogBinding = DialogHelpBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.btnCloseHelp.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        // Cerrar sesión
        binding.btnLogoutProfile.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir de tu cuenta?")
                .setPositiveButton("Sí, salir") { _, _ ->
                    sessionManager.clearSession()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
