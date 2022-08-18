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
        DB.execSQL("CREATE TABLE IF NOT EXISTS searches(" +
                "search_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "words TEXT," +
                "item_count INTEGER DEFAULT 0," +
                "new_item INTEGER DEFAULT 0," +
                "visible INTEGER DEFAULT 0," +
                "deleted INTEGER DEFAULT 0)");
        DB.execSQL("CREATE TABLE IF NOT EXISTS items(" +
                "search_id INTEGER," +
                "item_id TEXT," +
                "title TEXT," +
                "price DOUBLE," +
                "currency TEXT," +
                "permalink TEXT," +
                "thumbnail TEXT," +
                "state TEXT, " +
                "new_item INTEGER DEFAULT 0," +
                "FOREIGN KEY(search_id) REFERENCES searches(search_id))");
        DB.execSQL("CREATE TABLE items_tmp AS SELECT * FROM items WHERE 1=2");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
