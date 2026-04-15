package com.example.techstore.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginWithEmail()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserRoleAndNavigate(currentUser.uid)
        }
    }

    private fun loginWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Ingresa tu email"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Ingresa tu contraseña"
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Iniciando..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "INICIAR SESIÓN"

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        checkUserRoleAndNavigate(user.uid)
                    }
                } else {
                    val errorMsg = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkUserRoleAndNavigate(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("role").get().addOnSuccessListener { snapshot ->
            val role = snapshot.getValue(String::class.java)

            if (role == "admin") {
                startActivity(Intent(this, AdminMainActivity::class.java))
                finish()
            } else {
                // Si es "cliente" o no tiene rol, va a la pantalla de cliente
                startActivity(Intent(this, ClientMainActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            // Si hay error, asumimos que es cliente
            startActivity(Intent(this, ClientMainActivity::class.java))
            finish()
        }
    }
}