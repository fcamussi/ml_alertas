package fcamussi.mlalertas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase ItemViewActivity
 *
 * @author Fernando Camussi
 */
public class ItemViewActivity extends AppCompatActivity {

    private Button btnOpenInML;
    private String permalink;
    private String thumbnailLink;
    private Bitmap thumbnailBitmap;
    private ActivityResultLauncher<Intent> openInMLLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        String itemId = getIntent().getStringExtra("item_id");
        int searchId = getIntent().getIntExtra("search_id", 0);
        DataBase dataBase = new DataBase(this);
        Item item = dataBase.getItem(itemId, searchId);
        Objects.requireNonNull(getSupportActionBar()).setTitle(item.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView tvTitle = findViewById(R.id.item_view_tv_title);
        ImageView ivThumbnail = findViewById(R.id.item_view_iv_thumbnail);
        TextView tvDetails1 = findViewById(R.id.item_view_tv_details1);
        TextView tvDetails2 = findViewById(R.id.item_view_tv_details2);
        btnOpenInML = findViewById(R.id.item_view_btn_open_in_ml);
        tvTitle.setText(item.getTitle());
        byte[] thumbnail = item.getThumbnail();
        if (thumbnail != null) { // muestro la imagen
            Bitmap thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
            ivThumbnail.setImageBitmap(thumbnailBitmap);
        }
        String detail1 = String.format("%s, %s", item.getCity(), item.getState());
        @SuppressLint("DefaultLocale") String detail2 = String.format("%s %,.2f", item.getCurrency(), item.getPrice());
        tvDetails1.setText(detail1);
        tvDetails2.setText(detail2);
        permalink = item.getPermalink();
        /* descargo y muestro una imagen thumbnail de mejor calidad */
        thumbnailLink = item.getThumbnailLink();
        thumbnailLink = thumbnailLink.substring(0, thumbnailLink.length() - 5) + "H.jpg";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                InputStream in = new URL(thumbnailLink).openStream();
                thumbnailBitmap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                thumbnailBitmap = null;
            }
            handler.post(() -> {
                if (thumbnailBitmap != null) {
                    ivThumbnail.setImageBitmap(thumbnailBitmap);
                }
            });
        });

        openInMLLauncher = registerForActivityResult(  // para evitar multiples clicks
                new ActivityResultContracts.StartActivityForResult(),
                result -> btnOpenInML.setEnabled(true));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickBtnOpenInML(View view) {
        btnOpenInML.setEnabled(false); // para evitar multiples clicks
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(permalink));
        openInMLLauncher.launch(intent);
    }

}
