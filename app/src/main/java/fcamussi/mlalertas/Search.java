package fcamussi.mlalertas;

import java.util.List;

/**
 * Clase Search
 *
 * @author Fernando Camussi
 */
public class Search {

    private int id;
    private List<String> wordList;
    private String siteId;
    private String frequencyId;
    private int minutesCountdown;
    private int itemCount;
    private boolean newItem;
    private long updated;
    private boolean deleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(String frequencyId) {
        this.frequencyId = frequencyId;
    }

    public int getMinutesCountdown() {
        return minutesCountdown;
    }

    public void setMinutesCountdown(int minutesCountdown) {
        this.minutesCountdown = minutesCountdown;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
