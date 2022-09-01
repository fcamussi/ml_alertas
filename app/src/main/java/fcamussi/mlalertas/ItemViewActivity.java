package fcamussi.mlalertas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
    }
}


    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
    //startActivity(browserIntent);