package fcamussi.mlalertas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("CREATE TABLE searches(" +
                "search_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "words TEXT," +
                "site_id TEXT," +
                "item_count INTEGER DEFAULT 0," +
                "new_item INTEGER DEFAULT 0," +
                "visible INTEGER DEFAULT 0," +
                "deleted INTEGER DEFAULT 0)");
        DB.execSQL("CREATE TABLE items(" +
                "search_id INTEGER," +
                "item_id TEXT," +
                "title TEXT," +
                "price DOUBLE," +
                "currency TEXT," +
                "permalink TEXT," +
                "thumbnail_link TEXT," +
                "thumbnail_image TEXT DEFAULT NULL," +
                "state TEXT, " +
                "new_item INTEGER DEFAULT 0," +
                "PRIMARY KEY(search_id,item_id)," +
                "FOREIGN KEY(search_id) REFERENCES searches(search_id) ON DELETE CASCADE)");
        DB.execSQL("CREATE TABLE items_tmp AS SELECT * FROM items");
        DB.execSQL("CREATE TABLE sites(" +
                "site_id TEXT PRIMARY KEY," +
                "name TEXT)");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MLB','Brasil')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MPE','Perú')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MLA','Argentina')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MLC','Chile')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MNI','Nicaragua')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MEC','Ecuador')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MLM','Mexico')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MSV','El Salvador')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MHN','Honduras')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MLV','Venezuela')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MLU','Uruguay')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MPY','Paraguay')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MCO','Colombia')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MGT','Guatemala')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MBO','Bolivia')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MCR','Costa Rica')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MRD','Dominicana')");
        DB.execSQL("INSERT INTO sites(site_id,name) VALUES('MPA','Panamá')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
