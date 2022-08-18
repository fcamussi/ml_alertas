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
        tvWords = view.findViewById(R.id.tvWords);
        tvItemCount = view.findViewById((R.id.tvItemCount));
        int _id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String words = cursor.getString(cursor.getColumnIndexOrThrow("words"));
        view.setTag(String.valueOf(_id));
        tvWords.setText(words);
        tvItemCount.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("item_count"))));
    }

}
