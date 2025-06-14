package com.wilsonmontano.mvc1erparcial.vista


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.TallerControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.TallerUI

@Composable
fun VistaFormularioTaller(
    tallerExistente: TallerUI?,  // Cambiar el tipo de Taller a TallerUI
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val controlador = remember {
        TallerControlador(context, object : VistaTallerInterface {
            override fun actualizarLista(lista: List<TallerUI>) {}
            override fun mostrarMensaje(mensaje: String) {
                println(mensaje)
            }
        })
    }

    // Estados del formulario
    var nombre by remember { mutableStateOf(tallerExistente?.nombre ?: "") }
    var direccion by remember { mutableStateOf(tallerExistente?.direccion ?: "") }
    var telefono by remember { mutableStateOf(tallerExistente?.telefono ?: "") }
    var comentario by remember { mutableStateOf(tallerExistente?.comentario ?: "") }

    // Botón Guardar
    val btnGuardar = @Composable {
        Button(onClick = {
            if (nombre.isNotBlank() && direccion.isNotBlank()) {
                if (tallerExistente == null) {
                    controlador.guardarTaller(nombre, direccion, telefono, comentario)
                } else {
                    controlador.editarTaller(
                        id = tallerExistente.id,
                        nombre = nombre,
                        direccion = direccion,
                        telefono = telefono,
                        comentario = comentario
                    )
                }
                onVolver()
            }
        }) {
            Text("Guardar")
        }
    }

    // Botón Cancelar
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
            text = if (tallerExistente == null) "Registrar Taller" else "Editar Taller",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
        OutlinedTextField(value = comentario, onValueChange = { comentario = it }, label = { Text("Comentario") })

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            btnGuardar()
            btnCancelar()
        }
    }
}