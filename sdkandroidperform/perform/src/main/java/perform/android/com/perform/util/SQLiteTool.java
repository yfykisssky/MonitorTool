package perform.android.com.perform.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteTool extends SQLiteOpenHelper {
    private static final String DB_NAME = "performdata.db"; // 数据库文件名
    private static final int VERSION = 1;// 数据库版本


    public static SQLiteTool newInstance(Context context) {
        return new SQLiteTool(context, DB_NAME, null, VERSION);
    }

    private SQLiteTool(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//当数据库版本变大时调用
        onCreate(db);
    }
}
