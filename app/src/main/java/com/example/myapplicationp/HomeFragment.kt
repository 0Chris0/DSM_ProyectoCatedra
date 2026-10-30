package com.example.myapplicationp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplicationp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnPromoAction.setOnClickListener {
            navigateToMenu(1)
        }

        binding.cardCatBurgers.setOnClickListener {
            navigateToMenu(1)
        }

        binding.cardCatSides.setOnClickListener {
            navigateToMenu(2)
        }

        binding.cardCatDrinks.setOnClickListener {
            navigateToMenu(3)
        }

        binding.cardCatDesserts.setOnClickListener {
            navigateToMenu(4)
        }

        return binding.root
    }

    private fun navigateToMenu(categoryFilter: Int) {
        val menuFragment = MenuFragment().apply {
            arguments = Bundle().apply {
                putInt("FILTER_CATEGORY", categoryFilter)
            }
        }
        (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainer, menuFragment)
            ?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
