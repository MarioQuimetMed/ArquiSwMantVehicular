package com.wilsonmontano.mvc1erparcial.basededatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MantenimientoVehicular(context: Context):
    SQLiteOpenHelper(context,
        MantenimientoVehicular.DATABASE_NAME, null,
        MantenimientoVehicular.DATABASE_VERSION
    ){

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tablas en SQLite
        db.execSQL(CREAR_TABLA_VEHICULO)
        db.execSQL(CREAR_TABLA_TALLER)
        db.execSQL(CREAR_TABLA_MANTENIMIENTO)
        db.execSQL(CREAR_TABLA_GASTO)
        db.execSQL(CREAR_TABLA_TIPOMANTENIMIENTO)
        db.execSQL(CREAR_TABLA_RECORDATORIO)

    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Manejo de actualizaciones de base de datos (por ejemplo, migraciones)
        db.execSQL("DROP TABLE IF EXISTS $TABLA_VEHICULO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_TALLER")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_MANTENIMIENTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_GASTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_TIPOMANTENIMIENTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_RECORDATORIO")

        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 20
        const val DATABASE_NAME = "mantenimientovehicular.db"

        // Definición de tablas
        private const val TABLA_VEHICULO = "vehiculo"
        private const val TABLA_TALLER= "taller"
        private const val TABLA_MANTENIMIENTO = "mantenimiento"
        private const val TABLA_GASTO = "gasto"
        private const val TABLA_TIPOMANTENIMIENTO = "tipomantenimiento"
        private const val TABLA_RECORDATORIO = "recordatorio"
        // Sentencias SQL para crear las tablas
        private const val CREAR_TABLA_VEHICULO = """
            CREATE TABLE $TABLA_VEHICULO (
                id INTEGER PRIMARY KEY,
                marca TEXT,
                modelo TEXT,
                año INTEGER,
                tipo TEXT
            );
        """
        private const val CREAR_TABLA_TALLER = """
            CREATE TABLE $TABLA_TALLER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                direccion TEXT,
                telefono TEXT,
                comentario TEXT
            );
        """

        private const val CREAR_TABLA_MANTENIMIENTO = """
            CREATE TABLE $TABLA_MANTENIMIENTO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_vehiculo INTEGER,
                id_taller INTEGER,
                id_tipomantenimiento INTEGER,
                nombre TEXT,
                fecha TEXT,
                costo REAL,
                estado TEXT,
                FOREIGN KEY(id_vehiculo) REFERENCES $TABLA_VEHICULO(id),
                FOREIGN KEY(id_taller) REFERENCES $TABLA_TALLER(id),
                FOREIGN KEY(id_tipomantenimiento) REFERENCES $TABLA_TIPOMANTENIMIENTO(id)
            );
        """

        private const val CREAR_TABLA_TIPOMANTENIMIENTO = """
            CREATE TABLE $TABLA_TIPOMANTENIMIENTO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                descripcion TEXT
            );
        """

        private const val CREAR_TABLA_GASTO = """
            CREATE TABLE $TABLA_GASTO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_mantenimiento INTEGER,
                item_nombre TEXT,
                item_costo REAL,
                item_fecha TEXT,
                FOREIGN KEY(id_mantenimiento) REFERENCES $TABLA_MANTENIMIENTO(id)
            );
        """

        private const val CREAR_TABLA_RECORDATORIO = """
            CREATE TABLE $TABLA_RECORDATORIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_mantenimiento INTEGER,
                fecha_recordatorio TEXT,
                descripcion TEXT,
                estado TEXT,

            FOREIGN KEY(id_mantenimiento) REFERENCES mantenimiento(id) ON DELETE CASCADE
            );
        """
    }
}