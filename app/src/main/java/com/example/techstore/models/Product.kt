package com.example.techstore.models

import java.io.Serializable

data class Product(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precioCompra: Double = 0.0,
    val precioAlquiler: Double = 0.0,
    val stock: Int = 0,
    val stockMinimo: Int = 5,
    val categoria: String = "",
    val imagenUrl: String = "",
    val activo: Boolean = true,
    val fechaRegistro: Long = System.currentTimeMillis()
) : Serializable