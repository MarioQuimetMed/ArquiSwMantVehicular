package com.wilsonmontano.mvc1erparcial.vista

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.vista.VistaTipoMantenimiento
import com.wilsonmontano.mvc1erparcial.controlador.TallerControlador
import com.wilsonmontano.mvc1erparcial.controlador.TipoMantenimientoControlador
//import com.wilsonmontano.mvc1erparcial.modelo.Taller
//import com.wilsonmontano.mvc1erparcial.modelo.TipoMantenimiento
import com.wilsonmontano.mvc1erparcial.ui.helpui.TipoMantenimientoUI

@Composable
fun VistaFormularioTipoMantenimiento(
    tipoExistente: TipoMantenimientoUI?,  // Cambiar el tipo de TipoMantenimiento a TipoMantenimientoUI
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val controlador = remember {
        TipoMantenimientoControlador(context, object : VistaTipoMantenimientoInterface {
            override fun actualizarLista(lista: List<TipoMantenimientoUI>) {}
            override fun mostrarMensaje(mensaje: String) {
                println(mensaje)
            }
        })
    }

    // Estados del formulario
    var nombre by remember { mutableStateOf(tipoExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(tipoExistente?.descripcion ?: "") }

    val btnGuardar = @Composable {
        Button(onClick = {
            if (nombre.isNotBlank() && descripcion.isNotBlank()) {
                if (tipoExistente == null) {
                    controlador.guardarTipo(nombre, descripcion)
                } else {
                    controlador.editarTipo(id = tipoExistente.id, nombre = nombre, descripcion = descripcion)
                }
                onVolver()
            }
        }) {
            Text("Guardar")
        }
    }

    val btnCancelar = @Composable {
        Button(onClick = onVolver) {
            Text("Cancelar")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (tipoExistente == null) "Registrar Tipo de Mantenimiento" else "Editar Tipo de Mantenimiento",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            btnGuardar()
            btnCancelar()
        }
    }
}
