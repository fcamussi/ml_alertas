package fcamussi.mlalertas;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import mlsearcher.MLSearcher;

public class ItemsActivity extends AppCompatActivity {

    private DataBase dataBase;
    private Cursor cursor;
    private ListView lv;
    private ItemCursorAdapter adapter;
    private int searchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        searchId = getIntent().getIntExtra("search_id", 0);
        dataBase = new DataBase(this);
        lv = findViewById(R.id.lv_items);
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter = new ItemCursorAdapter(this, cursor);
        lv.setAdapter(adapter);
        Search search = dataBase.getSearch(searchId);
        getSupportActionBar().setTitle(MLSearcher.stringListToString(search.getWordList()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter.changeCursor(cursor);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
