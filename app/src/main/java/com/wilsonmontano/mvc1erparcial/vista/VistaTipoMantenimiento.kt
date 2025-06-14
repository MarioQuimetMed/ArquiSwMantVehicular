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
import com.wilsonmontano.mvc1erparcial.controlador.TipoMantenimientoControlador
//import com.wilsonmontano.mvc1erparcial.modelo.Taller
//import com.wilsonmontano.mvc1erparcial.modelo.TipoMantenimiento
import com.wilsonmontano.mvc1erparcial.ui.helpui.TipoMantenimientoUI


@Composable
fun VistaTipoMantenimiento(
    navToFormulario: (TipoMantenimientoUI?) -> Unit  // Cambiar tipo de TipoMantenimiento a TipoMantenimientoUI
) {
    val context = LocalContext.current
    val listaTipos = remember { mutableStateListOf<TipoMantenimientoUI>() }

    val controlador = remember {
        TipoMantenimientoControlador(context, object : VistaTipoMantenimientoInterface {
            override fun actualizarLista(lista: List<TipoMantenimientoUI>) {
                listaTipos.clear()
                listaTipos.addAll(lista)  // Recibir los tipos convertidos a TipoMantenimientoUI
            }

            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(Unit) {
        controlador.cargarTipos()  // Cargar los tipos de mantenimiento procesados para la vista
    }

    // Botón para agregar tipo de mantenimiento
    val btnAgregar = @Composable {
        Button(onClick = { navToFormulario(null) }) {
            Text("Agregar Tipo de Mantenimiento")
        }
    }

    // Botón para editar tipo de mantenimiento
    val btnEditar = @Composable { tipo: TipoMantenimientoUI ->
        Button(onClick = { navToFormulario(tipo) }) {
            Text("Editar")
        }
    }

    // Botón para eliminar tipo de mantenimiento
    val btnEliminar = @Composable { tipo: TipoMantenimientoUI ->
        Button(onClick = { controlador.eliminarTipo(tipo.id) }) {
            Text("Eliminar")
        }
    }

    // UI
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista de Tipos de Mantenimiento", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        btnAgregar()
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaTipos) { tipo ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${tipo.nombre}")
                        Text("Descripción: ${tipo.descripcion}")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            btnEditar(tipo)
                            btnEliminar(tipo)
                        }
                    }
                }
            }
        }
    }
}

// ✅ Interfaz necesaria para el controlador
interface VistaTipoMantenimientoInterface {
    fun actualizarLista(lista: List<TipoMantenimientoUI>)  // Para actualizar la lista de tipos de mantenimiento
    fun mostrarMensaje(mensaje: String)  // Para mostrar mensajes de éxito o error
}