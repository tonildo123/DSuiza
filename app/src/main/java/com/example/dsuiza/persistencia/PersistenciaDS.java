package com.example.dsuiza.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class PersistenciaDS extends SQLiteOpenHelper {
    private static final String MI_BASE_DE_DATOS = "dbusuarios.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_USUARIOS = "CREATE TABLE table_usuarios" +
            "(id INTEGER PRIMARY KEY, user TEXT, password TEXT)";

    public PersistenciaDS(Context context, String s, Object o, int i) {
        super(context, MI_BASE_DE_DATOS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // aqui creamos las tablas
        db.execSQL(TABLA_USUARIOS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS table_usuarios");
        onCreate(db);

    }

    public void insertar_usuarios(String user, String pass) {
        ContentValues registro = new ContentValues();
        //registro.put("id", id);
        registro.put("user", user);
        registro.put("password", pass);

        this.getWritableDatabase().insertOrThrow("table_usuarios", "", registro);

    }




}
