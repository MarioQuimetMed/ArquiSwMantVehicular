package com.wilsonmontano.mvc1erparcial.ui.helpui

data class MantenimientoUI(
    val id: Int,
    val tipo: String,
    val vehiculo: String,
    val taller: String,
    val fecha: String,
    val costo: Double,
    val nombre: String,
    val estado: String,
    val notificar: Boolean = false,
    val gastos: List<GastoUI>? = null

)
