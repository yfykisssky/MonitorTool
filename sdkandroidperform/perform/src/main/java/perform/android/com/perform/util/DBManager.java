package perform.android.com.perform.util;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

import perform.android.com.perform.PerformInstance;

public class DBManager {
    private AtomicInteger dbOpenCount = new AtomicInteger();//计数器
    private static DBManager instance;
    private static SQLiteTool sqlLiteTool;
    private SQLiteDatabase database;

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    private DBManager() {
        sqlLiteTool = SQLiteTool.newInstance(PerformInstance.getContext());
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (dbOpenCount.incrementAndGet() == 1) {
            database = sqlLiteTool.getWritableDatabase();
            database.enableWriteAheadLogging();// 允许读写同时进行
        }
        return database;
    }

    public synchronized void closeDatabase() {
        if (dbOpenCount.decrementAndGet() == 0) {
            database.close();
            database = null;
        }
    }
}
