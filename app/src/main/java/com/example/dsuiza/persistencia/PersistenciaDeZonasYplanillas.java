package com.example.dsuiza.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class
PersistenciaDeZonasYplanillas extends SQLiteOpenHelper {
    private static final String MI_BASE_DE_DATOS = "zonas.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_PLANILLAS = "CREATE TABLE table_zonas" +
            "(idplanilla INTEGER PRIMARY KEY,"+
            "posicion INTEGER,"+
            "zona TEXT,"+
            "fecha TEXT,"+
            "desde TEXT,"+
            "hasta TEXT)";

    public PersistenciaDeZonasYplanillas(Context context, String s, Object o, int i) {
        super(context, MI_BASE_DE_DATOS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // aqui creamos las tablas
        db.execSQL(TABLA_PLANILLAS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS table_zonas");
        onCreate(db);

    }

    public void guardarPlanillaXZona(Integer idplanilla, Integer posicion,
                                     String zona, String fecha, String desde
                                    ,String  hasta) {

        ContentValues registro = new ContentValues();

        registro.put("idplanilla", idplanilla );
        registro.put("posicion", posicion );
        registro.put("zona", zona);
        registro.put("fecha",fecha );
        registro.put("desde", desde );
        registro.put("hasta", hasta);


        this.getWritableDatabase().insertOrThrow("table_zonas", "", registro);

    }


}
