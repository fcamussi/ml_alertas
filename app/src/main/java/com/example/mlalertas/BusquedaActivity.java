package com.example.mlalertas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class BusquedaActivity extends AppCompatActivity {

    EditText etPalabras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        etPalabras = findViewById(R.id.etPalabras);
    }

    public void onClick(View view) {
        Intent data = new Intent();
        String palabras = etPalabras.getText().toString();
        data.putExtra("palabras", palabras);
        setResult(RESULT_OK, data);
        finish();
    }

}