package com.wilsonmontano.mvc1erparcial.modelo

import android.content.ContentValues
import android.content.Context
import com.wilsonmontano.mvc1erparcial.basededatos.MantenimientoVehicular

//import modelo.MantenimientoVehicular
class VehiculoModelo(private val context: Context) {

    data class Vehiculo(
        var id: Int = 0,
        var marca: String = "",
        var modelo: String = "",
        var año: Int = 0,
        var tipo: String = "" // Ej: "Auto", "Motocicleta", "Camión"
    )

    private val dbHelper = MantenimientoVehicular(context)

    fun insertar(vehiculo: Vehiculo): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("marca", vehiculo.marca)
            put("modelo", vehiculo.modelo)
            put("año", vehiculo.año)
            put("tipo", vehiculo.tipo)
        }
        val resultado = db.insert("vehiculo", null, valores)
        db.close()
        return resultado
    }

    fun actualizar(vehiculo: Vehiculo): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("marca", vehiculo.marca)
            put("modelo", vehiculo.modelo)
            put("año", vehiculo.año)
            put("tipo", vehiculo.tipo)
        }
        val resultado = db.update("vehiculo", valores, "id = ?", arrayOf(vehiculo.id.toString()))
        db.close()
        return resultado
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("vehiculo", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun listar(): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vehiculo", null)
        if (cursor.moveToFirst()) {
            do {
                val vehiculo = Vehiculo(
                    id = cursor.getInt(0),
                    marca = cursor.getString(1),
                    modelo = cursor.getString(2),
                    año = cursor.getInt(3),
                    tipo = cursor.getString(4)
                )
                lista.add(vehiculo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun buscarPorId(id: Int): Vehiculo? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vehiculo WHERE id = ?", arrayOf(id.toString()))
        var vehiculo: Vehiculo? = null
        if (cursor.moveToFirst()) {
            vehiculo = Vehiculo(
                id = cursor.getInt(0),
                marca = cursor.getString(1),
                modelo = cursor.getString(2),
                año = cursor.getInt(3),
                tipo = cursor.getString(4)
            )
        }
        cursor.close()
        db.close()
        return vehiculo
    }


    fun tieneMantenimientos(idVehiculo: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM mantenimiento WHERE id_vehiculo = ?",
            arrayOf(idVehiculo.toString())
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