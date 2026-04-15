package com.example.techstore.models

data class Rental(
    var id: String = "",
    var usuarioId: String = "",
    var usuarioNombre: String = "",
    var productoId: String = "",
    var productoNombre: String = "",
    var fechaInicio: String = "",
    var fechaFin: String = "",
    var precioTotal: Double = 0.0,
    var estado: String = "PENDIENTE", // PENDIENTE, ACTIVO, FINALIZADO, CANCELADO
    var fechaRegistro: Long = System.currentTimeMillis()
)