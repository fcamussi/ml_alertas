package com.example.mlalertas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class BusquedaCursorAdapter extends CursorAdapter {

    TextView tvPalabras;
    TextView tvDetalles;

    public BusquedaCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_busqueda, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvPalabras = view.findViewById(R.id.tvPalabras);
        tvDetalles = view.findViewById(R.id.tvDetalles);
        String palabras = cursor.getString(cursor.getColumnIndexOrThrow("PALABRAS"));
        String detalles = "";
        tvPalabras.setText(palabras);
        tvDetalles.setText(detalles);
    }

}