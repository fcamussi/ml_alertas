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

    public Cursor getCursorBusquedas() {
        Cursor cursor;
        cursor = BD.rawQuery("SELECT _id,PALABRAS,ARTICULO_NUEVO FROM BUSQUEDAS", null);
        return cursor;
    }

    public List<Busqueda> getBusquedas() {
        List<Busqueda> busquedasList = new ArrayList<>();

        Cursor cursor = getCursorBusquedas();
        if (cursor.moveToFirst()) {
            do {
                Busqueda busqueda = new Busqueda();
                busqueda.setId(cursor.getInt(0));
                busqueda.setPalabras(cursor.getString(1));
                busqueda.setArticuloNuevo(cursor.getInt(2) > 0);
                busquedasList.add(busqueda);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return busquedasList;
    }

    public void addBusqueda(Busqueda busqueda) {
        ContentValues registro = new ContentValues();
        registro.put("PALABRAS", busqueda.getPalabras());
        registro.put("ARTICULO_NUEVO", busqueda.isArticuloNuevo());
        BD.insert("BUSQUEDAS", null, registro);
    }

    public void eliminarBusqueda() {
    }


    public Cursor getCursorArticulos(int id_busqueda) {
        Cursor cursor;
        String _id = String.valueOf(id_busqueda);
        cursor = BD.rawQuery("SELECT _id,ID,TITLE,PERMALINK FROM ARTICULOS WHERE _id=" + _id, null);
        return cursor;
    }

    public List<Articulo> getArticulos(int id_busqueda) {
        List<Articulo> articulosList = new ArrayList<>();

        Cursor cursor = getCursorArticulos(id_busqueda);
        if (cursor.moveToFirst()) {
            do {
                Articulo articulo = new Articulo();
                articulo.setId(cursor.getString(0));
                articulo.setTitle(cursor.getString(1));
                articulo.setPermalink(cursor.getString(2));
                articulosList.add(articulo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return articulosList;
    }

    public void addArticulo(int id_busqueda, Articulo articulo) {
        ContentValues registro = new ContentValues();
        registro.put("_id", String.valueOf(id_busqueda));
        registro.put("ID", articulo.getId());
        registro.put("TITLE", articulo.getTitle());
        registro.put("PERMALINK", articulo.getPermalink());
        BD.insert("ARTICULOS", null, registro);
    }

}
