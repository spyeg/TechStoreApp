package com.example.techstore.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { registerUser() }
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val nombre = binding.etNombre.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validaciones...
        if (nombre.isEmpty()) { binding.etNombre.error = "Ingresa tu nombre"; return }
        if (email.isEmpty()) { binding.etEmail.error = "Ingresa tu email"; return }
        if (password.isEmpty()) { binding.etPassword.error = "Ingresa tu contraseña"; return }
        if (password != confirmPassword) { binding.etConfirmPassword.error = "Las contraseñas no coinciden"; return }
        if (password.length < 6) { binding.etPassword.error = "Mínimo 6 caracteres"; return }

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Registrando..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "REGISTRARSE"

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        // Creamos un mapa con los datos del usuario, incluyendo el rol por defecto "cliente"
                        val userData = mapOf(
                            "uid" to userId,
                            "nombre" to nombre,
                            "email" to email,
                            "role" to "cliente", // Rol por defecto
                            "activo" to true,
                            "fechaRegistro" to System.currentTimeMillis()
                        )

                        // Guardamos los datos en Realtime Database
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(userId)
                            .setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "✅ Registro exitoso", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, RoleSelectionActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(this, "Error al guardar: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    val errorMsg = when {
                        task.exception?.message?.contains("email address is already in use") == true -> "❌ Email ya registrado"
                        task.exception?.message?.contains("badly formatted") == true -> "❌ Email inválido"
                        else -> "❌ Error: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
    }
}