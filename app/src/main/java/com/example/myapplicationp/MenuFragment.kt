package com.example.myapplicationp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.myapplicationp.data.ApiService
import com.example.myapplicationp.data.Burger
import com.example.myapplicationp.data.CartManager
import com.example.myapplicationp.databinding.DialogAddToCartBinding
import com.example.myapplicationp.databinding.FragmentMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var currentFilter = 0 // 0: All, 1: Burgers, 2: Sides, 3: Drinks, 4: Desserts
    private var cachedBurgers = listOf<Burger>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        currentFilter = arguments?.getInt("FILTER_CATEGORY", 0) ?: 0

        setupChips()

        binding.etSearchMenu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterAndPopulate()
            }
        })

        fetchBurgers()

        return binding.root
    }

    private fun setupChips() {
        binding.chipAll.isChecked = currentFilter == 0
        binding.chipBurgers.isChecked = currentFilter == 1
        binding.chipSides.isChecked = currentFilter == 2
        binding.chipDrinks.isChecked = currentFilter == 3
        binding.chipDesserts.isChecked = currentFilter == 4

        val updateChipStyles = {
            binding.chipAll.chipBackgroundColor = android.content.res.ColorStateList.valueOf(if (binding.chipAll.isChecked) Color.parseColor("#ffd700") else Color.parseColor("#e8e8e8"))
            binding.chipBurgers.chipBackgroundColor = android.content.res.ColorStateList.valueOf(if (binding.chipBurgers.isChecked) Color.parseColor("#ffd700") else Color.parseColor("#e8e8e8"))
            binding.chipSides.chipBackgroundColor = android.content.res.ColorStateList.valueOf(if (binding.chipSides.isChecked) Color.parseColor("#ffd700") else Color.parseColor("#e8e8e8"))
            binding.chipDrinks.chipBackgroundColor = android.content.res.ColorStateList.valueOf(if (binding.chipDrinks.isChecked) Color.parseColor("#ffd700") else Color.parseColor("#e8e8e8"))
            binding.chipDesserts.chipBackgroundColor = android.content.res.ColorStateList.valueOf(if (binding.chipDesserts.isChecked) Color.parseColor("#ffd700") else Color.parseColor("#e8e8e8"))
        }

        updateChipStyles()

        binding.chipAll.setOnCheckedChangeListener { _, checked ->
            if (checked) { currentFilter = 0; updateChipStyles(); filterAndPopulate() }
        }
        binding.chipBurgers.setOnCheckedChangeListener { _, checked ->
            if (checked) { currentFilter = 1; updateChipStyles(); filterAndPopulate() }
        }
        binding.chipSides.setOnCheckedChangeListener { _, checked ->
            if (checked) { currentFilter = 2; updateChipStyles(); filterAndPopulate() }
        }
        binding.chipDrinks.setOnCheckedChangeListener { _, checked ->
            if (checked) { currentFilter = 3; updateChipStyles(); filterAndPopulate() }
        }
        binding.chipDesserts.setOnCheckedChangeListener { _, checked ->
            if (checked) { currentFilter = 4; updateChipStyles(); filterAndPopulate() }
        }
    }

    private fun fetchBurgers() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                cachedBurgers = withContext(Dispatchers.IO) {
                    ApiService.instance.getHamburguesas()
                }
                binding.progressBar.visibility = View.GONE
                filterAndPopulate()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error al cargar la API: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun filterAndPopulate() {
        val query = binding.etSearchMenu.text.toString().trim().lowercase(Locale.ROOT)

        val categoryFiltered = when (currentFilter) {
            1 -> cachedBurgers.filter { (it.id.toIntOrNull() ?: 1) in 1..15 }
            2 -> cachedBurgers.filter { (it.id.toIntOrNull() ?: 1) in 16..25 }
            3 -> cachedBurgers.filter { (it.id.toIntOrNull() ?: 1) in 26..38 }
            4 -> cachedBurgers.filter { (it.id.toIntOrNull() ?: 1) in 39..50 }
            else -> cachedBurgers
        }

        val finalFiltered = if (query.isEmpty()) {
            categoryFiltered
        } else {
            categoryFiltered.filter {
                it.name.lowercase(Locale.ROOT).contains(query) ||
                        it.description.lowercase(Locale.ROOT).contains(query)
            }
        }

        populateMenu(finalFiltered)
    }

    private fun populateMenu(burgers: List<Burger>) {
        // Mantener título, barra de búsqueda y chips de filtrado (los primeros 3 elementos del LinearLayout)
        while (binding.menuContainer.childCount > 3) {
            binding.menuContainer.removeViewAt(3)
        }

        for (burger in burgers) {
            val card = MaterialCardView(requireContext()).apply {
                radius = 14f * resources.displayMetrics.density
                elevation = 1f * resources.displayMetrics.density
                setCardBackgroundColor(Color.WHITE)
                strokeWidth = (1 * resources.displayMetrics.density).toInt()
                strokeColor = Color.parseColor("#ebebeb")
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (12 * resources.displayMetrics.density).toInt()
                }
                
                setOnClickListener {
                    val intent = Intent(requireContext(), BurgerDetailActivity::class.java).apply {
                        putExtra("BURGER_ID", burger.id)
                        putExtra("BURGER_NAME", burger.name)
                        putExtra("BURGER_PRICE", burger.price)
                        putExtra("BURGER_DESC", burger.description)
                        putExtra("BURGER_IMAGE", burger.image)
                    }
                    startActivity(intent)
                }
            }

            val cardLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
            }

            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (140 * resources.displayMetrics.density).toInt())
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundColor(Color.parseColor("#f0f0f0"))
                load(burger.image.trim()) {
                    crossfade(true)
                    placeholder(R.drawable.logo_wicho)
                    error(R.drawable.logo_wicho)
                }
            }

            val contentLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(14, 12, 14, 14)
            }

            val headerLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    bottomMargin = (2 * resources.displayMetrics.density).toInt()
                }
            }

            val nameView = TextView(requireContext()).apply {
                text = burger.name
                textSize = 15f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#1a1c1c"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val priceView = TextView(requireContext()).apply {
                text = "$${String.format(Locale.getDefault(), "%.2f", burger.price)}"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#705e00"))
            }

            headerLayout.addView(nameView)
            headerLayout.addView(priceView)

            val descView = TextView(requireContext()).apply {
                text = burger.description
                textSize = 12.5f
                setTextColor(Color.parseColor("#666666"))
                maxLines = 2
                ellipsize = android.text.TextUtils.TruncateAt.END
                setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
            }

            val buttonLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.END
            }

            val btnAdd = Button(requireContext()).apply {
                text = "+ Personalizar"
                textSize = 12f
                setTextColor(Color.parseColor("#705e00"))
                setTypeface(null, Typeface.BOLD)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#ffd700"))
                setOnClickListener {
                    val intent = Intent(requireContext(), BurgerDetailActivity::class.java).apply {
                        putExtra("BURGER_ID", burger.id)
                        putExtra("BURGER_NAME", burger.name)
                        putExtra("BURGER_PRICE", burger.price)
                        putExtra("BURGER_DESC", burger.description)
                        putExtra("BURGER_IMAGE", burger.image)
                    }
                    startActivity(intent)
                }
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (36 * resources.displayMetrics.density).toInt())
                setPadding(24, 0, 24, 0)
            }

            buttonLayout.addView(btnAdd)

            contentLayout.addView(headerLayout)
            contentLayout.addView(descView)
            contentLayout.addView(buttonLayout)

            cardLayout.addView(imageView)
            cardLayout.addView(contentLayout)
            card.addView(cardLayout)

            binding.menuContainer.addView(card)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
