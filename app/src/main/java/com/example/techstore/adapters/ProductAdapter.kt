package com.example.techstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.techstore.databinding.ItemProductBinding
import com.example.techstore.models.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onBuyClick: (Product) -> Unit,
    private val onRentClick: (Product) -> Unit,
    private val onRepairClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvNombre.text = product.nombre
            binding.tvDescripcion.text = product.descripcion.take(80) + "..."
            binding.tvPrecioCompra.text = "💰 ${product.precioCompra}€"
            binding.tvPrecioAlquiler.text = "📅 ${product.precioAlquiler}€/día"
            binding.tvStock.text = if (product.stock > 0) "📦 Stock: ${product.stock}" else "❌ Agotado"

            binding.btnBuy.setOnClickListener { onBuyClick(product) }
            binding.btnRent.setOnClickListener { onRentClick(product) }
            binding.btnRepair.setOnClickListener { onRepairClick(product) }
        }
    }
}