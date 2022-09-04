package fcamussi.mlalertas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import mlsearcher.MLSearcher;

public class ItemsActivity extends AppCompatActivity {

    private DataBase dataBase;
    private Cursor cursor;
    private ListView lv;
    private ItemCursorAdapter adapter;
    private int searchId;
    private BroadcastReceiver brCursorRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        searchId = getIntent().getIntExtra("search_id", 0);
        dataBase = new DataBase(this);
        lv = findViewById(R.id.items_lv);
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter = new ItemCursorAdapter(this, cursor);
        lv.setAdapter(adapter);
        Search search = dataBase.getSearch(searchId);
        getSupportActionBar().setTitle(MLSearcher.stringListToString(search.getWordList()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemId = view.getTag().toString();
                Intent intent = new Intent(getBaseContext(), ItemViewActivity.class);
                intent.putExtra("item_id", itemId);
                intent.putExtra("search_id", searchId);
                startActivity(intent);
                dataBase.beginTransaction();
                try {
                    Item item = dataBase.getItem(itemId, searchId);
                    item.setNewItem(false);
                    dataBase.updateItem(item);
                    if (dataBase.getNewItemCount(searchId) == 0) {
                        Search search = dataBase.getSearch(searchId);
                        search.setNewItem(false);
                        dataBase.updateSearch(search);
                    }
                    dataBase.setTransactionSuccessful();
                } finally {
                    dataBase.endTransaction();
                }
                cursor = dataBase.getCursorForAdapterItem(searchId);
                adapter.changeCursor(cursor);
            }
        });

        brCursorRefresh = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterItem(searchId);
                adapter.changeCursor(cursor);
            }
        };
        IntentFilter filter = new IntentFilter(Constants.SEARCHER_FINISHED);
        this.registerReceiver(brCursorRefresh, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter.changeCursor(cursor);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(brCursorRefresh);
        super.onDestroy();
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
