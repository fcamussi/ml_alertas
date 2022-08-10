package com.example.mlalertas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mlconsulta.Articulo;

public class BaseDatos {

    private static AdminSQLiteOpenHelper admin = null;
    private final SQLiteDatabase BD;

    public BaseDatos(Context context) {
        if (admin == null) {
            admin = new AdminSQLiteOpenHelper(context, "BD", null, 1);
        }
        BD = admin.getWritableDatabase();
    }

    public Cursor getCursorForAdapterBusqueda() { // ACA PUEDO PONER ORDER!
        Cursor cursor;
        cursor = BD.rawQuery("SELECT id_busqueda AS _id,palabras,nuevo FROM busquedas", null);
        return cursor;
    }

    public Busqueda getBusqueda(int id_busqueda) {
        Busqueda busqueda = new Busqueda();
        Cursor cursor;
        cursor = BD.rawQuery("SELECT * FROM busquedas WHERE id_busqueda=" + id_busqueda, null);
        if (cursor.moveToFirst()) {
            busqueda.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_busqueda")));
            busqueda.setPalabras(cursor.getString(cursor.getColumnIndexOrThrow("palabras")));
            busqueda.setArticuloNuevo(cursor.getInt(cursor.getColumnIndexOrThrow("nuevo")) > 0);
        }
        cursor.close();
        return busqueda;
    }

    public List<Busqueda> getBusquedas() {
        List<Busqueda> busquedasList = new ArrayList<>();

        Cursor cursor;
        cursor = BD.rawQuery("SELECT * FROM busquedas", null);
        if (cursor.moveToFirst()) {
            do {
                Busqueda busqueda = new Busqueda();
                busqueda.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_busqueda")));
                busqueda.setPalabras(cursor.getString(cursor.getColumnIndexOrThrow("palabras")));
                busqueda.setArticuloNuevo(cursor.getInt(cursor.getColumnIndexOrThrow("nuevo")) > 0);
                busquedasList.add(busqueda);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return busquedasList;
    }

    public void addBusqueda(Busqueda busqueda) {
        ContentValues registro = new ContentValues();
        registro.put("palabras", busqueda.getPalabras());
        registro.put("nuevo", busqueda.isArticuloNuevo());
        BD.insert("busquedas", null, registro);
    }

    public Cursor getCursorForAdapterArticulo(int id_busqueda) {
        Cursor cursor;
        cursor = BD.rawQuery("SELECT id_articulo AS _id,title,permalink FROM articulos WHERE id_busqueda=" + id_busqueda, null);
        return cursor;
    }

    public int addArticulos(int id_busqueda, List<Articulo> articuloList, boolean nuevo) {
        BD.execSQL("DELETE FROM articulos_tmp");
        for (Articulo articulo : articuloList) {
            ContentValues registro = new ContentValues();
            registro.put("id_busqueda", id_busqueda);
            registro.put("id_articulo", articulo.getId());
            registro.put("title", articulo.getTitle());
            registro.put("permalink", articulo.getPermalink());
            registro.put("nuevo", false);
            BD.insert("articulos_tmp", null, registro);
        }
        Cursor cursor;
        cursor = BD.rawQuery("SELECT * FROM articulos_tmp " +
                "WHERE id_articulo NOT IN (SELECT id_articulo FROM articulos WHERE id_busqueda=" + id_busqueda + ")", null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues registro = new ContentValues();
                registro.put("id_busqueda", id_busqueda);
                registro.put("id_articulo", cursor.getString(cursor.getColumnIndexOrThrow("id_articulo")));
                registro.put("title", cursor.getString(cursor.getColumnIndexOrThrow("title")));
                registro.put("permalink", cursor.getString(cursor.getColumnIndexOrThrow("permalink")));
                registro.put("nuevo", nuevo);
                BD.insert("articulos", null, registro);
            } while (cursor.moveToNext());
        }
        int cantidad = cursor.getCount();
        cursor.close();
        return cantidad;
    }

}
