package fcamussi.mlalertas;

import java.util.List;

import mlsearcher.MLSearcher;

public class Searcher {

    DataBase dataBase;

    public Searcher(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public int buscar() {
        List<Search> searches = dataBase.getAllSearches();
        MLSearcher mlSearcher = new MLSearcher();
        // set Agente
        int nuevos = 0;
        for (Search search : searches) {
            mlSearcher.setSiteId("MLA");
            mlSearcher.setWords(search.getWordList());
            try {
                mlSearcher.searchItems();
                nuevos += dataBase.addItems(search.getId(), mlSearcher.getFoundItems(), true);
            } catch (Exception e) {
            }
        }
        return nuevos;
    }

}
