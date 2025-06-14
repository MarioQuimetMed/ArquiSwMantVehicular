package com.wilsonmontano.mvc1erparcial.controlador

import android.content.Context
//import com.wilsonmontano.mvc1erparcial.modelo.Gasto
import com.wilsonmontano.mvc1erparcial.modelo.GastoModelo
import com.wilsonmontano.mvc1erparcial.modelo.MantenimientoModelo
import com.wilsonmontano.mvc1erparcial.modelo.VehiculoModelo
import com.wilsonmontano.mvc1erparcial.ui.helpui.GastoUI
import com.wilsonmontano.mvc1erparcial.vista.VistaGastoInterface
import java.time.LocalDate

class GastoControlador(
    private val context: Context,
    private val vista: VistaGastoInterface
) {
    private val gasto = GastoModelo(context)
    private val mantenimientoModelo = MantenimientoModelo(context)
    private val vehiculoModelo = VehiculoModelo(context)

    fun guardarGasto(nombre: String, costo: Double, idMantenimiento: Int) {
        val fechaActual = LocalDate.now().toString()
        val nuevo = GastoModelo.Gasto(
            itemNombre = nombre,
            itemCosto = costo,
            itemFecha = fechaActual,
            idMantenimiento = idMantenimiento
        )
        gasto.insertar(nuevo)
        vista.mostrarMensaje("Gasto añadido correctamente")
        cargarGastos(idMantenimiento)
    }

    fun eliminarGasto(idGasto: Int, idMantenimiento: Int) {
        gasto.eliminar(idGasto)
        vista.mostrarMensaje("Gasto eliminado correctamente")
        cargarGastos(idMantenimiento)
    }

    fun cargarGastos(idMantenimiento: Int) {
        val mantenimiento = mantenimientoModelo.buscarPorId(idMantenimiento)
        val vehiculo = vehiculoModelo.buscarPorId(mantenimiento?.idVehiculo ?: 0)
        val listaGastos = gasto.listarPorMantenimiento(idMantenimiento)
        val totalGasto = listaGastos.sumOf { it.itemCosto }

        val listaUI = listaGastos.map { gasto ->
            GastoUI(
                id = gasto.id,
                itemNombre = gasto.itemNombre,
                itemCosto = gasto.itemCosto,
                itemFecha = gasto.itemFecha,
                mantenimientoId = gasto.idMantenimiento,
                mantenimientoNombre = mantenimiento?.nombre ?: "¿?",
                vehiculo = "${vehiculo?.marca ?: "¿"} ${vehiculo?.modelo ?: "?"}",
                total_gasto = totalGasto
            )
        }

        vista.actualizarLista(listaUI)
    }

    fun obtenerMantenimientosPorVehiculo(idVehiculo: Int?): List<Pair<Int, String>> {
        return mantenimientoModelo.listar()
            .filter { idVehiculo == null || it.idVehiculo == idVehiculo }
            .map { it.id to it.nombre }
    }

    fun obtenerVehiculosParaVista(): List<Pair<Int, String>> {
        return vehiculoModelo.listar().map { it.id to "${it.marca} ${it.modelo}" }
    }

}
