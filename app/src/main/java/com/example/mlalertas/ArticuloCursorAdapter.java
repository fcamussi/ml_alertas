package com.example.mlalertas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import mlconsulta.Articulo;

public class ArticuloCursorAdapter extends CursorAdapter {

    TextView tvTitulo;

    public ArticuloCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_articulo, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvTitulo = view.findViewById(R.id.tvTitulo);
        String titulo = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        tvTitulo.setText(titulo);
    }

}