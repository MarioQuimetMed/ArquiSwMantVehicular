package com.wilsonmontano.mvc1erparcial.modelo


import android.content.ContentValues
import android.content.Context
import com.wilsonmontano.mvc1erparcial.basededatos.MantenimientoVehicular

class RecordatorioDao(private val context: Context) {

    private val dbHelper = MantenimientoVehicular(context)

    fun insertar(recordatorio: Recordatorio): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("id_mantenimiento", recordatorio.idMantenimiento)
            put("fecha_recordatorio", recordatorio.fechaRecordatorio)
            put("descripcion", recordatorio.descripcion)
            put("estado", recordatorio.estado)
        }
        val resultado = db.insert("recordatorio", null, valores)
        db.close()
        return resultado
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("recordatorio", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun actualizarEstado(id: Int, nuevoEstado: String): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        val resultado = db.update(
            "recordatorio",
            valores,
            "id = ?",
            arrayOf(id.toString())
        )
        db.close()
        return resultado
    }

    fun actualizarEstadoPorMantenimiento(idMantenimiento: Int, nuevoEstado: String): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        val resultado = db.update(
            "recordatorio",
            valores,
            "id_mantenimiento = ?",
            arrayOf(idMantenimiento.toString())
        )
        db.close()
        return resultado
    }

    fun listarNotificables(hoy: String): List<Recordatorio> {
        val lista = mutableListOf<Recordatorio>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM recordatorio WHERE fecha_recordatorio <= ? AND estado = 'pendiente'",
            arrayOf(hoy)
        )
        if (cursor.moveToFirst()) {
            do {
                val recordatorio = Recordatorio(
                    id = cursor.getInt(0),
                    idMantenimiento = cursor.getInt(1),
                    fechaRecordatorio = cursor.getString(2),
                    descripcion = cursor.getString(3),
                    estado = cursor.getString(4)
                )
                lista.add(recordatorio)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
