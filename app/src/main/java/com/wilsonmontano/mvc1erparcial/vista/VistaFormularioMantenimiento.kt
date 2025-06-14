import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wilsonmontano.mvc1erparcial.controlador.MantenimientoControlador
import com.wilsonmontano.mvc1erparcial.ui.helpui.MantenimientoUI
import com.wilsonmontano.mvc1erparcial.vista.VistaMantenimientoInterface
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaFormularioMantenimiento(
    mantenimientoExistente: MantenimientoUI?,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    val controlador = remember {
        MantenimientoControlador(context, object : VistaMantenimientoInterface {
            override fun actualizarLista(lista: List<MantenimientoUI>) {}
            override fun actualizarVehiculosUI(vehiculos: List<Pair<Int, String>>) {}
            override fun mostrarMensaje(mensaje: String) {
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    val vehiculos = remember { controlador.obtenerVehiculosParaVista() }
    val talleres = remember { controlador.obtenerTalleresParaVista() }
    val tipos = remember { controlador.obtenerTiposMantenimientoParaVista() }

    var nombre by remember { mutableStateOf(mantenimientoExistente?.nombre ?: "") }
    var fecha by remember { mutableStateOf(mantenimientoExistente?.fecha ?: "") }
    var costo by remember { mutableStateOf(mantenimientoExistente?.costo?.toString() ?: "") }

    var idVehiculo by remember { mutableStateOf(vehiculos.firstOrNull { it.second == mantenimientoExistente?.vehiculo }?.first ?: 0) }
    var idTaller by remember { mutableStateOf(talleres.firstOrNull { it.second == mantenimientoExistente?.taller }?.first ?: 0) }
    var idTipo by remember { mutableStateOf(tipos.firstOrNull { it.second == mantenimientoExistente?.tipo }?.first ?: 0) }

    var expandedVehiculo by remember { mutableStateOf(false) }
    var expandedTaller by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }

    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val fechaOriginal = mantenimientoExistente?.fecha ?: ""
    val notificarOriginal = mantenimientoExistente?.notificar ?: false
    val fechaModificada = fecha != fechaOriginal
    val puedeEditarRecordatorio = fechaModificada && fecha > LocalDate.now().toString()

    var notificar by remember {
        mutableStateOf(
            if (!fechaModificada && mantenimientoExistente?.estado == "programado") notificarOriginal else false
        )
    }

    val DropdownVehiculo = @Composable {
        ExposedDropdownMenuBox(expanded = expandedVehiculo, onExpandedChange = { expandedVehiculo = !expandedVehiculo }) {
            OutlinedTextField(
                value = vehiculos.find { it.first == idVehiculo }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehículo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedVehiculo) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expandedVehiculo, onDismissRequest = { expandedVehiculo = false }) {
                vehiculos.forEach { (id, desc) ->
                    DropdownMenuItem(text = { Text(desc) }, onClick = {
                        idVehiculo = id
                        expandedVehiculo = false
                    })
                }
            }
        }
    }

    val DropdownTaller = @Composable {
        ExposedDropdownMenuBox(expanded = expandedTaller, onExpandedChange = { expandedTaller = !expandedTaller }) {
            OutlinedTextField(
                value = talleres.find { it.first == idTaller }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Taller") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTaller) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expandedTaller, onDismissRequest = { expandedTaller = false }) {
                talleres.forEach { (id, nombre) ->
                    DropdownMenuItem(text = { Text(nombre) }, onClick = {
                        idTaller = id
                        expandedTaller = false
                    })
                }
            }
        }
    }

    val DropdownTipoMantenimiento = @Composable {
        ExposedDropdownMenuBox(expanded = expandedTipo, onExpandedChange = { expandedTipo = !expandedTipo }) {
            OutlinedTextField(
                value = tipos.find { it.first == idTipo }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Mantenimiento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expandedTipo, onDismissRequest = { expandedTipo = false }) {
                tipos.forEach { (id, nombre) ->
                    DropdownMenuItem(text = { Text(nombre) }, onClick = {
                        idTipo = id
                        expandedTipo = false
                    })
                }
            }
        }
    }

    val btnGuardar = @Composable {
        Button(onClick = {
            val costoDouble = costo.toDoubleOrNull()
            controlador.procesarFormulario(
                mantenimientoExistente?.id,
                nombre,
                idVehiculo,
                idTaller,
                idTipo,
                fecha,
                costoDouble,
                notificar
            ) { exito ->
                if (exito) onVolver()
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

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (mantenimientoExistente == null) "Registrar Mantenimiento" else "Editar Mantenimiento",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })

        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            label = { Text("Fecha") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker.value = true }
        )

        if (showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker.value = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            fecha = localDate.toString()
                        }
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker.value = false }) {
                        Text("Cancelar")
                    }
                },
                properties = DialogProperties()
            ) {
                DatePicker(state = datePickerState)
            }
        }

        OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo (Bs)") })

        DropdownVehiculo()
        DropdownTaller()
        DropdownTipoMantenimiento()

        if (puedeEditarRecordatorio || (!fechaModificada && notificar)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = notificar, onCheckedChange = {
                    if (puedeEditarRecordatorio) notificar = it
                })
                Text("Activar recordatorio para esta fecha")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            btnGuardar()
            btnCancelar()
        }
    }
}