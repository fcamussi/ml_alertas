package fcamussi.mlalertas;

import java.util.Map;
import java.util.Objects;

/**
 * Clase Item
 *
 * @author Fernando Camussi
 */
public class Item {

    private String id;
    private int searchId;
    private String title;
    private double price;
    private String currency;
    private String permalink;
    private String thumbnailLink;
    private byte[] thumbnail;
    private String state;
    private String city;
    private boolean newItem;

    /**
     * Constructor
     */
    public Item() {
    }

    /**
     * Constructor
     *
     * @param item artículo de tipo Map<String, String> retornado en MLSearcher
     */
    public Item(Map<String, String> item) {
        id = item.get("id");
        title = item.get("title");
        price = Double.parseDouble(Objects.requireNonNull(item.get("price")));
        currency = item.get("currency");
        permalink = item.get("permalink");
        /* si empieza con http:// reemplazo por https:// */
        thumbnailLink = item.get("thumbnail_link").replaceFirst("(?i)^http://", "https://");
        state = item.get("state");
        city = item.get("city");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

}
