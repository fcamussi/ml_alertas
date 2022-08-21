package fcamussi.mlalertas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddSearchActivity extends AppCompatActivity {

    EditText etWords;
    Spinner spinnerSite;
    SharedPreferences preferences;
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
                android.R.layout.simple_spinner_dropdown_item,
                cursor,
                new String[]{"site_id_name"},
                new int[]{android.R.id.text1},
                0);
        spinnerSite.setAdapter(adapter);
        getSupportActionBar().setTitle("Agregar búsqueda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = getSharedPreferences("add_search_activity", Context.MODE_PRIVATE);
        int spinnerIndex = preferences.getInt("spinner_index", 0);
        spinnerSite.setSelection(spinnerIndex);
    }

    public void onClickBtnAdd(View view) {
        if (etWords.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar al menos una palabra", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("spinner_index", spinnerSite.getSelectedItemPosition());
            editor.commit();
            Intent intent = new Intent();
            String words = etWords.getText().toString();
            Cursor cursor = (Cursor) spinnerSite.getSelectedItem();
            String siteId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            intent.putExtra("words", words);
            intent.putExtra("site_id", siteId);
            setResult(RESULT_OK, intent);
            finish();
        }
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
