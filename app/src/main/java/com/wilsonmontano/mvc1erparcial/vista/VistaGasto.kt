package com.wilsonmontano.mvc1erparcial.vista

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.GastoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.GastoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaGasto(
    navToFormularioGasto: (Int) -> Unit
) {
    val context = LocalContext.current
    val listaMantenimientos = remember { mutableStateListOf<Pair<Int, String>>() }
    val vehiculos = remember { mutableStateListOf<Pair<Int, String>>() }

    val controlador = remember {
        GastoControlador(context, object : VistaGastoInterface {
            override fun actualizarLista(lista: List<GastoUI>) {
            }

            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    var idVehiculoSeleccionado by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        vehiculos.clear()
        vehiculos.add(-1 to "Todos")
        vehiculos.addAll(controlador.obtenerVehiculosParaVista())
        listaMantenimientos.clear()
        listaMantenimientos.addAll(controlador.obtenerMantenimientosPorVehiculo(null))
    }

    val DropdownVehiculo = @Composable {
        var expanded by remember { mutableStateOf(false) }
        val seleccionado = vehiculos.find { it.first == idVehiculoSeleccionado }?.second ?: "Seleccionar"

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = seleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por Vehículo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                vehiculos.forEach { (id, nombre) ->
                    DropdownMenuItem(text = { Text(nombre) }, onClick = {
                        idVehiculoSeleccionado = id
                        listaMantenimientos.clear()
                        listaMantenimientos.addAll(
                            if (id == -1) controlador.obtenerMantenimientosPorVehiculo(null)
                            else controlador.obtenerMantenimientosPorVehiculo(id)
                        )
                        expanded = false
                    })
                }
            }
        }
    }

    val BtnInfo = @Composable { idMantenimiento: Int ->
        IconButton(onClick = { navToFormularioGasto(idMantenimiento) }) {
            Icon(Icons.Default.Info, contentDescription = "Ver Gastos")
        }
    }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Gastos por Mantenimiento", style = MaterialTheme.typography.headlineSmall)

        DropdownVehiculo()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaMantenimientos) { (id, nombre) ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Mantenimiento: $nombre")
                        }
                        BtnInfo(id)
                    }
                }
            }
        }
    }
}

interface VistaGastoInterface {
    fun actualizarLista(lista: List<GastoUI>)
    fun mostrarMensaje(mensaje: String)
}