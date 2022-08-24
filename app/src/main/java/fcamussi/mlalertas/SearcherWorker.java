package fcamussi.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import mlsearcher.Item;
import mlsearcher.MLSearcher;

public class SearcherWorker extends Worker {

    DataBase dataBase;

    public SearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        DataBase dataBase = new DataBase(getApplicationContext());
        MLSearcher mlSearcher = new MLSearcher();
        boolean newItem = false;

        mlSearcher.setAgent(Constants.AGENT);
        List<Search> searches = dataBase.getAllSearches();
        for (Search search : searches) {
            if (search.isDeleted()) { // fue marcado para ser borrado
                dataBase.deleteSearch(search.getId());
                continue;
            }
            Frequency frequency = dataBase.getFrequency(search.getFrequencyId());
            int minutesCountdown = search.getMinutesCountdown();
            minutesCountdown -= frequency.getMinutes();
            if (minutesCountdown <= 0) { // hacemos la búsqueda
                mlSearcher.setSiteId(search.getSiteId());
                mlSearcher.setWords(search.getWordList());
                try {
                    mlSearcher.searchItems();
                } catch (Exception e) {
                }
                List<Item> foundItems = mlSearcher.getFoundItems();
                int newItemCount = dataBase.addItems(search.getId(), foundItems, true);
                if (newItemCount > 0) {
                    // tengo que marcar cada item como nuevo y la busqueda
                    // seria interesante que dataBase.addItems me devuelva los nuevos
                    newItem = true;
                }
                search.setItemCount(dataBase.getItemCount(search.getId()));
                dataBase.updateSearch(search);
            } else {
                search.setMinutesCountdown(minutesCountdown);
                dataBase.updateSearch(search);
                continue;
            }
            // deberia haber 2 limites, uno para agregar, otro para buscar, o usar el mismo
            // los items nuevos se marcan solos :-)
            // hay no solo que marcar como nuevo el item sino la busqueda...
        }

        return Result.success();
    }

}
