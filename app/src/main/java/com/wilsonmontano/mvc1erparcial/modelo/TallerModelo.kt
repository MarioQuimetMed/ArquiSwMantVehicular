package com.wilsonmontano.mvc1erparcial.modelo

import android.content.ContentValues
import android.content.Context
import com.wilsonmontano.mvc1erparcial.basededatos.MantenimientoVehicular

//import basededatos.MantenimientoVehicular

class TallerModelo(private val context: Context) {


    data class Taller(
        var id: Int = 0,
        var nombre: String = "",
        var direccion: String = "",
        var telefono: String = "",
        var comentario: String = ""
    )


    private val dbHelper = MantenimientoVehicular(context)

    fun insertar(taller: Taller): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", taller.nombre)
            put("direccion", taller.direccion)
            put("telefono", taller.telefono)
            put("comentario", taller.comentario)
        }
        val resultado = db.insert("taller", null, valores)
        db.close()
        return resultado
    }

    fun actualizar(taller: Taller): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", taller.nombre)
            put("direccion", taller.direccion)
            put("telefono", taller.telefono)
            put("comentario", taller.comentario)
        }
        val resultado = db.update("taller", valores, "id = ?", arrayOf(taller.id.toString()))
        db.close()
        return resultado
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("taller", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun listar(): List<Taller> {
        val lista = mutableListOf<Taller>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM taller", null)
        if (cursor.moveToFirst()) {
            do {
                val taller = Taller(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    direccion = cursor.getString(2),
                    telefono = cursor.getString(3),
                    comentario = cursor.getString(4)
                )
                lista.add(taller)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun buscarPorId(id: Int): Taller? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM taller WHERE id = ?", arrayOf(id.toString()))
        var taller: Taller? = null
        if (cursor.moveToFirst()) {
            taller = Taller(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                direccion = cursor.getString(2),
                telefono = cursor.getString(3),
                comentario = cursor.getString(4)
            )
        }
        cursor.close()
        db.close()
        return taller
    }


    fun tieneMantenimientos(idTaller: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM mantenimiento WHERE id_taller = ?",
            arrayOf(idTaller.toString())
        )
        var existe = false
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0
        }
        cursor.close()
        db.close()
        return existe
    }
}