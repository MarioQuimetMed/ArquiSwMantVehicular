package com.wilsonmontano.mvc1erparcial.modelo

import android.content.ContentValues
import android.content.Context
import com.wilsonmontano.mvc1erparcial.basededatos.MantenimientoVehicular

class MantenimientoModelo(context: Context) {


    data class Mantenimiento(
        val id: Int = 0,
        val idVehiculo: Int,
        val idTaller: Int,
        val idTipoMantenimiento: Int, // ✅ nuevo campo para el tipo de mantenimiento
        val nombre: String,
        val fecha: String,
        val costo: Double,
        val estado: String,

        // Datos extendidos (no guardados en BD, usados solo para mostrar)
        var vehiculoInfo: String = "",
        var tallerInfo: String = "",
        var tipoInfo: String = ""

    )

    private val dbHelper = MantenimientoVehicular(context)

    fun insertar(mantenimiento: Mantenimiento) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("id_vehiculo", mantenimiento.idVehiculo)
            put("id_taller", mantenimiento.idTaller)
            put("id_tipomantenimiento", mantenimiento.idTipoMantenimiento) // ✅ NUEVO
            put("nombre", mantenimiento.nombre)
            put("fecha", mantenimiento.fecha)
            put("costo", mantenimiento.costo)
            put("estado", calcularEstado(mantenimiento.fecha))
        }
        db.insert("mantenimiento", null, valores)
        db.close()
    }

    fun actualizar(mantenimiento: Mantenimiento) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("id_vehiculo", mantenimiento.idVehiculo)
            put("id_taller", mantenimiento.idTaller)
            put("id_tipomantenimiento", mantenimiento.idTipoMantenimiento) // ✅ NUEVO
            put("nombre", mantenimiento.nombre)
            put("fecha", mantenimiento.fecha)
            put("costo", mantenimiento.costo)
            put("estado", calcularEstado(mantenimiento.fecha))
        }
        db.update(
            "mantenimiento",
            valores,
            "id = ?",
            arrayOf(mantenimiento.id.toString())
        )
        db.close()
    }

    fun eliminar(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("mantenimiento", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun listar(): List<Mantenimiento> {
        val lista = mutableListOf<Mantenimiento>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM mantenimiento", null)
        if (cursor.moveToFirst()) {
            do {
                val mantenimiento = Mantenimiento(
                    id = cursor.getInt(0),
                    idVehiculo = cursor.getInt(1),
                    idTaller = cursor.getInt(2),
                    idTipoMantenimiento = cursor.getInt(3), // ✅ NUEVO
                    nombre = cursor.getString(4),
                    fecha = cursor.getString(5),
                    costo = cursor.getDouble(6),
                    estado = cursor.getString(7)
                )
                lista.add(mantenimiento)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }


    private fun calcularEstado(fecha: String): String {
        val hoy = java.time.LocalDate.now()
        val fechaIngresada = java.time.LocalDate.parse(fecha)
        return if (fechaIngresada.isAfter(hoy)) "programado"
        else "terminado"
    }
    fun buscarPorId(id: Int): Mantenimiento? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM mantenimiento WHERE id = ?", arrayOf(id.toString()))
        var mantenimiento: Mantenimiento? = null

        if (cursor.moveToFirst()) {
            mantenimiento = Mantenimiento(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                idVehiculo = cursor.getInt(cursor.getColumnIndexOrThrow("id_vehiculo")),
                idTaller = cursor.getInt(cursor.getColumnIndexOrThrow("id_taller")),
                idTipoMantenimiento = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipomantenimiento")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            )
        }

        cursor.close()
        db.close()
        return mantenimiento
    }


}