package fcamussi.mlalertas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

/**
 * Clase ConfigurationActivity
 *
 * @author Fernando Camussi
 */
public class ConfigurationActivity extends AppCompatActivity {

    CheckBox cbWifi;
    CheckBox cbBatteryNotLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Configuración");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cbWifi = findViewById(R.id.configuration_cb_wifi);
        cbBatteryNotLow = findViewById(R.id.configuration_cb_battery_not_low);

        boolean wifi = getIntent().getBooleanExtra("wifi", true);
        boolean batteryNotLow = getIntent().getBooleanExtra("battery_not_low", true);
        cbWifi.setChecked(wifi);
        cbBatteryNotLow.setChecked(batteryNotLow);
    }

    public void onClickBtnSave(View view) {
        boolean wifi = cbWifi.isChecked();
        boolean batteryNotLow = cbBatteryNotLow.isChecked();
        Intent intent = new Intent();
        intent.putExtra("wifi", wifi);
        intent.putExtra("battery_not_low", batteryNotLow);
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
