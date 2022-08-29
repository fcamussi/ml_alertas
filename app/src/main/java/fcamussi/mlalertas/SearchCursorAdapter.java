package fcamussi.mlalertas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;


public class SearchCursorAdapter extends CursorAdapter {

    TextView tvWords;
    TextView tvDetails1;
    TextView tvDetails2;
    ImageView iv;

    public SearchCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_search, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvWords = view.findViewById(R.id.tv_words);
        tvDetails1 = view.findViewById(R.id.tv_search_details1);
        tvDetails2 = view.findViewById(R.id.tv_search_details2);
        iv = view.findViewById(R.id.iv_search);
        int _id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String words = cursor.getString(cursor.getColumnIndexOrThrow("words"));
        String siteId = cursor.getString(cursor.getColumnIndexOrThrow("site_id"));
        String frequencyId = cursor.getString(cursor.getColumnIndexOrThrow("frequency_id"));
        int itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("item_count"));
        boolean newItem = cursor.getInt(cursor.getColumnIndexOrThrow("new_item")) > 0;
        view.setTag(String.valueOf(_id));
        tvWords.setText(words);
        String details1 = String.format(Locale.US, "Sitio: %s | Frequencia: %s", siteId, frequencyId);
        String details2 = String.format(Locale.US, "Cantidad: %d", itemCount);
        tvDetails1.setText(details1);
        tvDetails2.setText(details2);
        iv.setVisibility(newItem ? View.VISIBLE : View.GONE);
    }

}
