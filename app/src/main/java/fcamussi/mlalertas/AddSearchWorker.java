package fcamussi.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mlsearcher.MLSearcher;

/**
 * Clase AddSearchWorker para agregar una nueva búsqueda
 *
 * @author Fernando Camussi
 */
public class AddSearchWorker extends Worker {

    public AddSearchWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        String words = getInputData().getString("words");
        String siteId = getInputData().getString("site_id");
        String frequencyId = getInputData().getString("frequency_id");
        DataBase dataBase = new DataBase(getApplicationContext());
        MLSearcher mlSearcher = new MLSearcher();

        mlSearcher.setSiteId(siteId);
        List<String> wordList = MLSearcher.stringToStringList(words);
        mlSearcher.setWords(wordList);
        mlSearcher.setAgent(Constants.AGENT);
        try {
            if (mlSearcher.getResultCount() > Constants.ADD_SEARCH_MAX_RESULT_COUNT) {
                sendBroadcast(Constants.ADD_SEARCH_MAX_RESULT_COUNT_EXCEEDED);
                return Result.success();
            }
            mlSearcher.searchItems();
        } catch (Exception e) {
            sendBroadcast(Constants.ADD_SEARCH_CONNECTION_FAILED);
            return Result.success();
        }
        List<Item> foundItems = new ArrayList<>(); /* para cargar los items de la búsqueda */
        for (Map<String, String> item : mlSearcher.getFoundItems()) {
            foundItems.add(new Item(item));
        }
        Search search = new Search();
        search.setWordList(wordList);
        search.setSiteId(siteId);
        search.setFrequencyId(frequencyId);
        /* Escojo minutesCountdown de manera aleatoria entre SEARCHER_FREQUENCY_MINUTES y
           la cantidad de minutos de la frecuencia seleccionada para balancear la carga */
        int minutes = dataBase.getFrequency(frequencyId).getMinutes();
        int frequency = minutes / Constants.SEARCHER_FREQUENCY_MINUTES;
        int minutesCountdown = (int) (Math.random() * frequency + 1) * 15;
        search.setMinutesCountdown(minutesCountdown);
        dataBase.beginTransaction();
        try {
            search = dataBase.addSearch(search);
            dataBase.addNewItemsAndRemoveOldItems(search.getId(), foundItems, false);
            search.setItemCount(dataBase.getItemCount(search.getId()));
            dataBase.updateSearch(search);
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
        ThumbnailDownloader thumbnailDownloader = new ThumbnailDownloader(getApplicationContext());
        thumbnailDownloader.download();

        sendBroadcast(Constants.ADD_SEARCH_FINISHED);
        return Result.success();
    }

    private void sendBroadcast(String broadcastMsg) {
        Intent intent = new Intent(broadcastMsg);
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta app lo reciba
        getApplicationContext().sendBroadcast(intent);
    }

}
