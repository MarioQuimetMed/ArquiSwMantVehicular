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
import com.wilsonmontano.mvc1erparcial.controlador.VehiculoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.VehiculoUI


@Composable
fun VistaVehiculo(
    navToFormulario: (VehiculoUI?) -> Unit // Cambiar tipo de Vehiculo a VehiculoUI
) {
    val context = LocalContext.current
    val listaVehiculos = remember { mutableStateListOf<VehiculoUI>() }

    val controlador = remember {
        VehiculoControlador(context, object : VistaVehiculoInterface {
            override fun actualizarLista(lista: List<VehiculoUI>) {
                listaVehiculos.clear()
                listaVehiculos.addAll(lista)
            }

            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(Unit) {
        controlador.cargarVehiculos() // Cargar la lista de vehículos convertida a VehiculoUI
    }

    // Botón para agregar vehículo
    val btnAgregar = @Composable {
        Button(onClick = { navToFormulario(null) }) {
            Text("Agregar Vehículo")
        }
    }

    // Botón para editar vehículo
    val btnEditar = @Composable { vehiculo: VehiculoUI ->
        Button(onClick = { navToFormulario(vehiculo) }) {
            Text("Editar")
        }
    }

    // Botón para eliminar vehículo
    val btnEliminar = @Composable { vehiculo: VehiculoUI ->
        Button(onClick = { controlador.eliminarVehiculo(vehiculo.id) }) {
            Text("Eliminar")
        }
    }

    // UI
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista de Vehículos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        btnAgregar()
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaVehiculos) { vehiculo ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Marca: ${vehiculo.marca}")
                        Text("Modelo: ${vehiculo.modelo}")
                        Text("Año: ${vehiculo.año}")
                        Text("Tipo: ${vehiculo.tipo}")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            btnEditar(vehiculo)
                            btnEliminar(vehiculo)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

}
interface VistaVehiculoInterface {
    fun actualizarLista(lista: List<VehiculoUI>) // Para actualizar la lista de vehículos
    fun mostrarMensaje(mensaje: String)          // Para mostrar mensajes de éxito o error
}