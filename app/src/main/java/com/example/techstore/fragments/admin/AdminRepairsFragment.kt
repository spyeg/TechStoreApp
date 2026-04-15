package com.example.techstore.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AdminRepairsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            text = "Gestión de Reparaciones\n\n🔧 Reparaciones pendientes\n⚙️ En proceso\n✅ Completadas"
            textSize = 18f
            gravity = android.view.Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
    }
}