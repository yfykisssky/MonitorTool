package com.android.perform;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import perform.android.com.perform.PerformInstance;
import perform.android.com.perform.tool.TimeRecordTool;
import perform.android.com.perform.view.WindowTool;

public class MainActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PerformInstance.setIsSave(true);
        PerformInstance.setIsUse(true);
        PerformInstance.setIsWindow(true);
        PerformInstance.getInstance().init(this);

        //DBTCollect.cvsMemData();
        WindowTool.getInstance().show(this);

        TimeRecordTool.start("test2");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TimeRecordTool.end("test2");
            }
        }).start();


      /*  CpuMemTool.getInstance().init(this);
        CpuMemTool.getInstance().startCheck();*/

    }

}
