package fcamussi.mlalertas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import mlsearcher.MLSearcher;

/**
 * Clase ItemsActivity
 *
 * @author Fernando Camussi
 */
public class ItemsActivity extends AppCompatActivity {

    private DataBase dataBase;
    private Cursor cursor;
    private ItemCursorAdapter adapter;
    private int searchId;
    private BroadcastReceiver brCursorRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        searchId = getIntent().getIntExtra("search_id", 0);
        dataBase = new DataBase(this);
        ListView lv = findViewById(R.id.items_lv);
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter = new ItemCursorAdapter(this, cursor);
        lv.setAdapter(adapter);
        String title = MLSearcher.stringListToString(dataBase.getSearch(searchId).getWordList());
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            String itemId = view.getTag().toString(); // se obtiene el id del artículo del tag del view
            Intent intent = new Intent(getBaseContext(), ItemViewActivity.class);
            intent.putExtra("item_id", itemId);
            intent.putExtra("search_id", searchId);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // para evitar multiples clicks
            startActivity(intent);
            /* desmarco notificación del artículo visto */
            dataBase.beginTransaction();
            try {
                List<Item> itemList = dataBase.getItemsById(itemId);
                for (Item item : itemList) {
                    item.setNewItem(false);
                    dataBase.updateItem(item);
                    if (dataBase.getNewItemCount(item.getSearchId()) == 0) {
                        Search search = dataBase.getSearch(item.getSearchId());
                        search.setNewItem(false);
                        dataBase.updateSearch(search);
                    }
                }
                dataBase.setTransactionSuccessful();
            } finally {
                dataBase.endTransaction();
            }
            cursor = dataBase.getCursorForAdapterItem(searchId);
            adapter.changeCursor(cursor);
        });

        brCursorRefresh = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterItem(searchId);
                adapter.changeCursor(cursor);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ADD_SEARCH_FINISHED);
        filter.addAction(Constants.SEARCHER_FINISHED);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
