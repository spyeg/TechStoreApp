package com.example.techstore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.example.techstore.databinding.FragmentProductsBinding
import com.example.techstore.adapters.ProductAdapter
import com.example.techstore.models.Product

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("products")

        setupRecyclerView()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            products = productList,
            onBuyClick = { product -> showBuyDialog(product) },
            onRentClick = { product -> showRentDialog(product) },
            onRepairClick = { product -> showRepairDialog(product) }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductsFragment.adapter
        }
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        it.id = productSnapshot.key ?: ""
                        if (it.activo && it.stock > 0) {
                            productList.add(it)
                        }
                    }
                }
                adapter.updateProducts(productList)
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBuyDialog(product: Product) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Comprar ${product.nombre}")
            .setMessage("Precio: ${product.precioCompra}€\nStock disponible: ${product.stock}\n\n¿Confirmar compra?")
            .setPositiveButton("Comprar") { _, _ ->
                Toast.makeText(requireContext(), "Compra de ${product.nombre} - Próximamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showRentDialog(product: Product) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Alquilar ${product.nombre}")
            .setMessage("Precio/día: ${product.precioAlquiler}€\nStock disponible: ${product.stock}\n\n¿Confirmar alquiler?")
            .setPositiveButton("Alquilar") { _, _ ->
                Toast.makeText(requireContext(), "Alquiler de ${product.nombre} - Próximamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showRepairDialog(product: Product) {
        val input = android.widget.EditText(requireContext())
        input.hint = "Describe el problema..."

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Reparar ${product.nombre}")
            .setView(input)
            .setPositiveButton("Solicitar") { _, _ ->
                val problema = input.text.toString()
                if (problema.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Solicitud enviada: $problema", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Describe el problema", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}