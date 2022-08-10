package com.example.mlalertas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BD) {
        BD.execSQL("CREATE TABLE IF NOT EXISTS busquedas(" +
                "id_busqueda INTEGER PRIMARY KEY AUTOINCREMENT," +
                "palabras TEXT," +
                "nuevo INTEGER)");
        BD.execSQL("CREATE TABLE IF NOT EXISTS articulos(" +
                "id_busqueda INTEGER," +
                "id_articulo TEXT," +
                "title TEXT," +
                "permalink TEXT," +
                "nuevo INTEGER," +
                "FOREIGN KEY(id_busqueda) REFERENCES busquedas(id_busqueda))");
        BD.execSQL("CREATE TABLE articulos_tmp AS SELECT * FROM articulos WHERE 1=2");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}