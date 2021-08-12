package com.example.dsuiza.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PersistenciaPlanillas extends SQLiteOpenHelper {


    ///////////////////////////////////////////////////////
    ////////// HARA LA PERSISTENCIA DE LOS CLIENTES

    private static final String MI_BASE_DE_DATOS = "planillas.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_PLANILLAS = "CREATE TABLE table_planillas" +
            "(idplanilla INTEGER,"+
            "idcliente INTEGER,"+
            "nombre TEXT,"+
            "direccion TEXT,"+
            "localidad TEXT,"+
            "provincia TEXT,"+
            "token TEXT,"+
            "orden INTEGER,"+
            "qr TEXT,"+
            "seleccion INTEGER)";

    public PersistenciaPlanillas(Context context, String s, Object o, int i) {
        super(context, MI_BASE_DE_DATOS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // aqui creamos las tablas
        db.execSQL(TABLA_PLANILLAS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS table_planillas");
        onCreate(db);

    }

    public void guardarPlanilla(Integer idplanilla, Integer idcliente, String nombre, String direccion
                                , String localidad,String  provincia, String token,Integer  orden
                                , String  qr, Integer seleccion) {

        ContentValues registro = new ContentValues();

        registro.put("idplanilla", idplanilla );
        registro.put("idcliente", idcliente );
        registro.put("nombre", nombre);
        registro.put("direccion",direccion );
        registro.put("localidad", localidad );
        registro.put("provincia", provincia);
        registro.put("token", token);
        registro.put("orden",orden );
        registro.put("qr", qr);
        registro.put("seleccion",seleccion );


        this.getWritableDatabase().insertOrThrow("table_planillas", "", registro);

    }



}
