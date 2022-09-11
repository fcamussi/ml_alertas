package fcamussi.mlalertas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase para descargar las imágenes thumbnails
 *
 * @author Fernando Camussi
 */
public class ThumbnailDownloader {

    private static final int nThreads = 5;
    private final Context context;

    /**
     * Constructor
     *
     * @param context contexto
     */
    public ThumbnailDownloader(Context context) {
        this.context = context;
    }

    /**
     * Descarga las imágenes thumbnails
     */
    public void download() {
        DataBase dataBase = new DataBase(context);
        List<Item> itemList = dataBase.getAllItemThumbnailIsNull();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Callable<Void>> callableList = new ArrayList<>();

        /* descargamos todas las imágenes */
        for (Item item : itemList) {
            callableList.add(() -> {
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
                return null; // porque el callable espera un tipo Void
            });
        }
        try {
            executorService.invokeAll(callableList);
        } catch (InterruptedException ignored) {
        } finally {
            executorService.shutdown();
        }

        /* las guardamos en la base de datos */
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
