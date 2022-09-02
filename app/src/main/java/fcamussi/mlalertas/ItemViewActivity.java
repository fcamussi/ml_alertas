package fcamussi.mlalertas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import mlsearcher.MLSearcher;

public class ItemViewActivity extends AppCompatActivity {

    private DataBase dataBase;
    private TextView tvTitle;
    private TextView tvDetail1;
    private TextView tvDetail2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        getSupportActionBar().setTitle("Ponga título aquí");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        int searchId = getIntent().getIntExtra("search_id", 0);
        String itemId =  getIntent().getStringExtra("search_id");
        dataBase = new DataBase(this);
        tvTitle = findViewById(R.id.item_view_tv_title);
        tvDetail1 = findViewById(R.id.item_view_tv_detail1);
        tvDetail2 = findViewById(R.id.item_view_tv_detail2);
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


    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
    //startActivity(browserIntent);