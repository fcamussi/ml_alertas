package fcamussi.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import mlsearcher.Item;
import mlsearcher.MLSearcher;

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
        List<Item> foundItems = mlSearcher.getFoundItems();
        Search search = new Search();
        search.setWordList(wordList);
        search.setSiteId(siteId);
        search.setFrequencyId(frequencyId);
        search = dataBase.addSearch(search);
        int itemCount = dataBase.addItems(search.getId(), foundItems, false);
        search.setItemCount(itemCount);
        search.setVisible(true);
        dataBase.updateSearch(search);
        sendBroadcast(Constants.ADD_SEARCH_FINISHED);
        return Result.success();
    }

    private void sendBroadcast(String broadcastMsg) {
        Intent intent = new Intent(broadcastMsg);
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta app lo reciba
        getApplicationContext().sendBroadcast(intent);
    }

}
