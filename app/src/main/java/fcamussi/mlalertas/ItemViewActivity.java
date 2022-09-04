package fcamussi.mlalertas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ItemViewActivity extends AppCompatActivity {

    private Button btnOpenInML;
    private String permalink;
    private ActivityResultLauncher<Intent> openInMLLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        String itemId = getIntent().getStringExtra("item_id");
        int searchId = getIntent().getIntExtra("search_id", 0);
        DataBase dataBase = new DataBase(this);
        Item item = dataBase.getItem(itemId, searchId);
        getSupportActionBar().setTitle(item.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView tvTitle = findViewById(R.id.item_view_tv_title);
        ImageView ivThumbnail = findViewById(R.id.item_view_iv_thumbnail);
        TextView tvDetails1 = findViewById(R.id.item_view_tv_details1);
        TextView tvDetails2 = findViewById(R.id.item_view_tv_details2);
        btnOpenInML = findViewById(R.id.item_view_btn_open_in_ml);
        tvTitle.setText(item.getTitle());
        byte[] thumbnail = item.getThumbnail();
        if (thumbnail != null) {
            Bitmap thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
            ivThumbnail.setImageBitmap(thumbnailBitmap);
        }
        String detail1 = String.format(Locale.US, "Provincia: %s", item.getState());
        String detail2 = String.format(Locale.US, "Precio: %s %s",
                item.getCurrency(),
                item.getPrice());
        tvDetails1.setText(detail1);
        tvDetails2.setText(detail2);
        permalink = item.getPermalink();

        openInMLLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    btnOpenInML.setEnabled(true);
                });
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

    public void onClickBtnOpenInML(View view) {
        btnOpenInML.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(permalink));
        openInMLLauncher.launch(intent);
    }

}
