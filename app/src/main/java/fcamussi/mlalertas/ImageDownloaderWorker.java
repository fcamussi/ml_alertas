package fcamussi.mlalertas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ImageDownloaderWorker extends Worker {

    public ImageDownloaderWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        DataBase dataBase = new DataBase(getApplicationContext());

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

        sendBroadcast(Constants.IMAGE_DOWNLOADER_FINISHED);
        return Result.success();
    }

    private void sendBroadcast(String broadcastMsg) {
        Intent intent = new Intent(broadcastMsg);
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta app lo reciba
        getApplicationContext().sendBroadcast(intent);
    }

}
