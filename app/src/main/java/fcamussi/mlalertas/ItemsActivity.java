package fcamussi.mlalertas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import mlsearcher.MLSearcher;

/**
 * Clase ItemsActivity
 *
 * @author Fernando Camussi
 */
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
                String itemId = view.getTag().toString(); // se obtiene el id del artículo del tag del view
                Intent intent = new Intent(getBaseContext(), ItemViewActivity.class);
                intent.putExtra("item_id", itemId);
                intent.putExtra("search_id", searchId);
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
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = dataBase.getCursorForAdapterItem(searchId);
        adapter.changeCursor(cursor);
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
