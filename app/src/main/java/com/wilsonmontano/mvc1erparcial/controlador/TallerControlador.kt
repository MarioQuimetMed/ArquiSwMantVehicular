package com.wilsonmontano.mvc1erparcial.controlador

import android.content.Context
//import com.wilsonmontano.mvc1erparcial.modelo.Taller
import com.wilsonmontano.mvc1erparcial.modelo.TallerModelo
import com.wilsonmontano.mvc1erparcial.ui.helpui.TallerUI
import com.wilsonmontano.mvc1erparcial.vista.VistaTallerInterface



class TallerControlador(
    private val context: Context,
    private val vista: VistaTallerInterface
) {
    private val taller = TallerModelo(context)


    fun cargarTalleres() {
        val lista = taller.listar()


        val listaUI = lista.map { taller ->
            TallerUI(
                id = taller.id,
                nombre = taller.nombre,
                direccion = taller.direccion,
                telefono = taller.telefono,
                comentario = taller.comentario
            )
        }

        // Enviar la lista de TallerUI a la vista para actualizar la UI
        vista.actualizarLista(listaUI)
    }

    // Guardar un nuevo taller
    fun guardarTaller(nombre: String, direccion: String, telefono: String, comentario: String) {
        val taller = TallerModelo.Taller(
            nombre = nombre,
            direccion = direccion,
            telefono = telefono,
            comentario = comentario
        )
        this.taller.insertar(taller)
        vista.mostrarMensaje("Taller registrado con éxito")
        cargarTalleres()
    }

    // Editar un taller existente
    fun editarTaller(id: Int, nombre: String, direccion: String, telefono: String, comentario: String) {
        val taller = TallerModelo.Taller(
            id = id,
            nombre = nombre,
            direccion = direccion,
            telefono = telefono,
            comentario = comentario
        )
        this.taller.actualizar(taller)
        vista.mostrarMensaje("Taller actualizado con éxito")
        cargarTalleres()
    }


    fun eliminarTaller(id: Int) {
        if (taller.tieneMantenimientos(id)) {
            vista.mostrarMensaje("No se puede eliminar: el taller está asociado a mantenimientos.")
        } else {
            taller.eliminar(id)
            vista.mostrarMensaje("Taller eliminado correctamente")
            cargarTalleres()
        }
    }
}