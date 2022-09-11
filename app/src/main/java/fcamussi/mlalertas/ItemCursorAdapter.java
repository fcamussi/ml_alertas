package fcamussi.mlalertas;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Clase ItemCursorAdapter
 *
 * @author Fernando Camussi
 */
public class ItemCursorAdapter extends CursorAdapter {

    TextView tvTitle;
    TextView tvDetails1;
    TextView tvDetails2;
    ImageView ivBell;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView ivThumbnail = view.findViewById(R.id.item_item_iv_thumbnail);
        tvTitle = view.findViewById(R.id.item_item_tv_title);
        tvDetails1 = view.findViewById(R.id.item_item_tv_details1);
        tvDetails2 = view.findViewById(R.id.item_item_tv_details2);
        ivBell = view.findViewById(R.id.item_item_iv_bell);
        String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
        String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        String state = cursor.getString(cursor.getColumnIndexOrThrow("state"));
        byte[] thumbnail = cursor.getBlob(cursor.getColumnIndexOrThrow("thumbnail"));
        boolean newItem = cursor.getInt(cursor.getColumnIndexOrThrow("new_item")) > 0;
        view.setTag(_id); // se utiliza el tag de view para almacenar el id del artículo
        if (thumbnail != null) { // se muestra la imagen
            Bitmap thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
            ivThumbnail.setImageBitmap(thumbnailBitmap);
        }
        tvTitle.setText(title);
        tvDetails1.setText(String.format("%s, %s", city, state));
        tvDetails2.setText(String.format("%s %,.2f", currency, price));
        ivBell.setVisibility(newItem ? View.VISIBLE : View.GONE); // se activa la campanita si es un artículo nuevo
    }

}
