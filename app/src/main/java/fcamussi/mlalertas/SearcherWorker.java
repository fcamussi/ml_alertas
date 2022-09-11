package fcamussi.mlalertas;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mlsearcher.MLSearcher;

/**
 * Clase SearcherWorker para realizar las búsquedas de forma periódica y notificar
 * mediante una notificación push si se encuentra un artículo nuevo
 *
 * @author Fernando Camussi
 */
public class SearcherWorker extends Worker {

    final static String CHANNEL_ID = UUID.randomUUID().toString();
    final static int NOTIFICATION_ID = 1;

    public SearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
        createNotificationChannel();
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public Result doWork() {
        DataBase dataBase = new DataBase(getApplicationContext());
        MLSearcher mlSearcher = new MLSearcher();
        List<Item> newItemList = new ArrayList<>();

        mlSearcher.setAgent(Constants.AGENT);
        List<Search> searches = dataBase.getAllSearches();
        for (Search search : searches) {
            if (search.isDeleted()) { // fue marcado para ser borrado
                dataBase.deleteSearch(search.getId());
                continue;
            }
            Frequency frequency = dataBase.getFrequency(search.getFrequencyId());
            int minutesCountdown = search.getMinutesCountdown();
            minutesCountdown -= Constants.SEARCHER_FREQUENCY_MINUTES;
            if (minutesCountdown <= 0) { // es momento de hacer la búsqueda
                mlSearcher.setSiteId(search.getSiteId());
                mlSearcher.setWords(search.getWordList());
                try {
                    mlSearcher.searchItems();
                } catch (Exception e) {
                    continue; // si falla por cualquier motivo continúa con la siguiente
                }
                List<Item> foundItems = new ArrayList<>(); /* para cargar los items de la búsqueda */
                for (Map<String, String> item : mlSearcher.getFoundItems()) {
                    foundItems.add(new Item(item));
                }
                dataBase.beginTransaction();
                try {
                    search = dataBase.getSearch(search.getId()); // porque el search puede haber cambiado
                    List<Item> itemList = dataBase.addNewItemsAndRemoveOldItems(search.getId(),
                            foundItems,
                            true);
                    if (itemList.size() > 0) { // hay nuevos
                        search.setNewItem(true);
                        newItemList.addAll(itemList);
                    }
                    search.setItemCount(dataBase.getItemCount(search.getId()));
                    search.setMinutesCountdown(frequency.getMinutes()); // se resetea el countdown
                    dataBase.updateSearch(search);
                    dataBase.setTransactionSuccessful();
                } finally {
                    dataBase.endTransaction();
                }
            } else { // todavía no es momento de hacer la búsqueda
                dataBase.beginTransaction();
                try {
                    search = dataBase.getSearch(search.getId()); // porque el search puede haber cambiado
                    search.setMinutesCountdown(minutesCountdown);
                    dataBase.updateSearch(search);
                    dataBase.setTransactionSuccessful();
                } finally {
                    dataBase.endTransaction();
                }
            }
        }
        if (newItemList.size() > 0) {
            /* Veo la cantidad de artículos nuevos y envío una notificación push */
            Set<String> itemIdSet = new HashSet<>(); // para eliminar duplicados
            for (Item item : newItemList) {
                itemIdSet.add(item.getId());
            }
            if (itemIdSet.size() == 1) {
                sendNotification("¡Nuevo artículo publicado!", newItemList.get(0).getTitle());
            }
            if (itemIdSet.size() > 1) {
                sendNotification("¡Nuevos artículos publicados!",
                        String.format("Hay %d artículos nuevos", itemIdSet.size()));
            }
            ThumbnailDownloader thumbnailDownloader = new ThumbnailDownloader(getApplicationContext());
            thumbnailDownloader.download();
        }

        sendBroadcast();
        return Result.success();
    }

    private void sendBroadcast() {
        Intent intent = new Intent(Constants.SEARCHER_FINISHED);
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta app lo reciba
        getApplicationContext().sendBroadcast(intent);
    }

    private void sendNotification(String title, String text) {
        /* Crea la notificación */
        Intent intent = new Intent(getApplicationContext(), SearchesActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.bell_push_100x100)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        /* Muestra la notificación */
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
