package perform.android.com.perform.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import perform.android.com.perform.tool.CSVTool;
import perform.android.com.perform.tool.CpuMemTool;
import perform.android.com.perform.tool.FPSTool;
import perform.android.com.perform.tool.TimeRecordTool;

public class DBTCollect {

    // 创建表的语句
    public static void createTableSQL() {
        SQLiteDatabase database = DBManager.getInstance().openDatabase();
        String sqlStr = "create table if not exists " +
                "fpsdata" +
                "(id long,tag varchar,countLow long,maxLow long,minLow int,time long);";
        database.execSQL(sqlStr);
        sqlStr = "create table if not exists " +
                "timedata" +
                "(id long,tag varchar,dataStr varchar);";
        database.execSQL(sqlStr);
        sqlStr = "create table if not exists " +
                "memdata" +
                "(id long,dataStr varchar);";
        database.execSQL(sqlStr);
    }

    public static void insertFpsData(FPSTool.FpsData fpsData) {
        SQLiteDatabase database = DBManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", System.currentTimeMillis());
        cv.put("tag", fpsData.tag);
        cv.put("countLow", fpsData.countLow);
        cv.put("maxLow", fpsData.maxLow);
        cv.put("minLow", fpsData.minLow);
        cv.put("time", fpsData.time);
        database.insert("fpsdata", null, cv);
        DBManager.getInstance().closeDatabase();
    }

    public static ArrayList<FPSTool.FpsData> getAllFpsData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.query("fpsdata", null, null, null, null, null, null);
        ArrayList<FPSTool.FpsData> objs = new ArrayList<>();
        while (cursor.moveToNext()) {
            FPSTool.FpsData obj = new FPSTool.FpsData();
            obj.id = cursor.getLong(cursor.getColumnIndex("id"));
            obj.tag = cursor.getString(cursor.getColumnIndex("tag"));
            obj.countLow = cursor.getLong(cursor.getColumnIndex("countLow"));
            obj.maxLow = cursor.getLong(cursor.getColumnIndex("maxLow"));
            obj.minLow = cursor.getInt(cursor.getColumnIndex("minLow"));
            obj.time = cursor.getLong(cursor.getColumnIndex("time"));
            objs.add(obj);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return objs;
    }

    public static void clearFpsData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete("fpsdata", null, null);
        DBManager.getInstance().closeDatabase();
    }

    public static String cvsFpsData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cus = db.query("fpsdata", null, null, null, null, null, null);
        DBManager.getInstance().closeDatabase();
        return CSVTool.exportToDataBaseCSV(cus, CSVTool.getSaveFile("fps"));
    }

    public static void insertTimeData(TimeRecordTool.TimeData data) {
        SQLiteDatabase database = DBManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", System.currentTimeMillis());
        cv.put("tag", data.tag);
        cv.put("dataStr", data.dataStr);
        database.insert("timedata", null, cv);
        DBManager.getInstance().closeDatabase();
    }

    public static ArrayList<TimeRecordTool.TimeData> getAllTimeData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.query("timedata", null, null, null, null, null, null);
        ArrayList<TimeRecordTool.TimeData> objs = new ArrayList<>();
        while (cursor.moveToNext()) {
            TimeRecordTool.TimeData obj = new TimeRecordTool.TimeData();
            obj.id = cursor.getLong(cursor.getColumnIndex("id"));
            obj.tag = cursor.getString(cursor.getColumnIndex("tag"));
            obj.dataStr = cursor.getString(cursor.getColumnIndex("dataStr"));
            objs.add(obj);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return objs;
    }

    public static void clearTimeData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete("timedata", null, null);
        DBManager.getInstance().closeDatabase();
    }

    public static String cvsTimeData() {

        List<String> listData = new ArrayList<>();
        listData.add(new String("tag" + "," + "id" + "," + "start" + "," + "end"));
        ArrayList<TimeRecordTool.TimeData> timeDataArrayList = getAllTimeData();
        try {

            for (int x = 0; x < timeDataArrayList.size(); x++) {
                String data = "";
                data = timeDataArrayList.get(x).tag + "," + timeDataArrayList.get(x).id + ",";
                JSONObject jsonObject = new JSONObject(timeDataArrayList.get(x).dataStr);
                JSONArray array = jsonObject.getJSONArray(timeDataArrayList.get(x).tag);
                for (int r = 0; r < array.length(); r++) {
                    JSONObject object = array.getJSONObject(r);
                    if (object.has("start")) {
                        data += object.getString("start") + ",";
                    }
                    if (object.has("end")) {
                        data += object.getString("end");
                    }
                }
                listData.add(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DBManager.getInstance().closeDatabase();
        return CSVTool.exportToStrCSV(listData, CSVTool.getSaveFile("time"));
    }

    public static void insertMemData(List<CpuMemTool.MemData> data) {
        SQLiteDatabase database = DBManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", System.currentTimeMillis());
        String dataStr = new Gson().toJson(data);
        cv.put("dataStr", dataStr);
        database.insert("memdata", null, cv);
        DBManager.getInstance().closeDatabase();
    }

    public static ArrayList<CpuMemTool.MemSaveData> getAllMemData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.query("memdata", null, null, null, null, null, null);
        ArrayList<CpuMemTool.MemSaveData> objs = new ArrayList<>();
        while (cursor.moveToNext()) {
            CpuMemTool.MemSaveData obj = new CpuMemTool.MemSaveData();
            obj.id = cursor.getLong(cursor.getColumnIndex("id"));
            obj.data = cursor.getString(cursor.getColumnIndex("dataStr"));
            objs.add(obj);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return objs;
    }

    public static void clearMemData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete("memdata", null, null);
        DBManager.getInstance().closeDatabase();
    }

    public static String cvsMemData() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cus = db.query("memdata", null, null, null, null, null, null);
        DBManager.getInstance().closeDatabase();
        return null;
    }

}
