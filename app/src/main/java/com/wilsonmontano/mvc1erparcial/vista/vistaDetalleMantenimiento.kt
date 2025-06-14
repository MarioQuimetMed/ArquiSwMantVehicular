package com.wilsonmontano.mvc1erparcial.vista

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wilsonmontano.mvc1erparcial.controlador.MantenimientoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.MantenimientoUI

@Composable
fun VistaDetallesMantenimiento(
    idMantenimiento: Int,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var mantenimiento by remember { mutableStateOf<MantenimientoUI?>(null) }

    // Interfaz compartida
    val interfaz = remember {
        object : VistaMantenimientoInterface {
            override fun actualizarLista(lista: List<MantenimientoUI>) {}
            override fun actualizarVehiculosUI(vehiculos: List<Pair<Int, String>>) {}
            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val controlador = remember {
        MantenimientoControlador(context, interfaz)
    }

    LaunchedEffect(idMantenimiento) {
        mantenimiento = controlador.obtenerMantenimientoConGastos(idMantenimiento)
    }

    mantenimiento?.let { datos ->
        val listaGastos = datos.gastos ?: emptyList()
        val totalGasto = listaGastos.sumOf { it.itemCosto }
        val totalFinal = datos.costo + totalGasto

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📋 Detalles del Mantenimiento", style = MaterialTheme.typography.headlineSmall)
            Divider()

            Text("🔧 Nombre: ${datos.nombre}")
            Text("📅 Fecha: ${datos.fecha}")
            Text("💲 Costo base: %.2f Bs".format(datos.costo))
            Text("🚗 Vehículo: ${datos.vehiculo}")
            Text("🏭 Taller: ${datos.taller}")
            Text("🛠️ Tipo de Mantenimiento: ${datos.tipo}")
            Text("📌 Estado: ${datos.estado}")

            Spacer(modifier = Modifier.height(16.dp))
            Text("🧾 Detalle de Gastos:", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaGastos) { gasto ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("• ${gasto.itemNombre}: ${gasto.itemCosto} Bs")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("🧮 Total gastos: %.2f Bs".format(totalGasto))
            Text("💰 Total final: %.2f Bs".format(totalFinal), style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onVolver, modifier = Modifier.align(Alignment.End)) {
                Text("Volver")
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingSpinner()
        }
    }
}

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(48.dp)) {
        rotate(degrees = 45f) {
            drawArc(
                color = Color.Gray,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Rect(0f, 0f, size.width, size.height).topLeft,
                size = size
            )
        }
    }
}
