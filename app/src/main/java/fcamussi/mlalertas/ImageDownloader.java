package fcamussi.mlalertas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ImageDownloader {

    Context context;

    public ImageDownloader(Context context) {
        this.context = context;
    }

    public void download() {
        DataBase dataBase = new DataBase(context);

        List<Item> itemList = dataBase.getAllItemThumbnailNull();
        for (Item item : itemList) {
            try {
                InputStream is = new URL(item.getThumbnailLink()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                item.setThumbnail(os.toByteArray());
                os.close();
            } catch (IOException e) {
                item.setThumbnail(null);
            }
        }
        dataBase.beginTransaction();
        try {
            for (Item item : itemList) {
                Item itemUpdated = dataBase.getItem(item.getId(), item.getSearchId());
                itemUpdated.setThumbnail(item.getThumbnail());
                dataBase.updateItem(itemUpdated);
            }
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
    }

}
