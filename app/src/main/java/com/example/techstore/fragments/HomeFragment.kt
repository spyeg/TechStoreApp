package com.example.techstore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.techstore.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar los botones
        binding.btnBuy.setOnClickListener {
            Toast.makeText(requireContext(), "🛒 Ir a Productos", Toast.LENGTH_SHORT).show()
            // Navegar al fragment de productos
            // Si usas Navigation Component, descomenta la línea:
            // findNavController().navigate(R.id.action_home_to_products)
        }

        binding.btnRent.setOnClickListener {
            Toast.makeText(requireContext(), "📅 Ir a Alquileres", Toast.LENGTH_SHORT).show()
        }

        binding.btnRepair.setOnClickListener {
            Toast.makeText(requireContext(), "🔧 Ir a Reparaciones", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}