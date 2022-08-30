package fcamussi.mlalertas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mlsearcher.MLSearcher;

public class ItemsActivity extends AppCompatActivity {

    private DataBase dataBase;
    private Cursor cursor;
    private ListView lv;
    private ItemCursorAdapter adapter;
    private int searchId;
    private BroadcastReceiver broadcastReceiver;

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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterItem(searchId);
                adapter.changeCursor(cursor);
            }
        };
        IntentFilter filter = new IntentFilter(Constants.SEARCHER_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter.changeCursor(cursor);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
