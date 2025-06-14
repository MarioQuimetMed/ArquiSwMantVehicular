package com.wilsonmontano.mvc1erparcial.vista

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.GastoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.GastoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaFormularioGasto(
    idMantenimiento: Int,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val listaGastos = remember { mutableStateListOf<GastoUI>() }

    val controlador = remember {
        GastoControlador(context, object : VistaGastoInterface {
            override fun actualizarLista(lista: List<GastoUI>) {
                listaGastos.clear()
                listaGastos.addAll(lista)
            }

            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoCosto by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        controlador.cargarGastos(idMantenimiento)
    }

    val BtnGuardarGasto = @Composable {
        Button(onClick = {
            val costoDouble = nuevoCosto.toDoubleOrNull()
            if (nuevoNombre.isNotBlank() && costoDouble != null) {
                controlador.guardarGasto(nuevoNombre, costoDouble, idMantenimiento)
                nuevoNombre = ""
                nuevoCosto = ""
            }
        }) {
            Text("Añadir Gasto")
        }
    }

    val BtnEliminarGasto = @Composable { gasto: GastoUI ->
        IconButton(onClick = { controlador.eliminarGasto(gasto.id, idMantenimiento) }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }

    val BtnVolver = @Composable {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Formulario de Gastos", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = nuevoNombre,
            onValueChange = { nuevoNombre = it },
            label = { Text("Nombre del gasto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nuevoCosto,
            onValueChange = { nuevoCosto = it },
            label = { Text("Costo (Bs)") },
            modifier = Modifier.fillMaxWidth()
        )

        BtnGuardarGasto()

        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaGastos) { gasto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Item: ${gasto.itemNombre}")
                        Text("Costo: ${gasto.itemCosto} Bs")
                        Text("Fecha: ${gasto.itemFecha}")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            BtnEliminarGasto(gasto)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BtnVolver()
    }
}