package com.example.techstore.models

data class Product(
    var id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var precioCompra: Double = 0.0,
    var precioAlquiler: Double = 0.0,
    var stock: Int = 0,
    var stockMinimo: Int = 5,
    var categoria: String = "",
    var imagenUrl: String = "",
    var activo: Boolean = true,
    var fechaRegistro: Long = System.currentTimeMillis()
)