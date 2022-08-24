package fcamussi.mlalertas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE searches(" +
                "search_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "words TEXT," +
                "site_id TEXT," +
                "frequency_id TEXT," +
                "minutes_countdown INTEGER," +
                "item_count INTEGER DEFAULT 0," +
                "new_item INTEGER DEFAULT 0," +
                "visible INTEGER DEFAULT 0," +
                "deleted INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE items(" +
                "search_id INTEGER," +
                "item_id TEXT," +
                "title TEXT," +
                "price DOUBLE," +
                "currency TEXT," +
                "permalink TEXT," +
                "thumbnail_link TEXT," +
                "state TEXT, " +
                "new_item INTEGER DEFAULT 0," +
                "PRIMARY KEY(search_id,item_id)," +
                "FOREIGN KEY(search_id) REFERENCES searches(search_id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE items_tmp AS SELECT * FROM items");

        db.execSQL("CREATE TABLE sites(" +
                "site_id TEXT PRIMARY KEY," +
                "name TEXT)");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MLB','Brasil')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MPE','Perú')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MLA','Argentina')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MLC','Chile')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MNI','Nicaragua')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MEC','Ecuador')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MLM','Mexico')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MSV','El Salvador')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MHN','Honduras')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MLV','Venezuela')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MLU','Uruguay')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MPY','Paraguay')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MCO','Colombia')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MGT','Guatemala')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MBO','Bolivia')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MCR','Costa Rica')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MRD','Dominicana')");
        db.execSQL("INSERT INTO sites(site_id,name) VALUES('MPA','Panamá')");

        db.execSQL("CREATE TABLE frequencies(" +
                "frequency_id TEXT PRIMARY KEY," +
                "minutes INTEGER)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('15M',15)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('30M',30)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('1H',60)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('2H',120)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('4H',240)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('6H',360)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('12H',720)");
        db.execSQL("INSERT INTO frequencies(frequency_id,minutes) VALUES('1D',1440)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON"); // activa el soporte para claves foráneas
    }
}
