package com.example.techstore.models

data class User(
    var uid: String = "",
    var nombre: String = "",
    var email: String = "",
    var telefono: String = "",
    var direccion: String = "",
    var role: String = "cliente", // "cliente" o "admin"
    var activo: Boolean = true,
    var fechaRegistro: Long = System.currentTimeMillis()
)