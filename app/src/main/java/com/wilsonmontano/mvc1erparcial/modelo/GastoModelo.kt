package com.wilsonmontano.mvc1erparcial.modelo

import android.content.ContentValues
import android.content.Context
import com.wilsonmontano.mvc1erparcial.basededatos.MantenimientoVehicular

class GastoModelo(private val context: Context) {

    private val dbHelper = MantenimientoVehicular(context)


    data class Gasto(
        val id: Int = 0,
        val idMantenimiento: Int,
        val itemNombre: String,
        val itemCosto: Double,
        val itemFecha: String

    )

    fun insertar(gasto: Gasto) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("id_mantenimiento", gasto.idMantenimiento)
            put("item_nombre", gasto.itemNombre)
            put("item_costo", gasto.itemCosto)
            put("item_fecha", gasto.itemFecha)
        }
        db.insert("gasto", null, valores)
        db.close()
    }

    fun eliminar(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("gasto", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun listar(): List<Gasto> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Gasto>()
        val cursor = db.rawQuery("SELECT * FROM gasto", null)

        if (cursor.moveToFirst()) {
            do {
                val gasto = Gasto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    idMantenimiento = cursor.getInt(cursor.getColumnIndexOrThrow("id_mantenimiento")),
                    itemNombre = cursor.getString(cursor.getColumnIndexOrThrow("item_nombre")),
                    itemCosto = cursor.getDouble(cursor.getColumnIndexOrThrow("item_costo")),
                    itemFecha = cursor.getString(cursor.getColumnIndexOrThrow("item_fecha"))
                )
                lista.add(gasto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun listarPorMantenimiento(idMantenimiento: Int): List<Gasto> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Gasto>()
        val cursor = db.rawQuery("SELECT * FROM gasto WHERE id_mantenimiento = ?", arrayOf(idMantenimiento.toString()))

        if (cursor.moveToFirst()) {
            do {
                val gasto = Gasto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    idMantenimiento = cursor.getInt(cursor.getColumnIndexOrThrow("id_mantenimiento")),
                    itemNombre = cursor.getString(cursor.getColumnIndexOrThrow("item_nombre")),
                    itemCosto = cursor.getDouble(cursor.getColumnIndexOrThrow("item_costo")),
                    itemFecha = cursor.getString(cursor.getColumnIndexOrThrow("item_fecha"))
                )
                lista.add(gasto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}
