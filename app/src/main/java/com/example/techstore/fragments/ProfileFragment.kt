package com.example.techstore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.techstore.utils.FirebaseHelper

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val email = FirebaseHelper.getCurrentUserEmail() ?: "No email"

        return TextView(requireContext()).apply {
            text = "👤 Mi Perfil\n\nEmail: $email\n\nPróximamente más funciones"
            textSize = 18f
            gravity = android.view.Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
    }
}