package com.example.dsuiza.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistenciaDeSalida extends SQLiteOpenHelper {



    private static final String MI_BASE_DE_DATOS = "salidas.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_SALIDAS = "CREATE TABLE table_salidas" +
            "(token_tmp TEXT,"+
            "idplanilladetalle_par INTEGER,"+
            "entregada_par BOOLEAN,"+
            "fecha_par DATATIME,"+
            "observaciones_par TEXT,"+
            "firma_par TEXTT,"+
            "foto_par TEXT,"+
            "latitud_par TEXT,"+
            "longitud_par TEXT,"+
            "idmotivo_par INTEGER)";

    public PersistenciaDeSalida(Context context, String s, Object o, int i) {
        super(context, MI_BASE_DE_DATOS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // aqui creamos las tablas
        db.execSQL(TABLA_SALIDAS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS table_salidas");
        onCreate(db);

    }

    public void guardarSalidas(String token_tmp, Integer idplanilladetalle_par,boolean entregada_par,
                               String fecha_par, String observaciones_par,
                               String firma_par, String foto_par,
                               String latitud_par, String longitud_par, Integer idmotivo_par) {

        ContentValues registro = new ContentValues();

        registro.put("token_tmp", token_tmp );
        registro.put("idplanilladetalle_par", idplanilladetalle_par );
        registro.put("entregada_par", entregada_par );
        registro.put("fecha_par", fecha_par );
        registro.put("observaciones_par", observaciones_par );
        registro.put("firma_par", firma_par );
        registro.put("foto_par", foto_par );
        registro.put("latitud_par", latitud_par );
        registro.put("longitud_par", longitud_par );
        registro.put("idmotivo_par", idmotivo_par );

        this.getWritableDatabase().insertOrThrow("table_salidas", "", registro);

    }

}
