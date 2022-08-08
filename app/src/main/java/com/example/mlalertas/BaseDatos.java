package com.example.mlalertas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class BaseDatos {

    private static AdminSQLiteOpenHelper admin = null;
    private SQLiteDatabase BD;

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

    public ArrayList<Busqueda> getBusquedas() {
        ArrayList<Busqueda> busquedasList = new ArrayList<>();

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

/*    public Cursor getCursorArticulos(id) {
        Cursor cursor;
        cursor = BD.rawQuery("SELECT _id,PALABRAS,ARTICULO_NUEVO FROM BUSQUEDAS", null);
        return cursor;
    }
*/

    public long agregarArticulo(String id) {
/*puede agregar los existentes solamente usando otra tabla e informar si hay nuevos o usar
otra funcion que diga cantidad para comparar
 */
        return 0;
    }

}
