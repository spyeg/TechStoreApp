package com.example.techstore.models

data class Repair(
    var id: String = "",
    var usuarioId: String = "",
    var usuarioNombre: String = "",
    var productoId: String = "",
    var productoNombre: String = "",
    var fechaSolicitud: String = "",
    var fechaEntrega: String = "",
    var estado: String = "PENDIENTE", // PENDIENTE, EN_REVISION, REPARANDO, FINALIZADA
    var descripcionProblema: String = "",
    var diagnostico: String = "",
    var coste: Double = 0.0,
    var fechaRegistro: Long = System.currentTimeMillis()
)