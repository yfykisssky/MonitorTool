package perform.android.com.perform;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import perform.android.com.perform.tool.CpuMemTool;
import perform.android.com.perform.util.DBTCollect;
import perform.android.com.perform.view.WindowTool;

public class CheckService extends Service {

    public void setTime(int time) {
        this.time = time;
    }

    public void setSave(int save) {
        this.save = save;
    }

    public void setTimeCheck(int timeCheck) {
        this.timeCheck = timeCheck * 1000;
    }

    private int time = 10;

    private int saveCount = 0;

    private int save = 6;

    private int timeCheck = 1000;

    private int timeCount = 0;

    private boolean isCheck;

    private double memAvg = 0;

    private double memMax = 0;

    private List<CpuMemTool.MemData> memAlls = new ArrayList<>();

    private Thread checkThread = new Thread(new Runnable() {
        @Override
        public void run() {

            while (isCheck) {
                double mem = CpuMemTool.getInstance().appMemory();
                if (memAvg != 0) {
                    memAvg = (memAvg + mem) / 2;
                } else {
                    memAvg = mem;
                }
                if (mem > memMax) {
                    memMax = mem;
                }
                timeCount++;
                if (timeCount == time) {
                    CpuMemTool.MemData memData = new CpuMemTool.MemData();
                    memData.memAvg = (int) memAvg;
                    memData.memMax = (int) memMax;
                    memAlls.add(memData);
                    memAvg = 0;
                    timeCount = 0;
                    saveCount++;
                }
                if (saveCount == save) {
                    saveCount = 0;
                    DBTCollect.insertMemData(memAlls);
                    memAlls.clear();
                }
                WindowTool.getInstance().setMem((int) mem);
                try {
                    Thread.sleep(timeCheck);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    });

    public class ServiceBinder extends Binder {
        public CheckService getService() {
            return CheckService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        CpuMemTool.getInstance().init(getApplicationContext());
        return new ServiceBinder();
    }

    public void startCheck() {
        if (checkThread.isAlive()) {
            return;
        }
        isCheck = true;
        checkThread.start();
    }

    public void stopCheck() {
        isCheck = false;
    }

}
