package com.wilsonmontano.mvc1erparcial.controlador

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.wilsonmontano.mvc1erparcial.R
import com.wilsonmontano.mvc1erparcial.modelo.*
import com.wilsonmontano.mvc1erparcial.ui.helpui.GastoUI
import com.wilsonmontano.mvc1erparcial.ui.helpui.MantenimientoUI
import com.wilsonmontano.mvc1erparcial.vista.VistaMantenimientoInterface
import com.wilsonmontano.mvc1erparcial.worker.RecordatorioWorker
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MantenimientoControlador(
    private val context: Context,
    private val vista: VistaMantenimientoInterface
) {
    private val mantenimiento = MantenimientoModelo(context)
    private val vehiculoModelo = VehiculoModelo(context)
    private val tallerModelo = TallerModelo(context)
    private val tipomantenimientoModelo = TipoMantenimientoModelo(context)
    private val gastoModelo = GastoModelo(context)

    fun obtenerVehiculosParaVista(): List<Pair<Int, String>> {
        return vehiculoModelo.listar().map { it.id to "${it.marca} ${it.modelo}" }
    }

    fun obtenerTalleresParaVista(): List<Pair<Int, String>> {
        return tallerModelo.listar().map { it.id to it.nombre }
    }

    fun obtenerTiposMantenimientoParaVista(): List<Pair<Int, String>> {
        return tipomantenimientoModelo.listar().map { it.id to it.nombre }
    }

    fun procesarFormulario(id: Int?, nombre: String, idVehiculo: Int, idTaller: Int, idTipoMantenimiento: Int, fecha: String, costo: Double?, notificar: Boolean, onResultado: (Boolean) -> Unit
    ) {
        if (nombre.isBlank() || fecha.isBlank() || costo == null || idVehiculo == 0 || idTaller == 0 || idTipoMantenimiento == 0) {
            vista.mostrarMensaje("Completa todos los campos correctamente.")
            onResultado(false)
            return
        }

        if (id == null) {
            guardarMantenimiento(nombre, idVehiculo, idTaller, idTipoMantenimiento, fecha, costo, notificar)
        } else {
            editarMantenimiento(id, nombre, idVehiculo, idTaller, idTipoMantenimiento, fecha, costo, notificar)
        }
        onResultado(true)
    }

    fun guardarMantenimiento(nombre: String, idVehiculo: Int, idTaller: Int, idTipoMantenimiento: Int, fecha: String, costo: Double, notificar: Boolean
    ) {
        val estado = if (fecha == LocalDate.now().toString()) "terminado" else "programado"
        val nuevo = MantenimientoModelo.Mantenimiento(
            idVehiculo = idVehiculo,
            idTaller = idTaller,
            idTipoMantenimiento = idTipoMantenimiento,
            nombre = nombre,
            fecha = fecha,
            costo = costo,
            estado = estado
        )
        mantenimiento.insertar(nuevo)

        if (notificar && estado == "programado") {
            mostrarNotificacionCreada(nombre, idVehiculo)
            generarRecordatorio(nuevo)
        }

        vista.mostrarMensaje("Mantenimiento registrado correctamente")
        cargarMantenimientos()//actualizar vista
    }

    private fun editarMantenimiento(
        id: Int,
        nombre: String,
        idVehiculo: Int,
        idTaller: Int,
        idTipoMantenimiento: Int,
        fecha: String,
        costo: Double,
        notificar: Boolean
    ) {
        val estado = if (fecha == LocalDate.now().toString()) "terminado" else "programado"
        val actualizado = MantenimientoModelo.Mantenimiento(
            id = id,
            idVehiculo = idVehiculo,
            idTaller = idTaller,
            idTipoMantenimiento = idTipoMantenimiento,
            nombre = nombre,
            fecha = fecha,
            costo = costo,
            estado = estado
        )
        mantenimiento.actualizar(actualizado)

        if (notificar && estado == "programado") {
            mostrarNotificacionCreada(nombre, idVehiculo)
        }

        vista.mostrarMensaje("Mantenimiento actualizado correctamente")
        cargarMantenimientos()
    }

    fun eliminarMantenimiento(id: Int) {
        mantenimiento.eliminar(id)
        vista.mostrarMensaje("Mantenimiento eliminado correctamente")
        cargarMantenimientos()
    }

    fun cargarMantenimientos(idVehiculo: Int? = null) {
        val listaOriginal = mantenimiento.listar().filter { mantenimiento ->
            idVehiculo == null || mantenimiento.idVehiculo == idVehiculo
        }

        val listaUI = listaOriginal.map { mantenimiento ->
            val vehiculo = vehiculoModelo.buscarPorId(mantenimiento.idVehiculo)
            val taller = tallerModelo.buscarPorId(mantenimiento.idTaller)
            val tipo = tipomantenimientoModelo.buscarPorId(mantenimiento.idTipoMantenimiento)
            val notificar = mantenimiento.estado == "programado"

            MantenimientoUI(id = mantenimiento.id, tipo = tipo?.nombre ?: "¿Tipo?", vehiculo = "${vehiculo?.marca ?: "¿?"} ${vehiculo?.modelo ?: ""}".trim(), taller = taller?.nombre ?: "¿Taller?", fecha = mantenimiento.fecha, costo = mantenimiento.costo, nombre = mantenimiento.nombre, estado = mantenimiento.estado, notificar = notificar
            )
        }

        vista.actualizarLista(listaUI)
        vista.actualizarVehiculosUI(obtenerVehiculosParaVista())
    }

    fun obtenerMantenimientoConGastos(idMantenimiento: Int): MantenimientoUI? {
        val mantenimiento = mantenimiento.buscarPorId(idMantenimiento) ?: return null
        val vehiculo = vehiculoModelo.buscarPorId(mantenimiento.idVehiculo)
        val taller = tallerModelo.buscarPorId(mantenimiento.idTaller)
        val tipo = tipomantenimientoModelo.buscarPorId(mantenimiento.idTipoMantenimiento)
        val gastos = gastoModelo.listarPorMantenimiento(idMantenimiento)
        val totalGasto = gastos.sumOf { it.itemCosto }

        val gastosUI = gastos.map {
            GastoUI(
                id = it.id,
                itemNombre = it.itemNombre,
                itemCosto = it.itemCosto,
                itemFecha = it.itemFecha,
                mantenimientoId = it.idMantenimiento,
                mantenimientoNombre = mantenimiento.nombre,
                vehiculo = "${vehiculo?.marca ?: "¿?"} ${vehiculo?.modelo ?: "¿?"}",
                total_gasto = totalGasto
            )
        }

        return MantenimientoUI(
            id = mantenimiento.id,
            tipo = tipo?.nombre ?: "¿Tipo?",
            vehiculo = "${vehiculo?.marca ?: "¿?"} ${vehiculo?.modelo ?: ""}".trim(),
            taller = taller?.nombre ?: "¿Taller?",
            fecha = mantenimiento.fecha,
            costo = mantenimiento.costo,
            nombre = mantenimiento.nombre,
            estado = mantenimiento.estado,
            notificar = mantenimiento.estado == "programado",
            gastos = gastosUI
        )
    }

    private fun generarRecordatorio(mantenimiento: MantenimientoModelo.Mantenimiento) {
        val fechaMantenimiento = LocalDate.parse(mantenimiento.fecha)
        val delayInMillis = ChronoUnit.DAYS.between(LocalDate.now(), fechaMantenimiento) * 24 * 60 * 60 * 1000

        if (delayInMillis > 0) {
            val workRequest = OneTimeWorkRequestBuilder<RecordatorioWorker>()
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("mantenimiento_id" to mantenimiento.id))
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    private fun mostrarNotificacionCreada(nombre: String, idVehiculo: Int) {
        val vehiculo = vehiculoModelo.buscarPorId(idVehiculo)
        val channelId = "recordatorio_guardado"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorio creado",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val texto = "$nombre programado para tu ${vehiculo?.marca ?: ""} ${vehiculo?.modelo ?: ""}"
        val noti = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("📅 Recordatorio creado")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), noti)
    }
}
