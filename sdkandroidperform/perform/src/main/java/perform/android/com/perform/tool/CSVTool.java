package perform.android.com.perform.tool;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import perform.android.com.perform.PerformInstance;
import perform.android.com.perform.util.SystemUtil;

public class CSVTool {

    public static String getSaveFile(String fileName) {
        return (fileName + "_" + SystemUtil.getDeviceBrand() +
                "_" + SystemUtil.getSystemModel() +
                "_" + SystemUtil.getSystemVersion() + "_"
                + "APP" + SystemUtil.getVersionName(PerformInstance.getContext()) + ".csv").replace(" ", "");
    }

    private static File getPath(String fileName) {
        File sdCardDir = Environment.getExternalStorageDirectory();
        return new File(sdCardDir, fileName);
    }

    public static String exportToDataBaseCSV(Cursor c, String fileName) {

        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
        File saveFile = getPath(fileName);
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                c.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(c.getColumnName(i) + ',');
                    else
                        bfw.write(c.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bfw.write(c.getString(j) + ',');
                        else
                            bfw.write(c.getString(j));
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            return saveFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            c.close();
        }
    }

    public static String exportToStrCSV(List<String> datas, String fileName) {

        FileWriter fw;
        BufferedWriter bfw = null;
        File saveFile = getPath(fileName);
        try {
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            for (int e = 0; e < datas.size(); e++) {
                bfw.write(datas.get(e));
                bfw.newLine();
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            return saveFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
        }
    }

}
