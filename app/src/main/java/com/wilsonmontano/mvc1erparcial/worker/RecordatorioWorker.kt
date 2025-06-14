package com.wilsonmontano.mvc1erparcial.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wilsonmontano.mvc1erparcial.R
import com.wilsonmontano.mvc1erparcial.modelo.MantenimientoModelo
import com.wilsonmontano.mvc1erparcial.modelo.VehiculoModelo
import java.time.LocalDate

class RecordatorioWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Obtener el ID del mantenimiento desde los datos de entrada
        val mantenimientoId = inputData.getInt("mantenimiento_id", -1)

        // Si el ID no es válido, retornar error
        if (mantenimientoId == -1) {
            return Result.failure()
        }

        // Consultar la base de datos para obtener el mantenimiento específico
        val dao = MantenimientoModelo(context)
        val mantenimiento = dao.buscarPorId(mantenimientoId)

        // Si no se encuentra el mantenimiento, retornar error
        if (mantenimiento == null) {
            return Result.failure()
        }

        // Verificar si el mantenimiento está programado para el día de hoy
        val hoy = LocalDate.now().toString()

        if (mantenimiento.estado == "programado" && mantenimiento.fecha == hoy) {
            // Si es el día del mantenimiento, mostrar la notificación
            val vehiculo = VehiculoModelo(context).buscarPorId(mantenimiento.idVehiculo)
            val titulo = "🔔 Recordatorio de Mantenimiento"
            val mensaje = "Hoy debes realizar el mantenimiento '${mantenimiento.nombre}' para el vehículo ${vehiculo?.marca ?: "Desconocido"} ${vehiculo?.modelo ?: ""}"
            mostrarNotificacion(titulo, mensaje, mantenimiento.id)
        }

        return Result.success()
    }

    // Método para mostrar la notificación
    private fun mostrarNotificacion(titulo: String, mensaje: String, id: Int) {
        val channelId = "mantenimiento_recordatorio"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de mantenimiento",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)  // Aquí puedes usar un ícono de tu preferencia
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        manager.notify(id, notification)
    }
}
