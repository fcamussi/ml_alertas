package com.example.mlalertas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    EditText etWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etWords = findViewById(R.id.etWords);
    }

    public void onClick(View view) {
        Intent data = new Intent();
        String words = etWords.getText().toString();
        data.putExtra("words", words);
        setResult(RESULT_OK, data);
        finish();
    }

}