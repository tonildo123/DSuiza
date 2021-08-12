package com.example.dsuiza.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistenciaDeComprobantes extends SQLiteOpenHelper {


    private static final String MI_BASE_DE_DATOS = "comprobantes.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_COMPROBANTES = "CREATE TABLE table_comprobantes" +
            "(idplanilla INTEGER,"+
            "idcliente INTEGER,"+
            "idventa INTEGER,"+
            "pedido TEXT,"+
            "rubro TEXT,"+
            "bultos TEXT,"+
            "fecha TEXT,"+
            "idplanilladetalle INTEGER PRIMARY KEY)";

    public PersistenciaDeComprobantes(Context context, String s, Object o, int i) {
        super(context, MI_BASE_DE_DATOS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // aqui creamos las tablas
        db.execSQL(TABLA_COMPROBANTES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS table_comprobantes");
        onCreate(db);

    }

    public void guardarcomprobantes(Integer idplanilla, Integer idcliente,Integer idventa, String pedido, String rubro,
                    String bultos, String fecha, Integer idplanilladetalle) {

        ContentValues registro = new ContentValues();

        registro.put("idplanilla", idplanilla );
        registro.put("idcliente", idcliente );
        registro.put("idventa", idventa );
        registro.put("pedido", pedido );
        registro.put("rubro", rubro );
        registro.put("bultos", bultos );
        registro.put("fecha", fecha );
        registro.put("idplanilladetalle", idplanilladetalle );




        this.getWritableDatabase().insertOrThrow("table_comprobantes", "", registro);

    }


}
