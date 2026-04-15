package com.example.techstore.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.techstore.R
import com.example.techstore.databinding.ActivityClientMainBinding
import com.example.techstore.fragments.HomeFragment
import com.example.techstore.fragments.ProductsFragment
import com.example.techstore.fragments.ProfileFragment
import com.example.techstore.fragments.RentalsFragment
import com.example.techstore.fragments.RepairsFragment

class ClientMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "TechStore"

        // Cargar el fragmento inicial
        loadFragment(HomeFragment())

        // Configurar la navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_products -> {
                    loadFragment(ProductsFragment())
                    true
                }
                R.id.nav_rentals -> {
                    loadFragment(RentalsFragment())
                    true
                }
                R.id.nav_repairs -> {
                    loadFragment(RepairsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

    // Agrega esta función para manejar el botón de atrás
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}