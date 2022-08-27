package fcamussi.mlalertas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mlsearcher.Item;
import mlsearcher.MLSearcher;

public class DataBase {

    private static AdminSQLiteOpenHelper admin = null;
    private final SQLiteDatabase db;

    public DataBase(Context context) {
        if (admin == null) {
            admin = new AdminSQLiteOpenHelper(context, "DB", null, 1);
        }
        db = admin.getWritableDatabase();
    }

    public Cursor getCursorForAdapterSearch() {
        Cursor cursor;
        cursor = db.rawQuery("SELECT search_id AS _id,words,site_id,frequency_id,item_count,new_item FROM searches " +
                "WHERE visible=1 AND deleted=0", null);
        return cursor;
    }

    public Search getSearch(int searchId) {
        Search search = new Search();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM searches WHERE search_id=" + searchId, null);
        if (cursor.moveToFirst()) {
            fillSearchFromCursor(search, cursor);
        }
        cursor.close();
        return search;
    }

    public List<Search> getAllSearches() {
        List<Search> searchList = new ArrayList<>();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM searches", null);
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
        Cursor cursor = db.rawQuery("SELECT search_id FROM searches WHERE rowid=" + rowid, null);
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
        register.put("visible", search.isVisible());
        register.put("deleted", search.isDeleted());
        db.update("searches", register, "search_id=" + search.getId(), null);
    }

    public void deleteSearch(int searchId) {
        db.execSQL("DELETE FROM searches WHERE search_id=" + searchId);
    }

    public Cursor getCursorForAdapterItem(int searchId) {
        Cursor cursor;
        cursor = db.rawQuery("SELECT item_id AS _id,title,price,currency,state " +
                "FROM items WHERE search_id=" + searchId + " " +
                "ORDER BY item_id DESC", null);
        return cursor;
    }

    public int addItems(int searchId, List<Item> itemList, boolean newItem) {
        db.execSQL("DELETE FROM items_tmp");
        for (Item item : itemList) {
            ContentValues register = new ContentValues();
            register.put("search_id", searchId);
            register.put("item_id", item.getId());
            register.put("title", item.getTitle());
            register.put("price", item.getPrice());
            register.put("currency", item.getCurrency());
            register.put("permalink", item.getPermalink());
            register.put("thumbnail_link", item.getThumbnailLink());
            register.put("state", item.getState());
            register.put("new_item", newItem);
            db.insert("items_tmp", null, register);
        }
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM items_tmp " +
                "WHERE item_id NOT IN (SELECT item_id FROM items " +
                "WHERE search_id=" + searchId + ") AND search_id=" + searchId, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues register = new ContentValues();
                register.put("search_id", searchId);
                register.put("item_id", cursor.getString(cursor.getColumnIndexOrThrow("item_id")));
                register.put("title", cursor.getString(cursor.getColumnIndexOrThrow("title")));
                register.put("price", cursor.getString(cursor.getColumnIndexOrThrow("price")));
                register.put("currency", cursor.getString(cursor.getColumnIndexOrThrow("currency")));
                register.put("permalink", cursor.getString(cursor.getColumnIndexOrThrow("permalink")));
                register.put("thumbnail_link", cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_link")));
                register.put("state", cursor.getString(cursor.getColumnIndexOrThrow("state")));
                register.put("new_item", newItem);
                db.insert("items", null, register);
            } while (cursor.moveToNext());
        }
        int newItemCount = cursor.getCount();
        cursor.close();
        return newItemCount;
    }

    public int getItemCount(int search_id) {
        Cursor cursor = db.rawQuery("SELECT * FROM items WHERE search_id=" + search_id, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Cursor getCursorForAdapterSite() {
        Cursor cursor;
        cursor = db.rawQuery("SELECT site_id AS _id,site_id||' ('||name||')' as site_id_name FROM sites", null);
        return cursor;
    }

    public Cursor getCursorForAdapterFrequency() {
        Cursor cursor;
        cursor = db.rawQuery("SELECT frequency_id AS _id FROM frequencies ORDER BY MINUTES ASC", null);
        return cursor;
    }

    public Frequency getFrequency(String frequencyId) {
        Frequency frequency = new Frequency();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM frequencies WHERE frequency_id='" + frequencyId + "'", null);
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
        search.setVisible(cursor.getInt(cursor.getColumnIndexOrThrow("visible")) > 0);
        search.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) > 0);
    }

}
