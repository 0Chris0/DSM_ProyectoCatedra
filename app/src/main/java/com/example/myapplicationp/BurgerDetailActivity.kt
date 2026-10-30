package com.example.myapplicationp

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import coil.load
import com.example.myapplicationp.data.CartManager
import com.example.myapplicationp.databinding.ActivityBurgerDetailBinding
import java.util.Locale

class BurgerDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBurgerDetailBinding
    private var basePrice = 0.0
    private var quantity = 1
    private var category = 1 // 1: Burger, 2: Side, 3: Drink, 4: Dessert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBurgerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.topBarDetail.updatePadding(top = insets.top)
            val density = resources.displayMetrics.density
            val defaultBottomPx = (16 * density).toInt()
            binding.bottomActionLayout.updatePadding(
                bottom = defaultBottomPx + insets.bottom
            )
            windowInsets
        }

        val idStr = intent.getStringExtra("BURGER_ID") ?: "1"
        val idNum = idStr.toIntOrNull() ?: 1
        val name = intent.getStringExtra("BURGER_NAME") ?: "Producto"
        basePrice = intent.getDoubleExtra("BURGER_PRICE", 10.0)
        val description = intent.getStringExtra("BURGER_DESC") ?: ""
        val imageUrl = intent.getStringExtra("BURGER_IMAGE") ?: ""

        // Clasificación exacta por rangos de IDs:
        // 1-15: Hamburguesas / Platos Principales
        // 16-25: Acompañamientos / Entradas / Sides
        // 26-38: Bebidas y Malteadas
        // 39-50: Postres
        category = when (idNum) {
            in 1..15 -> 1
            in 16..25 -> 2
            in 26..38 -> 3
            in 39..50 -> 4
            else -> 1
        }

        when (category) {
            1 -> { // Hamburguesas
                binding.cardDrink.visibility = View.GONE
                binding.cardDessert.visibility = View.GONE
                binding.cardSalad.visibility = View.GONE
                binding.cardBread.visibility = View.VISIBLE
                binding.cardMeats.visibility = View.VISIBLE
                binding.cardExtras.visibility = View.VISIBLE
                binding.cardSauces.visibility = View.VISIBLE
            }
            2 -> { // Acompañamientos / Sides
                binding.cardDrink.visibility = View.GONE
                binding.cardDessert.visibility = View.GONE
                binding.cardSalad.visibility = View.GONE
                binding.cardBread.visibility = View.GONE
                binding.cardMeats.visibility = View.GONE
                binding.cardExtras.visibility = View.VISIBLE
                binding.cardSauces.visibility = View.VISIBLE
            }
            3 -> { // Bebidas
                binding.cardDrink.visibility = View.VISIBLE
                binding.cardDessert.visibility = View.GONE
                binding.cardSalad.visibility = View.GONE
                binding.cardBread.visibility = View.GONE
                binding.cardMeats.visibility = View.GONE
                binding.cardExtras.visibility = View.GONE
                binding.cardSauces.visibility = View.GONE
            }
            4 -> { // Postres
                binding.cardDrink.visibility = View.GONE
                binding.cardDessert.visibility = View.VISIBLE
                binding.cardSalad.visibility = View.GONE
                binding.cardBread.visibility = View.GONE
                binding.cardMeats.visibility = View.GONE
                binding.cardExtras.visibility = View.GONE
                binding.cardSauces.visibility = View.GONE
            }
        }

        binding.tvDetailName.text = name
        binding.tvDetailPrice.text = "$${String.format(Locale.getDefault(), "%.2f", basePrice)}"
        binding.tvDetailDesc.text = description
        binding.ivDetailImage.load(imageUrl.trim()) {
            crossfade(true)
            placeholder(R.drawable.logo_wicho)
            error(R.drawable.logo_wicho)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etSpecialNotes.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollViewDetail.post {
                    binding.scrollViewDetail.smoothScrollTo(0, binding.etSpecialNotes.bottom)
                }
            }
        }

        val updatePrice = {
            var currentTotal = basePrice
            when (category) {
                3 -> { // Drink
                    if (binding.rbSizeLarge.isChecked) currentTotal += 0.75
                    if (binding.rbSizeXtra.isChecked) currentTotal += 1.50
                }
                4 -> { // Dessert
                    if (binding.cbExtraIceCream.isChecked) currentTotal += 1.50
                    if (binding.cbExtraFudge.isChecked) currentTotal += 0.75
                    if (binding.cbExtraWhippedCream.isChecked) currentTotal += 0.50
                }
                else -> { // Burger (1) or Side (2)
                    if (category == 1) {
                        if (binding.rbBreadIntegral.isChecked) currentTotal += 0.50
                        if (binding.rbBreadGarlic.isChecked) currentTotal += 0.75
                        if (binding.rbMeat2.isChecked) currentTotal += 2.50
                        if (binding.rbMeat3.isChecked) currentTotal += 5.00
                    }
                    if (binding.cbBacon.isChecked) currentTotal += 1.50
                    if (binding.cbCheese.isChecked) currentTotal += 1.00
                    if (binding.cbJalapeno.isChecked) currentTotal += 0.75
                    if (binding.cbEgg.isChecked) currentTotal += 1.25
                    if (binding.cbOnion.isChecked) currentTotal += 0.80
                    if (binding.cbSauceBBQ.isChecked) currentTotal += 0.50
                    if (binding.cbSauceChipotle.isChecked) currentTotal += 0.50
                    if (binding.cbSauceRanch.isChecked) currentTotal += 0.50
                }
            }

            val finalTotal = currentTotal * quantity
            binding.btnAddToCartDetail.text = "AGREGAR • $${String.format(Locale.getDefault(), "%.2f", finalTotal)}"
        }

        updatePrice()

        val listener = CompoundButton.OnCheckedChangeListener { _, _ -> updatePrice() }
        binding.rbSizeMed.setOnCheckedChangeListener(listener)
        binding.rbSizeLarge.setOnCheckedChangeListener(listener)
        binding.rbSizeXtra.setOnCheckedChangeListener(listener)
        binding.rbIceNormal.setOnCheckedChangeListener(listener)
        binding.rbIceLess.setOnCheckedChangeListener(listener)
        binding.rbIceNone.setOnCheckedChangeListener(listener)

        binding.cbExtraIceCream.setOnCheckedChangeListener(listener)
        binding.cbExtraFudge.setOnCheckedChangeListener(listener)
        binding.cbExtraWhippedCream.setOnCheckedChangeListener(listener)

        binding.rbBreadBrioche.setOnCheckedChangeListener(listener)
        binding.rbBreadIntegral.setOnCheckedChangeListener(listener)
        binding.rbBreadGarlic.setOnCheckedChangeListener(listener)
        binding.rbMeat1.setOnCheckedChangeListener(listener)
        binding.rbMeat2.setOnCheckedChangeListener(listener)
        binding.rbMeat3.setOnCheckedChangeListener(listener)
        binding.cbBacon.setOnCheckedChangeListener(listener)
        binding.cbCheese.setOnCheckedChangeListener(listener)
        binding.cbJalapeno.setOnCheckedChangeListener(listener)
        binding.cbEgg.setOnCheckedChangeListener(listener)
        binding.cbOnion.setOnCheckedChangeListener(listener)
        binding.cbSauceBBQ.setOnCheckedChangeListener(listener)
        binding.cbSauceChipotle.setOnCheckedChangeListener(listener)
        binding.cbSauceRanch.setOnCheckedChangeListener(listener)

        binding.btnDetailPlus.setOnClickListener {
            quantity++
            binding.tvDetailQty.text = quantity.toString()
            updatePrice()
        }

        binding.btnDetailMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvDetailQty.text = quantity.toString()
                updatePrice()
            }
        }

        binding.btnAddToCartDetail.setOnClickListener {
            val customDesc = when (category) {
                3 -> { // Drink
                    val sizeText = when {
                        binding.rbSizeLarge.isChecked -> "Grande 22oz"
                        binding.rbSizeXtra.isChecked -> "Jarra 32oz"
                        else -> "Mediano 16oz"
                    }
                    val iceText = when {
                        binding.rbIceLess.isChecked -> "Poco Hielo"
                        binding.rbIceNone.isChecked -> "Sin Hielo"
                        else -> "Hielo Normal"
                    }
                    "$sizeText | $iceText"
                }
                4 -> { // Dessert
                    val dessertExtras = mutableListOf<String>()
                    if (binding.cbExtraIceCream.isChecked) dessertExtras.add("Bola de helado extra")
                    if (binding.cbExtraFudge.isChecked) dessertExtras.add("Jarabe fudge extra")
                    if (binding.cbExtraWhippedCream.isChecked) dessertExtras.add("Crema batida extra")

                    if (dessertExtras.isNotEmpty()) "Extras: ${dessertExtras.joinToString(", ")}" else "Porción estándar"
                }
                else -> { // Burger (1) or Side (2)
                    val extras = mutableListOf<String>()
                    if (binding.cbBacon.isChecked) extras.add("Tocino extra")
                    if (binding.cbCheese.isChecked) extras.add("Queso extra")
                    if (binding.cbJalapeno.isChecked) extras.add("Jalapeños")
                    if (binding.cbEgg.isChecked) extras.add("Huevo frito")
                    if (binding.cbOnion.isChecked) extras.add("Cebolla caramelizada")
                    if (binding.cbSauceBBQ.isChecked) extras.add("Salsa BBQ")
                    if (binding.cbSauceChipotle.isChecked) extras.add("Mayonesa Chipotle")
                    if (binding.cbSauceRanch.isChecked) extras.add("Salsa Ranch")

                    var desc = if (category == 1) {
                        val breadText = when {
                            binding.rbBreadIntegral.isChecked -> "Pan Integral"
                            binding.rbBreadGarlic.isChecked -> "Pan de Ajo"
                            else -> "Pan Brioche"
                        }
                        val meatText = when {
                            binding.rbMeat2.isChecked -> "2 Carnes"
                            binding.rbMeat3.isChecked -> "3 Carnes"
                            else -> "1 Carne"
                        }
                        "$breadText | $meatText"
                    } else {
                        "Porción estándar"
                    }

                    if (extras.isNotEmpty()) desc += " | Extras: ${extras.joinToString(", ")}"
                    desc
                }
            }

            val notes = binding.etSpecialNotes.text.toString().trim()
            val finalDesc = if (notes.isNotEmpty()) "$customDesc | Nota: $notes" else customDesc

            var currentTotal = basePrice
            when (category) {
                3 -> {
                    if (binding.rbSizeLarge.isChecked) currentTotal += 0.75
                    if (binding.rbSizeXtra.isChecked) currentTotal += 1.50
                }
                4 -> {
                    if (binding.cbExtraIceCream.isChecked) currentTotal += 1.50
                    if (binding.cbExtraFudge.isChecked) currentTotal += 0.75
                    if (binding.cbExtraWhippedCream.isChecked) currentTotal += 0.50
                }
                else -> {
                    if (category == 1) {
                        if (binding.rbBreadIntegral.isChecked) currentTotal += 0.50
                        if (binding.rbBreadGarlic.isChecked) currentTotal += 0.75
                        if (binding.rbMeat2.isChecked) currentTotal += 2.50
                        if (binding.rbMeat3.isChecked) currentTotal += 5.00
                    }
                    if (binding.cbBacon.isChecked) currentTotal += 1.50
                    if (binding.cbCheese.isChecked) currentTotal += 1.00
                    if (binding.cbJalapeno.isChecked) currentTotal += 0.75
                    if (binding.cbEgg.isChecked) currentTotal += 1.25
                    if (binding.cbOnion.isChecked) currentTotal += 0.80
                    if (binding.cbSauceBBQ.isChecked) currentTotal += 0.50
                    if (binding.cbSauceChipotle.isChecked) currentTotal += 0.50
                    if (binding.cbSauceRanch.isChecked) currentTotal += 0.50
                }
            }

            CartManager.addItem(name, currentTotal, quantity, finalDesc)

            Toast.makeText(this, "¡$quantity x $name agregado al carrito!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
