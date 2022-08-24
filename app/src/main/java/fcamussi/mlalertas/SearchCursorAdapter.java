package fcamussi.mlalertas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class SearchCursorAdapter extends CursorAdapter {

    TextView tvWords;
    TextView tvSiteId;
    TextView tvFrequencyId;
    TextView tvItemCount;

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
        tvSiteId = view.findViewById(R.id.tv_site_id);
        tvFrequencyId = view.findViewById(R.id.tv_frequency_id);
        tvItemCount = view.findViewById((R.id.tv_item_count));
        int _id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String words = cursor.getString(cursor.getColumnIndexOrThrow("words"));
        String siteId = cursor.getString(cursor.getColumnIndexOrThrow("site_id"));
        String frequencyId = cursor.getString(cursor.getColumnIndexOrThrow("frequency_id"));
        int itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("item_count"));
        view.setTag(String.valueOf(_id));
        tvWords.setText(words);
        tvSiteId.setText(siteId);
        tvFrequencyId.setText(frequencyId);
        tvItemCount.setText(String.valueOf(itemCount));
    }

}
