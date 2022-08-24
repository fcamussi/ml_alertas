package fcamussi.mlalertas;

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
    TextView tvDetails1;
    TextView tvDetails2;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvDetails1 = view.findViewById(R.id.tv_item_details1);
        tvDetails2 = view.findViewById(R.id.tv_item_details2);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
        String state = cursor.getString(cursor.getColumnIndexOrThrow("state"));
        tvTitle.setText(title);
        tvDetails1.setText(String.format(Locale.US, "Provincia: %s", state));
        tvDetails2.setText(String.format(Locale.US, "Precio: %s %.2f", currency, price));
    }

}
