package com.wilsonmontano.mvc1erparcial.ui.helpui

data class GastoUI(
    val id: Int,
    val itemNombre: String,
    val itemCosto: Double,
    val itemFecha: String,
    val mantenimientoId: Int,
    val mantenimientoNombre: String,
    val vehiculo: String,
    val total_gasto:Double,
    val gastos: List<GastoUI>? = null
)
