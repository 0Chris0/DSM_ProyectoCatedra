package com.example.myapplicationp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplicationp.data.CartManager
import com.example.myapplicationp.databinding.FragmentOrdersBinding
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)

        binding.btnAddMore.setOnClickListener {
            (activity as? MainActivity)?.let { mainActivity ->
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MenuFragment())
                    .commit()
            }
        }

        binding.btnProceedCheckout.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "¡Pedido pagado y procesado exitosamente! 🎉", Toast.LENGTH_LONG).show()
            CartManager.clearCart()
            loadCartItems()
            (activity as? MainActivity)?.updateCartBadge()
        }

        binding.btnCancelOrder.setOnClickListener {
            CartManager.clearCart()
            loadCartItems()
            (activity as? MainActivity)?.updateCartBadge()
            Toast.makeText(requireContext(), "Pedido cancelado", Toast.LENGTH_SHORT).show()
        }

        loadCartItems()

        return binding.root
    }

    private fun loadCartItems() {
        binding.cartItemsContainer.removeAllViews()
        val cartItems = CartManager.getItems()

        if (cartItems.isEmpty()) {
            binding.tvEmptyCart.visibility = View.VISIBLE
            binding.tvSubtotal.text = "$0.00"
            binding.tvTax.text = "$0.00"
            binding.tvShipping.text = "$0.00"
            binding.tvTotal.text = "$0.00"
        } else {
            binding.tvEmptyCart.visibility = View.GONE
            
            for (item in cartItems) {
                val card = MaterialCardView(requireContext()).apply {
                    radius = 12f * resources.displayMetrics.density
                    elevation = 1f * resources.displayMetrics.density
                    setCardBackgroundColor(Color.WHITE)
                    strokeWidth = (1 * resources.displayMetrics.density).toInt()
                    strokeColor = Color.parseColor("#e0e0e0")
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = (12 * resources.displayMetrics.density).toInt()
                    }
                }

                val layout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)
                }

                val titleLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }

                val titleView = TextView(requireContext()).apply {
                    text = item.name
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.parseColor("#1a1c1c"))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val priceView = TextView(requireContext()).apply {
                    text = "$${String.format(Locale.getDefault(), "%.2f", item.price * item.quantity)}"
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.parseColor("#705e00"))
                }

                titleLayout.addView(titleView)
                titleLayout.addView(priceView)

                val descView = TextView(requireContext()).apply {
                    text = item.description
                    textSize = 13f
                    setTextColor(Color.parseColor("#5e5e5e"))
                    setPadding(0, 4, 0, 8)
                }

                val bottomRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }

                val btnMinus = Button(requireContext()).apply {
                    text = "-"
                    textSize = 14f
                    minWidth = (36 * resources.displayMetrics.density).toInt()
                    minimumWidth = minWidth
                    minHeight = (36 * resources.displayMetrics.density).toInt()
                    minimumHeight = minHeight
                    setOnClickListener {
                        CartManager.updateQuantity(item, item.quantity - 1)
                        loadCartItems()
                        (activity as? MainActivity)?.updateCartBadge()
                    }
                }

                val tvQty = TextView(requireContext()).apply {
                    text = item.quantity.toString()
                    textSize = 15f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.parseColor("#1a1c1c"))
                    setPadding(12, 0, 12, 0)
                    gravity = android.view.Gravity.CENTER
                }

                val btnPlus = Button(requireContext()).apply {
                    text = "+"
                    textSize = 14f
                    minWidth = (36 * resources.displayMetrics.density).toInt()
                    minimumWidth = minWidth
                    minHeight = (36 * resources.displayMetrics.density).toInt()
                    minimumHeight = minHeight
                    setOnClickListener {
                        CartManager.updateQuantity(item, item.quantity + 1)
                        loadCartItems()
                        (activity as? MainActivity)?.updateCartBadge()
                    }
                }

                val btnDelete = TextView(requireContext()).apply {
                    text = "ELIMINAR"
                    textSize = 13f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.parseColor("#d32f2f"))
                    setPadding(24, 0, 0, 0)
                    setOnClickListener {
                        CartManager.removeItem(item)
                        loadCartItems()
                        (activity as? MainActivity)?.updateCartBadge()
                    }
                }

                bottomRow.addView(btnMinus)
                bottomRow.addView(tvQty)
                bottomRow.addView(btnPlus)
                bottomRow.addView(btnDelete)

                layout.addView(titleLayout)
                layout.addView(descView)
                layout.addView(bottomRow)
                card.addView(layout)

                binding.cartItemsContainer.addView(card)
            }

            val subtotal = CartManager.getSubtotal()
            val tax = subtotal * 0.10
            val shipping = if (cartItems.isNotEmpty()) 2.00 else 0.00
            val total = subtotal + tax + shipping

            binding.tvSubtotal.text = "$${String.format(Locale.getDefault(), "%.2f", subtotal)}"
            binding.tvTax.text = "$${String.format(Locale.getDefault(), "%.2f", tax)}"
            binding.tvShipping.text = "$${String.format(Locale.getDefault(), "%.2f", shipping)}"
            binding.tvTotal.text = "$${String.format(Locale.getDefault(), "%.2f", total)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
