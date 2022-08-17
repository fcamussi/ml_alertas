package com.example.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import mlsearcher.Item;
import mlsearcher.MLSearcher;

public class OneTimeSearchWorker extends Worker {

    public OneTimeSearchWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        String words = getInputData().getString("words");
        String siteId = getInputData().getString("site_id");
        DataBase dataBase = new DataBase(getApplicationContext());
        MLSearcher mlSearcher = new MLSearcher();

/*        try {
            mlSearcher.setMaxItemCount(Constants.MAX_ITEM_COUNT);
        } catch (Exception e) {
        }*/
        mlSearcher.setSiteId(siteId);
        List<String> wordsList = MLSearcher.stringToStringList(words);
        mlSearcher.setWords(wordsList);
        mlSearcher.setAgent(Constants.AGENT);
        try {
            mlSearcher.searchItems();
        } catch (Exception e) {
            sendBroadcast(Constants.CONNECTION_FAILED, words);
            return Result.success();
        }
        List<Item> foundItems = mlSearcher.getFoundItems();
        if (foundItems.size() > Constants.MAX_ITEM_FOUND) {
            sendBroadcast(Constants.ONE_TIME_SEARCH_TOO_MANY_ITEMS_FOUND, words);
        } else {
            Search search = new Search();
            search.setWordList(wordsList);
            search = dataBase.addSearch(search);
            dataBase.addItems(search.getId(), foundItems, false);
            search.setItemCount(dataBase.getItemCount(search.getId()));
            search.setVisible(true);
            dataBase.updateSearch(search);
            sendBroadcast(Constants.ONE_TIME_SEARCH_FINISHED, words);
        }
        return Result.success();
    }

    private void sendBroadcast(String broadcastMsg, String words) {
        Intent intent = new Intent(broadcastMsg);
        intent.putExtra("words", words);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().sendBroadcast(intent);
    }

}
