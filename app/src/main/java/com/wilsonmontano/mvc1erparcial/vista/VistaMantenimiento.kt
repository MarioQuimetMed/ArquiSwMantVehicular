package com.wilsonmontano.mvc1erparcial.vista

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.MantenimientoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.MantenimientoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaMantenimiento(
    navToFormulario: (MantenimientoUI?) -> Unit,
    navToDetalle: (MantenimientoUI) -> Unit
)

 {
    val context = LocalContext.current
    val listaMantenimientos = remember { mutableStateListOf<MantenimientoUI>() }
    val vehiculosDisponibles = remember { mutableStateListOf<Pair<Int, String>>() }

    // Instancia del controlador
    val controlador = remember {
        MantenimientoControlador(context, object : VistaMantenimientoInterface {
            override fun actualizarLista(lista: List<MantenimientoUI>) {
                listaMantenimientos.clear()
                listaMantenimientos.addAll(lista)
            }

            override fun actualizarVehiculosUI(vehiculos: List<Pair<Int, String>>) {
                vehiculosDisponibles.clear()
                vehiculosDisponibles.addAll(vehiculos)
            }

            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    var vehiculoSeleccionado by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        controlador.cargarMantenimientos()
    }

    // Componente Dropdown para filtrar por vehículo
    val DropdownVehiculo = @Composable {
        var expanded by remember { mutableStateOf(false) }
        val seleccionado = vehiculosDisponibles.find { it.first == vehiculoSeleccionado }?.second ?: "Todos"

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = seleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por vehículo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Todos") }, onClick = {
                    vehiculoSeleccionado = -1
                    controlador.cargarMantenimientos()
                    expanded = false
                })
                vehiculosDisponibles.forEach { (id, nombre) ->
                    DropdownMenuItem(text = { Text(nombre) }, onClick = {
                        vehiculoSeleccionado = id
                        controlador.cargarMantenimientos(id)
                        expanded = false
                    })
                }
            }
        }
    }

    // Boton para agregar nuevo mantenimiento
    val btnAgregar = @Composable {
        Button(onClick = { navToFormulario(null) }) {
            Text("Registrar Mantenimiento")
        }
    }

    // Boton para editar mantenimiento existente
     val btnEditar = @Composable { mantenimiento: MantenimientoUI ->
         IconButton(onClick = { navToFormulario(mantenimiento) }) {
             Icon(Icons.Default.Edit, contentDescription = "Editar")
         }
     }

     val btnVerDetalle = @Composable { mantenimiento: MantenimientoUI ->
         IconButton(onClick = { navToDetalle(mantenimiento) }) {
             Icon(Icons.Default.Info, contentDescription = "Ver Detalles")
         }
     }


     // Botón para eliminar mantenimiento existente
    val btnEliminar = @Composable { mantenimiento: MantenimientoUI ->
        IconButton(onClick = { controlador.eliminarMantenimiento(mantenimiento.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
        }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista de Mantenimientos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        DropdownVehiculo()
        Spacer(modifier = Modifier.height(16.dp))

        btnAgregar()
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaMantenimientos) { mantenimiento ->
                val colorFondo = when (mantenimiento.estado.lowercase()) {
                    "terminado" -> Color(0xFFA5D6A7)
                    "programado" -> Color(0xFF90CAF9)
                    else -> Color.LightGray
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorFondo)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tipo: ${mantenimiento.tipo}")
                        Text("Vehículo: ${mantenimiento.vehiculo}")
                        Text("Estado: ${mantenimiento.estado}", color = Color.Black)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            btnEditar(mantenimiento)
                            btnEliminar(mantenimiento)
                            btnVerDetalle(mantenimiento)
                        }
                    }
                }
            }
        }
    }
}

interface VistaMantenimientoInterface {
    fun actualizarLista(lista: List<MantenimientoUI>)
    fun actualizarVehiculosUI(vehiculos: List<Pair<Int, String>>)
    fun mostrarMensaje(mensaje: String)
}