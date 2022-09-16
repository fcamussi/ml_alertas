package fcamussi.mlalertas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mlsearcher.MLSearcher;

/**
 * Clase SearcherWorker para realizar las búsquedas de forma periódica y notificar
 * mediante una notificación push si se encuentra un artículo nuevo
 *
 * @author Fernando Camussi
 */
public class SearcherWorker extends Worker {

    Context context;

    public SearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public Result doWork() {
        DataBase dataBase = new DataBase(context);
        Notification notification = new Notification(context,
                "NIN",
                context.getString(R.string.new_item_notification));
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
                    } else { // puede que haya desaparecido uno marcado como nuevo
                        if (dataBase.getNewItemCount(search.getId()) <= 0) {
                            search.setNewItem(false);
                        }
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
            ThumbnailDownloader thumbnailDownloader = new ThumbnailDownloader(context);
            thumbnailDownloader.download();

            /* incremento el último notificationId y lo guardo en preferencias */
            SharedPreferences preferences = context.getSharedPreferences("searcher_worker", Context.MODE_PRIVATE);
            int notificationId = preferences.getInt("notification_id", 0);
            SharedPreferences.Editor editor = preferences.edit();
            notificationId++;
            editor.putInt("notification_id", notificationId);
            editor.apply();

            if (itemIdSet.size() == 1) {
                notification.send(notificationId, context.getString(R.string.new_item_published),
                        newItemList.get(0).getTitle());
            }
            if (itemIdSet.size() > 1) {
                notification.send(notificationId, context.getString(R.string.new_items_published),
                        String.format(context.getString(R.string.there_are_X_new_items), itemIdSet.size()));
            }
        }

        sendBroadcast();
        return Result.success();
    }

    private void sendBroadcast() {
        Intent intent = new Intent(Constants.SEARCHER_FINISHED);
        intent.setPackage(context.getPackageName()); // para que solo ésta app lo reciba
        context.sendBroadcast(intent);
    }

}
