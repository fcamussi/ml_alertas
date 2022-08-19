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
    private final SQLiteDatabase DB;

    public DataBase(Context context) {
        if (admin == null) {
            admin = new AdminSQLiteOpenHelper(context, "DB", null, 1);
        }
        DB = admin.getWritableDatabase();
    }

    public Cursor getCursorForAdapterSearch() {
        Cursor cursor;
        cursor = DB.rawQuery("SELECT search_id AS _id,words,site_id,item_count,new_item FROM searches " +
                "WHERE visible=1 AND deleted=0", null);
        return cursor;
    }

    public Search getSearch(int search_id) {
        Search search = new Search();
        Cursor cursor;
        cursor = DB.rawQuery("SELECT * FROM searches WHERE search_id=" + search_id, null);
        if (cursor.moveToFirst()) {
            search.setId(cursor.getInt(cursor.getColumnIndexOrThrow("search_id")));
            search.setWordList(MLSearcher.stringToStringList(cursor.getString(cursor.getColumnIndexOrThrow("words"))));
            search.setSiteId(cursor.getString(cursor.getColumnIndexOrThrow("site_id")));
            search.setItemCount(cursor.getInt(cursor.getColumnIndexOrThrow("item_count")));
            search.setNewItem(cursor.getInt(cursor.getColumnIndexOrThrow("new_item")) > 0);
            search.setVisible(cursor.getInt(cursor.getColumnIndexOrThrow("visible")) > 0);
            search.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) > 0);
        }
        cursor.close();
        return search;
    }

    public List<Search> getAllSearches() {
        List<Search> searchList = new ArrayList<>();
        Cursor cursor;
        cursor = DB.rawQuery("SELECT * FROM searches", null);
        if (cursor.moveToFirst()) {
            do {
                Search search = new Search();
                search.setId(cursor.getInt(cursor.getColumnIndexOrThrow("search_id")));
                search.setWordList(MLSearcher.stringToStringList(cursor.getString(cursor.getColumnIndexOrThrow("words"))));
                search.setSiteId(cursor.getString(cursor.getColumnIndexOrThrow("site_id")));
                search.setItemCount(cursor.getInt(cursor.getColumnIndexOrThrow("item_count")));
                search.setNewItem(cursor.getInt(cursor.getColumnIndexOrThrow("new_item")) > 0);
                search.setVisible(cursor.getInt(cursor.getColumnIndexOrThrow("visible")) > 0);
                search.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) > 0);
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
        long rowid = DB.insert("searches", null, register);
        Cursor cursor = DB.rawQuery("SELECT search_id FROM searches WHERE rowid=" + rowid, null);
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
        register.put("item_count", search.getItemCount());
        register.put("new_item", search.isNewItem());
        register.put("visible", search.isVisible());
        register.put("deleted", search.isDeleted());
        DB.update("searches", register, "search_id=" + search.getId(), null);
    }

    public Cursor getCursorForAdapterItem(int search_id) {
        Cursor cursor;
        cursor = DB.rawQuery("SELECT item_id AS _id,title,price,currency,state " +
                "FROM items WHERE search_id=" + search_id + " " +
                "ORDER BY item_id DESC", null);
        return cursor;
    }

    public int addItems(int search_id, List<Item> itemList, boolean newItem) {
        DB.execSQL("DELETE FROM items_tmp");
        for (Item item : itemList) {
            ContentValues register = new ContentValues();
            register.put("search_id", search_id);
            register.put("item_id", item.getId());
            register.put("title", item.getTitle());
            register.put("price", item.getPrice());
            register.put("currency", item.getCurrency());
            register.put("permalink", item.getPermalink());
            register.put("thumbnail", item.getThumbnail());
            register.put("state", item.getState());
            register.put("new_item", newItem);
            DB.insert("items_tmp", null, register);
        }
        Cursor cursor;
        cursor = DB.rawQuery("SELECT * FROM items_tmp " +
                "WHERE item_id NOT IN (SELECT item_id FROM items " +
                "WHERE search_id=" + search_id + ") AND search_id=" + search_id, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues register = new ContentValues();
                register.put("search_id", search_id);
                register.put("item_id", cursor.getString(cursor.getColumnIndexOrThrow("item_id")));
                register.put("title", cursor.getString(cursor.getColumnIndexOrThrow("title")));
                register.put("price", cursor.getString(cursor.getColumnIndexOrThrow("price")));
                register.put("currency", cursor.getString(cursor.getColumnIndexOrThrow("currency")));
                register.put("permalink", cursor.getString(cursor.getColumnIndexOrThrow("permalink")));
                register.put("thumbnail", cursor.getString(cursor.getColumnIndexOrThrow("thumbnail")));
                register.put("state", cursor.getString(cursor.getColumnIndexOrThrow("state")));
                register.put("new_item", newItem);
                DB.insert("items", null, register);
            } while (cursor.moveToNext());
        }
        int new_items_count = cursor.getCount();
        cursor.close();
        return new_items_count;
    }

    public int getItemCount(int search_id) {
        Cursor cursor = DB.rawQuery("SELECT * FROM items WHERE search_id=" + search_id, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Cursor getCursorForAdapterSite() {
        Cursor cursor;
        cursor = DB.rawQuery("SELECT site_id AS _id,site_id||' ('||name||')' as site_id_name FROM sites", null);
        return cursor;
    }

}
