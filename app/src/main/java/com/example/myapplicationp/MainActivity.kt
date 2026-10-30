package com.example.myapplicationp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.myapplicationp.data.CartManager
import com.example.myapplicationp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.topAppBar.updatePadding(top = insets.top)
            val density = resources.displayMetrics.density
            val defaultBottomPx = (12 * density).toInt()
            binding.bottomNavBar.updatePadding(
                bottom = defaultBottomPx + insets.bottom
            )
            windowInsets
        }

        sessionManager = SessionManager(this)

        if (savedInstanceState == null) {
            loadFragment(MenuFragment(), 1)
        }

        // Clic en el menú hamburguesa superior (Opciones)
        binding.ivHamburger.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, "Acerca de Wicho Burger")
            popup.menu.add(0, 2, 1, "Configuración")
            popup.menu.add(0, 3, 2, "Ayuda y Soporte")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        Toast.makeText(this, "Wicho Burger v1.0 - App Oficial", Toast.LENGTH_SHORT).show()
                        true
                    }
                    2 -> {
                        Toast.makeText(this, "Configuración de la app", Toast.LENGTH_SHORT).show()
                        true
                    }
                    3 -> {
                        Toast.makeText(this, "Contacto: soporte@wichoburger.com", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // Clic en el icono del carrito en el top bar lleva a Orders
        binding.flCartContainer.setOnClickListener {
            loadFragment(OrdersFragment(), 2)
        }

        // Configurar clics para las 4 pantallas (pestañas) de la barra inferior
        binding.navHome.setOnClickListener {
            loadFragment(HomeFragment(), 0)
        }

        binding.navMenu.setOnClickListener {
            loadFragment(MenuFragment(), 1)
        }

        binding.navOrders.setOnClickListener {
            loadFragment(OrdersFragment(), 2)
        }

        binding.navProfile.setOnClickListener {
            loadFragment(ProfileFragment(), 3)
        }

        updateCartBadge()
    }

    fun updateCartBadge() {
        val count = CartManager.getTotalItemsCount()
        val badge = findViewById<TextView>(R.id.tvCartBadge)
        if (badge != null) {
            badge.text = count.toString()
            badge.visibility = if (count > 0) View.VISIBLE else View.GONE
        }
    }

    fun navigateToMenu() {
        loadFragment(MenuFragment(), 1)
    }

    private fun loadFragment(fragment: Fragment, tabIndex: Int) {
        updateTabHighlight(tabIndex)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateTabHighlight(activeIndex: Int) {
        val containers = listOf(binding.containerHome, binding.containerMenu, binding.containerOrders, binding.containerProfile)
        val imageViews = listOf(binding.ivHome, binding.ivMenu, binding.ivOrders, binding.ivProfile)
        val textViews = listOf(binding.tvHome, binding.tvMenu, binding.tvOrders, binding.tvProfile)

        for (i in containers.indices) {
            if (i == activeIndex) {
                containers[i].setBackgroundResource(R.drawable.bg_menu_active)
                imageViews[i].setColorFilter(Color.parseColor("#705e00"))
                textViews[i].setTextColor(Color.parseColor("#705e00"))
                textViews[i].setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                containers[i].background = null
                imageViews[i].setColorFilter(Color.parseColor("#5e5e5e"))
                textViews[i].setTextColor(Color.parseColor("#5e5e5e"))
                textViews[i].setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
    }
}
