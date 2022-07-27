package com.example.mlalertas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BusquedaBD {

    SQLiteDatabase BD;

    public BusquedaBD(SQLiteDatabase BD) {
        this.BD = BD;
    }

    public Cursor getCursor() {
        Cursor cursor;
        cursor = BD.rawQuery("SELECT PALABRAS AS _id,PALABRAS FROM BUSQUEDAS", null);
        return cursor;
    }

    public long agregarBusqueda(String palabras) {
        ContentValues registro = new ContentValues();
        registro.put("PALABRAS", palabras);
        long id = BD.insert("BUSQUEDAS", null, registro);
        return id;
    }

    public void eliminarBusqueda() {

    }

    public long agregarArticulo(String id) {
/*puede agregar los existentes solamente usando otra tabla e informar si hay nuevos o usar
otra funcion que diga cantidad para comparar
 */
        return 0;
    }

}
