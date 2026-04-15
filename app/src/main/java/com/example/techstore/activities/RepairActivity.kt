package com.example.techstore.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.databinding.ActivityRepairBinding
import com.example.techstore.models.Product
import com.example.techstore.utils.FirebaseHelper

class RepairActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepairBinding
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Solicitar Reparación"

        product = intent.getSerializableExtra("product") as? Product ?: run {
            Toast.makeText(this, "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvProductName.text = product.nombre

        binding.btnConfirmRepair.setOnClickListener {
            solicitarReparacion()
        }

        binding.btnSelectImage.setOnClickListener {
            Toast.makeText(this, "Selector de imágenes - Próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun solicitarReparacion() {
        val description = binding.etDescription.text.toString().trim()
        val userId = FirebaseHelper.getCurrentUserId()

        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Describe el problema"
            return
        }

        val repairData = mapOf(
            "usuarioId" to userId,
            "productoId" to product.id,
            "productoNombre" to product.nombre,
            "descripcionProblema" to description,
            "estado" to "PENDIENTE",
            "fechaSolicitud" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().getReference("repairs")
            .push()
            .setValue(repairData)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Solicitud de reparación enviada", Toast.LENGTH_LONG).show()
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