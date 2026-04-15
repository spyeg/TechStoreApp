package com.example.techstore.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.example.techstore.R
import com.example.techstore.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogin.setOnClickListener {
            loginWithEmail()
        }

        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Verificar si hay usuario logueado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Verificar si el usuario tiene rol en la base de datos
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
                        // Verificar o crear perfil en Realtime Database
                        checkOrCreateUserProfile(user)
                    }
                } else {
                    val errorMsg = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkOrCreateUserProfile(user: com.google.firebase.auth.FirebaseUser) {
        val userId = user.uid
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // Crear perfil si no existe
                val userData = mapOf(
                    "uid" to userId,
                    "nombre" to (user.displayName ?: "Usuario"),
                    "email" to (user.email ?: ""),
                    "role" to "cliente",
                    "activo" to true,
                    "fechaRegistro" to System.currentTimeMillis()
                )
                userRef.setValue(userData).addOnSuccessListener {
                    checkUserRoleAndNavigate(userId)
                }
            } else {
                checkUserRoleAndNavigate(userId)
            }
        }.addOnFailureListener {
            checkUserRoleAndNavigate(userId)
        }
    }

    private fun checkUserRoleAndNavigate(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("role").get().addOnSuccessListener { snapshot ->
            val role = snapshot.getValue(String::class.java)

            if (role == "admin") {
                startActivity(Intent(this, AdminMainActivity::class.java))
                finish()
            } else if (role == "cliente") {
                startActivity(Intent(this, ClientMainActivity::class.java))
                finish()
            } else {
                // Si no tiene rol, ir a selección de rol
                startActivity(Intent(this, RoleSelectionActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            // Si hay error, ir a selección de rol
            startActivity(Intent(this, RoleSelectionActivity::class.java))
            finish()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!, account.displayName ?: "", account.email ?: "")
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, nombre: String, email: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        checkOrCreateUserProfile(it)
                    }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}