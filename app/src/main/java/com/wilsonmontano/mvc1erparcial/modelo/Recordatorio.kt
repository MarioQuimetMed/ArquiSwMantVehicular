package com.wilsonmontano.mvc1erparcial.modelo

data class Recordatorio(
    val id: Int = 0,
    val idMantenimiento: Int,
    val fechaRecordatorio: String,
    val descripcion: String,
    val estado: String = "pendiente"
)
