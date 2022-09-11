package fcamussi.mlalertas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddSearchActivity extends AppCompatActivity {

    EditText etWords;
    Spinner spinnerSite;
    Spinner spinnerFrequency;
    ImageView ivWordsInfo;
    ImageView ivFrequencyInfo;
    SharedPreferences preferences;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_search);

        getSupportActionBar().setTitle(getString(R.string.add_search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataBase = new DataBase(this);
        etWords = findViewById(R.id.add_search_et_words);
        spinnerSite = findViewById(R.id.add_search_sp_site);
        spinnerFrequency = findViewById(R.id.add_search_sp_frequency);
        ivWordsInfo = findViewById(R.id.add_search_iv_words_info);
        ivFrequencyInfo = findViewById(R.id.add_search_iv_frequency_info);
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

        ivWordsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.popup);
                TextView tvMessage = dialog.findViewById(R.id.popup_tv_message);
                tvMessage.setText(getString(R.string.words_information));
                dialog.show();
            }
        });

        ivFrequencyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.popup);
                TextView tvMessage = dialog.findViewById(R.id.popup_tv_message);
                tvMessage.setText(getString(R.string.frequency_information));
                dialog.show();
            }
        });

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
            Toast.makeText(this, getString(R.string.must_enter_least_one_word), Toast.LENGTH_SHORT).show();
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
