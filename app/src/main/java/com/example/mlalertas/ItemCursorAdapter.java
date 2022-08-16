package com.example.mlalertas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Locale;

public class ItemCursorAdapter extends CursorAdapter {

    TextView tvTitle;
    TextView tvPrice;
    TextView tvState;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvState = view.findViewById(R.id.tvState);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
        String state = cursor.getString(cursor.getColumnIndexOrThrow("state"));
        tvTitle.setText(title);
        tvPrice.setText(String.format(Locale.US, "%s %.2f", currency, price));
        tvState.setText(state);
    }

}