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
    Spinner spinnerFrequency;
    SharedPreferences preferences;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_search);

        getSupportActionBar().setTitle("Agregar búsqueda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataBase = new DataBase(this);
        etWords = findViewById(R.id.add_search_et_words);
        spinnerSite = findViewById(R.id.add_search_sp_site);
        spinnerFrequency = findViewById(R.id.add_search_sp_frequency);
        Cursor cursorSite = dataBase.getCursorForAdapterSite();
        SimpleCursorAdapter adapterSite = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                cursorSite,
                new String[]{"site_id_name"},
                new int[]{android.R.id.text1},
                0);
        spinnerSite.setAdapter(adapterSite);
        Cursor cursorFrequency = dataBase.getCursorForAdapterFrequency();
        SimpleCursorAdapter adapterFrequency = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                cursorFrequency,
                new String[]{"_id"},
                new int[]{android.R.id.text1},
                0);
        spinnerFrequency.setAdapter(adapterFrequency);

        preferences = getSharedPreferences("add_search_activity", Context.MODE_PRIVATE);
        String words = preferences.getString("words", "");
        int spinnerSiteIndex = preferences.getInt("spinner_site_index", 0);
        int spinnerFrequencyIndex = preferences.getInt("spinner_frequency_index", spinnerFrequency.getCount()-1);
        etWords.setText(words);
        spinnerSite.setSelection(spinnerSiteIndex);
        spinnerFrequency.setSelection(spinnerFrequencyIndex);
    }

    public void onClickBtnAdd(View view) {
        if (etWords.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar al menos una palabra", Toast.LENGTH_SHORT).show();
        } else {
            String words = etWords.getText().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("words", words);
            editor.putInt("spinner_site_index", spinnerSite.getSelectedItemPosition());
            editor.putInt("spinner_frequency_index", spinnerFrequency.getSelectedItemPosition());
            editor.apply();
            Intent intent = new Intent();
            Cursor cursorSite = (Cursor) spinnerSite.getSelectedItem();
            Cursor cursorFrequency = (Cursor) spinnerFrequency.getSelectedItem();
            String siteId = cursorSite.getString(cursorSite.getColumnIndexOrThrow("_id"));
            String frequencyId = cursorFrequency.getString(cursorFrequency.getColumnIndexOrThrow("_id"));
            intent.putExtra("words", words);
            intent.putExtra("site_id", siteId);
            intent.putExtra("frequency_id", frequencyId);
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
