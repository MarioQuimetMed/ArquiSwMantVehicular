package com.wilsonmontano.mvc1erparcial.controlador

import android.content.Context
//import com.wilsonmontano.mvc1erparcial.modelo.TipoMantenimiento
import com.wilsonmontano.mvc1erparcial.modelo.TipoMantenimientoModelo
import com.wilsonmontano.mvc1erparcial.ui.helpui.TipoMantenimientoUI
import com.wilsonmontano.mvc1erparcial.vista.VistaTipoMantenimientoInterface

class TipoMantenimientoControlador(
    private val context: Context,
    private val vista: VistaTipoMantenimientoInterface
) {
    private val tipomantenimiento = TipoMantenimientoModelo(context)


    fun cargarTipos() {
        val lista = tipomantenimiento.listar()


        val listaUI = lista.map { tipo ->
            TipoMantenimientoUI(
                id = tipo.id,
                nombre = tipo.nombre,
                descripcion = tipo.descripcion
            )
        }

        // Enviar la lista de TipoMantenimientoUI a la vista para actualizar la UI
        vista.actualizarLista(listaUI)
    }


    fun guardarTipo(nombre: String, descripcion: String) {
        val tipo =
            TipoMantenimientoModelo.TipoMantenimiento(nombre = nombre, descripcion = descripcion)
        tipomantenimiento.insertar(tipo)
        vista.mostrarMensaje("Tipo de mantenimiento registrado con éxito")
        cargarTipos()
    }


    fun editarTipo(id: Int, nombre: String, descripcion: String) {
        val tipo = TipoMantenimientoModelo.TipoMantenimiento(
            id = id,
            nombre = nombre,
            descripcion = descripcion
        )
        tipomantenimiento.actualizar(tipo)  // Actualizar el tipo de mantenimiento en la base de datos
        vista.mostrarMensaje("Tipo de mantenimiento actualizado con éxito")
        cargarTipos()  // Recargar la lista después de editar
    }

    // Eliminar un tipo de mantenimiento
    fun eliminarTipo(id: Int) {
        if (tipomantenimiento.tieneMantenimientos(id)) {
            vista.mostrarMensaje("No se puede eliminar: el tipo está asociado a mantenimientos.")
        } else {
            tipomantenimiento.eliminar(id)  // Eliminar el tipo de mantenimiento de la base de datos
            vista.mostrarMensaje("Tipo de mantenimiento eliminado correctamente")
            cargarTipos()  // Recargar la lista después de eliminar
        }
    }
}