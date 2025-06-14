package com.wilsonmontano.mvc1erparcial.vista

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.VehiculoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.VehiculoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaFormularioVehiculo(
    vehiculoExistente: VehiculoUI?, // Cambiar el tipo a VehiculoUI
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val controlador = remember {
        VehiculoControlador(context, object : VistaVehiculoInterface {
            override fun actualizarLista(lista: List<VehiculoUI>) {}
            override fun mostrarMensaje(mensaje: String) {
                println(mensaje)
            }
        })
    }

    // Estados del formulario
    var marca by remember { mutableStateOf(vehiculoExistente?.marca ?: "") }
    var modelo by remember { mutableStateOf(vehiculoExistente?.modelo ?: "") }
    var anio by remember { mutableStateOf(vehiculoExistente?.año?.toString() ?: "") }
    var tipo by remember { mutableStateOf(vehiculoExistente?.tipo ?: "Auto") }
    val tipos = listOf("Auto", "Motocicleta", "Camión")
    var expanded by remember { mutableStateOf(false) }

    // ==== Botón Guardar ====
    val btnGuardar = @Composable {
        Button(onClick = {
            val añoInt = anio.toIntOrNull()
            if (marca.isNotBlank() && modelo.isNotBlank() && añoInt != null) {
                if (vehiculoExistente == null) {
                    controlador.guardarVehiculo(marca, modelo, añoInt, tipo)
                } else {
                    controlador.editarVehiculo(vehiculoExistente.id, marca, modelo, añoInt, tipo)
                }
                onVolver()
            }
        }) {
            Text("Guardar")
        }
    }

    // ==== Botón Cancelar ====
    val btnCancelar = @Composable {
        Button(onClick = onVolver) {
            Text("Cancelar")
        }
    }

    // ==== UI ====
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (vehiculoExistente == null) "Registrar Vehículo" else "Editar Vehículo",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") })
        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
        OutlinedTextField(value = anio, onValueChange = { anio = it }, label = { Text("Año") })

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = tipo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                tipos.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            tipo = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            btnGuardar()
            btnCancelar()
        }
    }
}