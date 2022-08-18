package fcamussi.mlalertas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import mlsearcher.MLSearcher;

public class AddSearchActivity extends AppCompatActivity {

    EditText etWords;
    Spinner spinnerSite;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_search);

        dataBase = new DataBase(this);
        etWords = findViewById(R.id.et_words);
        spinnerSite = findViewById(R.id.spinner_site);
        Cursor cursor = dataBase.getCursorForAdapterSite();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                cursor,
                new String[]{"name"},
                new int[]{android.R.id.text1},
                0);
        spinnerSite.setAdapter(adapter);
        getSupportActionBar().setTitle("Agregar búsqueda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        String words = etWords.getText().toString();
        Cursor cursor = (Cursor) spinnerSite.getSelectedItem();
        String siteId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        intent.putExtra("words", words);
        intent.putExtra("site_id", siteId);
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
