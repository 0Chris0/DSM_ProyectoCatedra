package com.example.myapplicationp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplicationp.data.CartManager
import com.example.myapplicationp.databinding.DialogAddToCartBinding
import com.example.myapplicationp.databinding.FragmentMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.btnAddToCart1.setOnClickListener {
            showAddToCartDialog("La Clásica", 12.00)
        }

        binding.btnAddToCart2.setOnClickListener {
            showAddToCartDialog("Doble Tocino", 15.00)
        }

        return binding.root
    }

    private fun showAddToCartDialog(itemName: String, itemPrice: Double) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogAddToCartBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvBottomSheetTitle.text = itemName
        dialogBinding.tvBottomSheetPrice.text = "$%.2f c/u".format(itemPrice)

        var quantity = 1
        fun updateTotalPrice() {
            dialogBinding.tvQuantity.text = quantity.toString()
            dialogBinding.tvTotal.text = "$%.2f".format(itemPrice * quantity)
        }

        updateTotalPrice()

        dialogBinding.btnPlus.setOnClickListener {
            quantity++
            updateTotalPrice()
        }

        dialogBinding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateTotalPrice()
            }
        }

        dialogBinding.btnConfirmAdd.setOnClickListener {
            CartManager.addItem(itemName, itemPrice, quantity)
            dialog.dismiss()

            Snackbar.make(requireView(), "¡$quantity x $itemName añadido al carrito!", Snackbar.LENGTH_LONG)
                .setAction("Ver Pedidos") {
                    (activity as? MainActivity)?.let { mainActivity ->
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, OrdersFragment())
                            .commit()
                    }
                }
                .show()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
