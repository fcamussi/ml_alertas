package fcamussi.mlalertas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    CheckBox cbWifi;
    CheckBox cbBatteryNotLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        getSupportActionBar().setTitle("Configuración");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cbWifi = findViewById(R.id.cb_wifi);
        cbBatteryNotLow = findViewById(R.id.cb_battery_not_low);
    }

    public void onClickBtnAccept(View view) {
        boolean wifi = cbWifi.isChecked();
        boolean batteryNotLow = cbBatteryNotLow.isChecked();
        Intent intent = new Intent();
        intent.putExtra("wifi", wifi);
        intent.putExtra("battery_not_low", batteryNotLow);
        setResult(RESULT_OK, intent);
        finish();
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
