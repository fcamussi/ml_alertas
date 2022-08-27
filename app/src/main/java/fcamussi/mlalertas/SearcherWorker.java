package fcamussi.mlalertas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import mlsearcher.Item;
import mlsearcher.MLSearcher;

public class SearcherWorker extends Worker {

    final static String CHANNEL_ID = UUID.randomUUID().toString();
    final static int NOTIFICATION_ID = 1;

    public SearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
        createNotificationChannel();
    }

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
                }
                List<Item> foundItems = mlSearcher.getFoundItems();
                List itemList = dataBase.addItems(search.getId(), foundItems, true);
                if (itemList.size() > 0) {
                    search.setNewItem(true);
                    newItemList.addAll(itemList);
                }
                search.setItemCount(dataBase.getItemCount(search.getId()));
                search.setMinutesCountdown(frequency.getMinutes()); // se resetea el countdown
            } else { // todavía no es momento de hacer la búsqueda
                search.setMinutesCountdown(minutesCountdown);
            }
            dataBase.updateSearch(search);
        }
        if (newItemList.size() == 1) {
            sendNotification("¡Nuevo artículo publicado!", newItemList.get(0).getTitle());
            sendBroadcast(Constants.SEARCHER_NEW_ITEM_FOUND);
        } else if (newItemList.size() > 1) {
            sendNotification("¡Nuevos artículos publicados!", String.format(Locale.US,
                    "Hay %d nuevos artículos", newItemList.size()));
            sendBroadcast(Constants.SEARCHER_NEW_ITEM_FOUND);
        }
        return Result.success();
    }

    private void sendBroadcast(String broadcastMsg) {
        Intent intent = new Intent(broadcastMsg);
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta app lo reciba
        getApplicationContext().sendBroadcast(intent);
    }

    private void sendNotification(String title, String text) {
        /* Crea la notificación */
        Intent intent = new Intent(getApplicationContext(), SearchesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ml_alertas_300x300)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
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
