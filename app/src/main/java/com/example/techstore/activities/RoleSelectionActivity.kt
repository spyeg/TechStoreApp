package com.example.techstore.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.databinding.ActivityRoleSelectionBinding

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Verificar si ya tiene rol asignado
        checkExistingRole()

        binding.cardClient.setOnClickListener {
            updateUserRole("cliente")
        }

        binding.cardAdmin.setOnClickListener {
            updateUserRole("admin")
        }
    }

    private fun checkExistingRole() {
        val userId = auth.currentUser?.uid ?: return

        FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("role")
            .get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.getValue(String::class.java)
                if (role == "admin") {
                    startActivity(Intent(this, AdminMainActivity::class.java))
                    finish()
                } else if (role == "cliente") {
                    startActivity(Intent(this, ClientMainActivity::class.java))
                    finish()
                }
                // Si no tiene rol, mostrar la pantalla de selección
            }
    }

    private fun updateUserRole(role: String) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.cardClient.isEnabled = false
        binding.cardAdmin.isEnabled = false

        FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("role")
            .setValue(role)
            .addOnSuccessListener {
                val message = if (role == "admin") "✅ Bienvenido Administrador" else "✅ Bienvenido Cliente"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                if (role == "admin") {
                    startActivity(Intent(this, AdminMainActivity::class.java))
                } else {
                    startActivity(Intent(this, ClientMainActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "❌ Error: ${error.message}", Toast.LENGTH_SHORT).show()
                binding.cardClient.isEnabled = true
                binding.cardAdmin.isEnabled = true
            }
    }
}