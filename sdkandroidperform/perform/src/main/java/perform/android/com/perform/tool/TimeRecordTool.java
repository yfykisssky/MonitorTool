package perform.android.com.perform.tool;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import perform.android.com.perform.PerformInstance;
import perform.android.com.perform.util.DBTCollect;

public class TimeRecordTool {

    private static Long recordStart = 0L;
    private static String FILE_PATH="/storage/emulated/0/Android/recoedfile";

    static class Model {
        private long timeStart;
        private List<Data> listDatas = new ArrayList<>();
    }

    static class Data {
        private String mark;
        private long time;
    }

    public static class TimeData {
        public long id;
        public String tag;
        public String dataStr;
    }

    private static final HashMap<String, Model> recordData = new HashMap<>();

    public static void init() {
        if (PerformInstance.isIsUse()) {
            if (PerformInstance.isUseStart()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (recordStart) {
                            recordStart = readFromFile();
                        }
                    }
                }).start();
            }
        }
    }

    public static boolean start(String tag) {
        if (PerformInstance.isIsUse()) {
            synchronized (recordData) {
                if (recordData.containsKey(tag)) {
                    return false;
                }

                Model model = new Model();
                model.timeStart = System.currentTimeMillis();
                recordData.put(tag, model);
                return true;
            }

        }
        return false;
    }

    private static long readFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            String resultStr = null;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                resultStr = reader.readLine();
              /*  String tempString = null;
                int line = 1;
                while ((tempString = reader.readLine()) != null) {
                    resultStr += tempString;
                    line++;
                }*/
                reader.close();
                return Long.parseLong(resultStr);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                file.delete();
            }
            return 0;
        }
        return 0;
    }

    public static void record(String tag, String mark) {
        if (PerformInstance.isIsUse()) {
            synchronized (recordData) {
                Model model = recordData.get(tag);
                if (model != null) {
                    Data data = new Data();
                    data.mark = mark;
                    data.time = System.currentTimeMillis();
                    model.listDatas.add(data);
                }
            }
        }
    }

    public static void end(String tag) {
        end(tag, false);
    }

    public static void endUseStart(String tag) {
        end(tag, true);
    }

    public static void end(String tag, boolean useStart) {
        if (PerformInstance.isIsUse()) {
            long end = System.currentTimeMillis();
            Model model;
            synchronized (recordData) {
                model = recordData.get(tag);
                if (model == null) {
                    return;
                }
                recordData.remove(tag);
            }
            if (model != null) {
                JSONArray records = new JSONArray();
                try {
                    records.put(new JSONObject().put("start", "0"));
                    long start;
                    if (useStart) {
                        if (recordStart == 0) {
                            records.put(new JSONObject().put("start", "5000"));
                            start = model.timeStart;
                        } else {
                            start = recordStart;
                        }
                    } else {
                        start = model.timeStart;
                    }
                    for (int v = 0; v < model.listDatas.size(); v++) {
                        records.put(new JSONObject().put(model.listDatas.get(v).mark,
                                String.valueOf((model.listDatas.get(v).time - start))));
                    }
                    records.put(new JSONObject().put("end", String.valueOf(end - start)));
                    JSONObject object = new JSONObject();
                    object.put(tag, records);
                    TimeData timeData = new TimeData();
                    timeData.dataStr = object.toString();
                    timeData.tag = tag;
                    if (PerformInstance.isIsSave()) {
                        DBTCollect.insertTimeData(timeData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
