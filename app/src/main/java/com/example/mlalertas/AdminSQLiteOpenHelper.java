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
        BD.execSQL("CREATE TABLE IF NOT EXISTS BUSQUEDAS(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "PALABRAS TEXT," +
                    "ARTICULO_NUEVO INTEGER)");
        BD.execSQL("CREATE TABLE IF NOT EXISTS ARTICULOS(" +
                    "_id INTEGER," +
                    "ID TEXT," +
                    "TITLE TEXT," +
                    "PERMALINK TEXT," +
                    "FOREIGN KEY(_id) REFERENCES BUSQUEDAS(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}