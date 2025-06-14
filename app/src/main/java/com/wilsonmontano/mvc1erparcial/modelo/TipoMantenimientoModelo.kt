package com.wilsonmontano.mvc1erparcial.modelo

import android.content.ContentValues
import android.content.Context
import com.wilsonmontano.mvc1erparcial.basededatos.MantenimientoVehicular

class TipoMantenimientoModelo(private val context: Context) {

    data class TipoMantenimiento(
        var id: Int = 0,
        var nombre: String = "",
        var descripcion: String = ""
    )


    private val dbHelper = MantenimientoVehicular(context)

    fun insertar(tipo: TipoMantenimiento): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", tipo.nombre)
            put("descripcion", tipo.descripcion)
        }
        val resultado = db.insert("tipomantenimiento", null, valores)
        db.close()
        return resultado
    }

    fun actualizar(tipo: TipoMantenimiento): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", tipo.nombre)
            put("descripcion", tipo.descripcion)
        }
        val resultado = db.update(
            "tipomantenimiento",
            valores,
            "id = ?",
            arrayOf(tipo.id.toString())
        )
        db.close()
        return resultado
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete(
            "tipomantenimiento",
            "id = ?",
            arrayOf(id.toString())
        )
        db.close()
        return resultado
    }

    fun listar(): List<TipoMantenimiento> {
        val lista = mutableListOf<TipoMantenimiento>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tipomantenimiento", null)
        if (cursor.moveToFirst()) {
            do {
                val tipo = TipoMantenimiento(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    descripcion = cursor.getString(2)
                )
                lista.add(tipo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun buscarPorId(id: Int): TipoMantenimiento? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM tipomantenimiento WHERE id = ?",
            arrayOf(id.toString())
        )
        var tipo: TipoMantenimiento? = null
        if (cursor.moveToFirst()) {
            tipo = TipoMantenimiento(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                descripcion = cursor.getString(2)
            )
        }
        cursor.close()
        db.close()
        return tipo
    }

    fun tieneMantenimientos(idTipo: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM mantenimiento WHERE id_tipomantenimiento = ?",
            arrayOf(idTipo.toString())
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