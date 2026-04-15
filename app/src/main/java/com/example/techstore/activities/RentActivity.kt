package com.example.techstore.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.databinding.ActivityRentBinding
import com.example.techstore.models.Product
import com.example.techstore.utils.FirebaseHelper
import java.text.SimpleDateFormat
import java.util.*

class RentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentBinding
    private lateinit var product: Product
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Alquilar Producto"

        product = intent.getSerializableExtra("product") as? Product ?: run {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvProductName.text = product.nombre
        binding.tvProductPrice.text = "📅 Precio/día: ${product.precioAlquiler}€"

        binding.btnConfirmRent.setOnClickListener {
            realizarAlquiler()
        }

        // Calcular total cuando cambian las fechas
        binding.etStartDate.setOnFocusChangeListener { _, _ -> calcularTotal() }
        binding.etEndDate.setOnFocusChangeListener { _, _ -> calcularTotal() }
    }

    private fun calcularTotal() {
        val startDateStr = binding.etStartDate.text.toString()
        val endDateStr = binding.etEndDate.text.toString()

        if (startDateStr.isNotEmpty() && endDateStr.isNotEmpty()) {
            try {
                val startDate = dateFormat.parse(startDateStr)
                val endDate = dateFormat.parse(endDateStr)
                val days = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()

                if (days > 0) {
                    val total = days * product.precioAlquiler
                    binding.tvTotalPrice.text = "💰 Total: ${total}€ ($days días)"
                } else {
                    binding.tvTotalPrice.text = "⚠️ Fechas inválidas"
                }
            } catch (e: Exception) {
                binding.tvTotalPrice.text = "⚠️ Formato de fecha inválido"
            }
        }
    }

    private fun realizarAlquiler() {
        val startDate = binding.etStartDate.text.toString()
        val endDate = binding.etEndDate.text.toString()
        val userId = FirebaseHelper.getCurrentUserId()

        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Ingresa las fechas de alquiler", Toast.LENGTH_SHORT).show()
            return
        }

        val rentalData = mapOf(
            "usuarioId" to userId,
            "productoId" to product.id,
            "productoNombre" to product.nombre,
            "fechaInicio" to startDate,
            "fechaFin" to endDate,
            "precioPorDia" to product.precioAlquiler,
            "estado" to "PENDIENTE",
            "fechaRegistro" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().getReference("rentals")
            .push()
            .setValue(rentalData)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Solicitud de alquiler enviada", Toast.LENGTH_LONG).show()
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