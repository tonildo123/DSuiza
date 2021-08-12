package com.example.dsuiza.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistenciaDeTokens extends SQLiteOpenHelper {


    private static final String MI_BASE_DE_DATOS = "dbtokens.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_TOKENS = "CREATE TABLE table_tokens" +
            "(id INTEGER PRIMARY KEY," +
            " token TEXT," +
            " fecha TEXT," +
            " hora TEXT)";

    public PersistenciaDeTokens(Context context, String s, Object o, int i) {
        super(context, MI_BASE_DE_DATOS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // aqui creamos las tablas
        db.execSQL(TABLA_TOKENS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS table_tokens");
        onCreate(db);

    }

    public void insertarToken(String token, String fecha, String hora) {
        ContentValues registro = new ContentValues();

        registro.put("token", token);
        registro.put("fecha", fecha);
        registro.put("hora", hora);


        this.getWritableDatabase().insertOrThrow("table_tokens", "", registro);

    }

}
