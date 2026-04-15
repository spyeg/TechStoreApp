package com.example.techstore.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.example.techstore.activities.BuyActivity
import com.example.techstore.activities.RentActivity
import com.example.techstore.activities.RepairActivity
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
            onBuyClick = { product ->
                val intent = Intent(requireContext(), BuyActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onRentClick = { product ->
                val intent = Intent(requireContext(), RentActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onRepairClick = { product ->
                val intent = Intent(requireContext(), RepairActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
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

                if (productList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}