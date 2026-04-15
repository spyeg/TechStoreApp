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

        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_products -> loadFragment(ProductsFragment())
                R.id.nav_rentals -> loadFragment(RentalsFragment())
                R.id.nav_repairs -> loadFragment(RepairsFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}