package com.wilsonmontano.mvc1erparcial.controlador

import android.content.Context
//import com.wilsonmontano.mvc1erparcial.modelo.Vehiculo
import com.wilsonmontano.mvc1erparcial.modelo.VehiculoModelo
import com.wilsonmontano.mvc1erparcial.ui.helpui.VehiculoUI
import com.wilsonmontano.mvc1erparcial.vista.VistaVehiculoInterface


class VehiculoControlador(
    private val context: Context,
    private val vista: VistaVehiculoInterface
) {
    private val vehiculo = VehiculoModelo(context)


    fun cargarVehiculos() {
        val lista = vehiculo.listar()
        val listaUI = lista.map { vehiculo ->

            VehiculoUI(
                id = vehiculo.id,
                marca = vehiculo.marca,
                modelo = vehiculo.modelo,
                año = vehiculo.año,
                tipo = vehiculo.tipo
            )
        }
        vista.actualizarLista(listaUI)
    }


    fun guardarVehiculo(marca: String, modelo: String, año: Int, tipo: String) {
        val vehiculo = VehiculoModelo.Vehiculo(marca = marca, modelo = modelo, año = año, tipo = tipo)
        this.vehiculo.insertar(vehiculo)
        vista.mostrarMensaje("Vehículo registrado con éxito")
        cargarVehiculos()
    }


    fun editarVehiculo(id: Int, marca: String, modelo: String, año: Int, tipo: String) {
        val vehiculo =
            VehiculoModelo.Vehiculo(id = id, marca = marca, modelo = modelo, año = año, tipo = tipo)
        this.vehiculo.actualizar(vehiculo)
        vista.mostrarMensaje("Vehículo actualizado con éxito")
        cargarVehiculos()
    }


    fun eliminarVehiculo(id: Int) {
        if (vehiculo.tieneMantenimientos(id)) {
            vista.mostrarMensaje("No se puede eliminar: el vehículo está asociado a mantenimientos.")
        } else {
            vehiculo.eliminar(id)
            vista.mostrarMensaje("Vehículo eliminado correctamente")
            cargarVehiculos()
        }
    }
}