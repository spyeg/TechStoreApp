package com.example.techstore.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.R
import com.example.techstore.databinding.ActivityBuyBinding
import com.example.techstore.models.Product
import com.example.techstore.utils.FirebaseHelper

class BuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Comprar Producto"

        // Recibir el producto del intent
        product = intent.getSerializableExtra("product") as? Product ?: run {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar información del producto
        binding.tvProductName.text = product.nombre
        binding.tvProductPrice.text = "💰 Precio: ${product.precioCompra}€"
        binding.tvProductStock.text = "📦 Stock disponible: ${product.stock}"

        binding.btnConfirmBuy.setOnClickListener {
            realizarCompra()
        }
    }

    private fun realizarCompra() {
        val cantidad = binding.etQuantity.text.toString().toIntOrNull() ?: 1

        if (cantidad < 1) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidad > product.stock) {
            Toast.makeText(this, "Stock insuficiente. Solo hay ${product.stock} unidades", Toast.LENGTH_LONG).show()
            return
        }

        val userId = FirebaseHelper.getCurrentUserId()
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val total = product.precioCompra * cantidad

        // Crear el pedido
        val orderData = mapOf(
            "usuarioId" to userId,
            "productoId" to product.id,
            "productoNombre" to product.nombre,
            "cantidad" to cantidad,
            "precioUnitario" to product.precioCompra,
            "total" to total,
            "estado" to "PENDIENTE",
            "fecha" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().getReference("orders")
            .push()
            .setValue(orderData)
            .addOnSuccessListener {
                // Actualizar stock
                val newStock = product.stock - cantidad
                FirebaseDatabase.getInstance().getReference("products")
                    .child(product.id)
                    .child("stock")
                    .setValue(newStock)

                Toast.makeText(this, "✅ Compra realizada con éxito! Total: ${total}€", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}