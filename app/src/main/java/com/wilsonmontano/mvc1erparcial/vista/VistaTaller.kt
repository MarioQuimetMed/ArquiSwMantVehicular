package com.wilsonmontano.mvc1erparcial.vista

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.TallerControlador
//import com.wilsonmontano.mvc1erparcial.modelo.Taller
import com.wilsonmontano.mvc1erparcial.ui.helpui.TallerUI


@Composable
fun VistaTaller(
    navToFormulario: (TallerUI?) -> Unit  // Cambiar tipo de Taller a TallerUI
) {
    val context = LocalContext.current
    val listaTalleres = remember { mutableStateListOf<TallerUI>() }

    val controlador = remember {
        TallerControlador(context, object : VistaTallerInterface {
            override fun actualizarLista(lista: List<TallerUI>) {
                listaTalleres.clear()
                listaTalleres.addAll(lista)  // Recibir la lista convertida a TallerUI
            }

            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(Unit) {
        controlador.cargarTalleres()  // Cargar los talleres procesados para la vista
    }

    // Botón para agregar taller
    val btnAgregar = @Composable {
        Button(onClick = { navToFormulario(null) }) {
            Text("Agregar Taller")
        }
    }

    // Botón para editar taller
    val btnEditar = @Composable { taller: TallerUI ->
        Button(onClick = { navToFormulario(taller) }) {
            Text("Editar")
        }
    }

    // Botón para eliminar taller
    val btnEliminar = @Composable { taller: TallerUI ->
        Button(onClick = { controlador.eliminarTaller(taller.id) }) {
            Text("Eliminar")
        }
    }

    // UI
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista de Talleres", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        btnAgregar()
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaTalleres) { taller ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${taller.nombre}")
                        Text("Dirección: ${taller.direccion}")
                        Text("Teléfono: ${taller.telefono}")
                        Text("Comentario: ${taller.comentario}")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            btnEditar(taller)
                            btnEliminar(taller)
                        }
                    }
                }
            }
        }
    }
}

// ✅ Interfaz necesaria para el controlador
interface VistaTallerInterface {
    fun actualizarLista(lista: List<TallerUI>)  // Para actualizar la lista de talleres
    fun mostrarMensaje(mensaje: String)         // Para mostrar mensajes de éxito o error
}