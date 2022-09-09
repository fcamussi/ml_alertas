package fcamussi.mlalertas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mlsearcher.MLSearcher;

public class DataBase {

    private static SQLiteDatabase db = null;

    public DataBase(Context context) {
        if (db == null) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "DB", null, 1);
            db = admin.getWritableDatabase();
        }
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }

    public Cursor getCursorForAdapterSearch() {
        Cursor cursor;
        cursor = db.rawQuery("SELECT search_id AS _id,words,site_id,frequency_id,item_count,new_item " +
                        "FROM searches " +
                        "WHERE deleted=0",
                null);
        return cursor;
    }

    public Search getSearch(int searchId) {
        Search search = new Search();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * " +
                        "FROM searches " +
                        "WHERE search_id=" + searchId,
                null);
        if (cursor.moveToFirst()) {
            fillSearchFromCursor(search, cursor);
        }
        cursor.close();
        return search;
    }

    public List<Search> getAllSearches() {
        List<Search> searchList = new ArrayList<>();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * " +
                        "FROM searches",
                null);
        if (cursor.moveToFirst()) {
            do {
                Search search = new Search();
                fillSearchFromCursor(search, cursor);
                searchList.add(search);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchList;
    }

    public Search addSearch(Search search) {
        ContentValues register = new ContentValues();
        register.put("words", MLSearcher.stringListToString(search.getWordList()));
        register.put("site_id", search.getSiteId());
        register.put("frequency_id", search.getFrequencyId());
        long rowid = db.insert("searches", null, register);
        Cursor cursor = db.rawQuery("SELECT search_id " +
                        "FROM searches " +
                        "WHERE rowid=" + rowid,
                null);
        cursor.moveToFirst();
        int search_id = cursor.getInt(cursor.getColumnIndexOrThrow("search_id"));
        search.setId(search_id);
        cursor.close();
        return search;
    }

    public void updateSearch(Search search) {
        ContentValues register = new ContentValues();
        register.put("search_id", search.getId());
        register.put("words", MLSearcher.stringListToString(search.getWordList()));
        register.put("site_id", search.getSiteId());
        register.put("frequency_id", search.getFrequencyId());
        register.put("minutes_countdown", search.getMinutesCountdown());
        register.put("item_count", search.getItemCount());
        register.put("new_item", search.isNewItem());
        register.put("deleted", search.isDeleted());
        db.update("searches", register, "search_id=" + search.getId(), null);
    }

    public void deleteSearch(int searchId) {
        db.execSQL("DELETE FROM searches " +
                "WHERE search_id=" + searchId);
    }

    public Cursor getCursorForAdapterItem(int searchId) {
        Cursor cursor;
        cursor = db.rawQuery("SELECT item_id AS _id,title,price,currency,city,state,thumbnail,new_item " +
                        "FROM items " +
                        "WHERE search_id=" + searchId + " " +
                        "ORDER BY rowid DESC",
                null);
        return cursor;
    }

    public Item getItem(String itemId, int searchId) {
        Item item = new Item();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * " +
                        "FROM items " +
                        "WHERE item_id='" + itemId + "' AND search_id=" + searchId,
                null);
        if (cursor.moveToFirst()) {
            fillItemFromCursor(item, cursor);
        }
        cursor.close();
        return item;
    }

    public List<Item> addNewItemsAndRemoveOldItems(int searchId, List<Item> itemList, boolean newItem) {
        /* inserto los encontrados en items_tmp */
        db.execSQL("DELETE FROM items_tmp");
        for (Item item : itemList) {
            ContentValues register = new ContentValues();
            register.put("item_id", item.getId());
            register.put("search_id", searchId);
            register.put("title", item.getTitle());
            register.put("price", item.getPrice());
            register.put("currency", item.getCurrency());
            register.put("permalink", item.getPermalink());
            register.put("thumbnail_link", item.getThumbnailLink());
            register.put("city", item.getCity());
            register.put("state", item.getState());
            register.put("new_item", false);
            db.insert("items_tmp", null, register);
        }
        /* selecciono los nuevos: los que están en items_tmp y no están en items */
        Cursor cursor;
        cursor = db.rawQuery("SELECT * " +
                        "FROM items_tmp " +
                        "WHERE item_id NOT IN (SELECT item_id FROM items " +
                        "WHERE search_id=" + searchId + ") AND search_id=" + searchId,
                null);
        /* los agrego a newItemList para retornarlos */
        List<Item> newItemList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                fillItemFromCursor(item, cursor);
                newItemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        /* borro de items los que ya no están publicados */
        db.execSQL("DELETE FROM items " +
                "WHERE item_id NOT IN (SELECT item_id FROM items_tmp " +
                "WHERE search_id=" + searchId + ") AND search_id=" + searchId);
        if (newItem) {
            /* marco los items nuevos como nuevos */
            db.execSQL("UPDATE items_tmp SET new_item=1 " +
                    "WHERE item_id NOT IN (SELECT item_id FROM items " +
                    "WHERE search_id=" + searchId + ") AND search_id=" + searchId);
        }
        /* inserto en items los nuevos que fueron publicados */
        db.execSQL("INSERT INTO items " +
                "SELECT * FROM items_tmp " +
                "WHERE item_id NOT IN (SELECT item_id FROM items " +
                "WHERE search_id=" + searchId + ") AND search_id=" + searchId);
        return newItemList;
    }

    public void updateItem(Item item) {
        ContentValues register = new ContentValues();
        register.put("item_id", item.getId());
        register.put("search_id", item.getSearchId());
        register.put("title", item.getTitle());
        register.put("price", item.getPrice());
        register.put("currency", item.getCurrency());
        register.put("permalink", item.getPermalink());
        register.put("thumbnail_link", item.getThumbnailLink());
        register.put("thumbnail", item.getThumbnail());
        register.put("city", item.getCity());
        register.put("state", item.getState());
        register.put("new_item", item.isNewItem());
        db.update("items", register,
                "item_id='" + item.getId() + "' AND search_id=" + item.getSearchId(),
                null);
    }

    public int getItemCount(int search_id) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) " +
                        "FROM items " +
                        "WHERE search_id=" + search_id,
                null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getNewItemCount(int searchId) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) " +
                        "FROM items " +
                        "WHERE search_id=" + searchId + " AND new_item=1",
                null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void unsetAllNewItem(int searchId) {
        db.execSQL("UPDATE items SET new_item=0 " +
                "WHERE search_id=" + searchId);
    }

    public List<Item> getAllItemThumbnailIsNull() {
        Cursor cursor;
        List<Item> itemList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * " +
                        "FROM items " +
                        "WHERE thumbnail IS NULL",
                null);
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                fillItemFromCursor(item, cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public Cursor getCursorForAdapterSite() {
        Cursor cursor;
        cursor = db.rawQuery("SELECT site_id AS _id,site_id||' ('||name||')' as site_id_name " +
                        "FROM sites",
                null);
        return cursor;
    }

    public Cursor getCursorForAdapterFrequency() {
        Cursor cursor;
        cursor = db.rawQuery("SELECT frequency_id AS _id " +
                        "FROM frequencies " +
                        "ORDER BY MINUTES ASC",
                null);
        return cursor;
    }

    public Frequency getFrequency(String frequencyId) {
        Frequency frequency = new Frequency();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * " +
                        "FROM frequencies " +
                        "WHERE frequency_id='" + frequencyId + "'",
                null);
        if (cursor.moveToFirst()) {
            frequency.setId(cursor.getString(cursor.getColumnIndexOrThrow("frequency_id")));
            frequency.setMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("minutes")));
        }
        cursor.close();
        return frequency;
    }

    private void fillSearchFromCursor(Search search, Cursor cursor) {
        search.setId(cursor.getInt(cursor.getColumnIndexOrThrow("search_id")));
        search.setWordList(MLSearcher.stringToStringList(cursor.getString(cursor.getColumnIndexOrThrow("words"))));
        search.setSiteId(cursor.getString(cursor.getColumnIndexOrThrow("site_id")));
        search.setFrequencyId(cursor.getString(cursor.getColumnIndexOrThrow("frequency_id")));
        search.setMinutesCountdown(cursor.getInt(cursor.getColumnIndexOrThrow("minutes_countdown")));
        search.setItemCount(cursor.getInt(cursor.getColumnIndexOrThrow("item_count")));
        search.setNewItem(cursor.getInt(cursor.getColumnIndexOrThrow("new_item")) > 0);
        search.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) > 0);
    }

    private void fillItemFromCursor(Item item, Cursor cursor) {
        item.setId(cursor.getString(cursor.getColumnIndexOrThrow("item_id")));
        item.setSearchId(cursor.getInt(cursor.getColumnIndexOrThrow("search_id")));
        item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        item.setCurrency(cursor.getString(cursor.getColumnIndexOrThrow("currency")));
        item.setPermalink(cursor.getString(cursor.getColumnIndexOrThrow("permalink")));
        item.setThumbnailLink(cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_link")));
        item.setThumbnail(cursor.getBlob(cursor.getColumnIndexOrThrow("thumbnail")));
        item.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
        item.setState(cursor.getString(cursor.getColumnIndexOrThrow("state")));
        item.setNewItem(cursor.getInt(cursor.getColumnIndexOrThrow("new_item")) > 0);
    }

}
