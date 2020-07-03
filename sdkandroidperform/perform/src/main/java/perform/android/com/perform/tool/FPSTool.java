package perform.android.com.perform.tool;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;

import perform.android.com.perform.PerformInstance;
import perform.android.com.perform.util.DBTCollect;
import perform.android.com.perform.view.WindowTool;

public class FPSTool {

    private Choreographer mChoreographer;
    private Choreographer.FrameCallback frameCallback;

    private static int FPS = 30;

    private static volatile FPSTool instance;

    private static List<FpsData> datas = new ArrayList<>();

    public static class FpsData {
        public long id;
        public String tag;
        public long time = 0;
        public long countLow;
        public long maxLow;
        public long maxLastLowCount;
        public boolean lastIsLow = false;
        public int minLow = FPS;
    }

    public static FPSTool getInstance() {

        if (instance == null) {
            synchronized (FPSTool.class) {
                if (instance == null) {
                    instance = new FPSTool();
                }
            }
        }
        return instance;
    }

    private ViewHandler handler = new ViewHandler();

    static class ViewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int fps = (int) msg.obj;
            if (fps < FPS) {
                for (int v = 0; v < datas.size(); v++) {
                    datas.get(v).countLow++;
                    if (fps < datas.get(v).minLow) {
                        datas.get(v).minLow = fps;
                    }
                    datas.get(v).maxLastLowCount++;
                    if (!datas.get(v).lastIsLow) {
                        datas.get(v).lastIsLow = true;
                        datas.get(v).maxLow = datas.get(v).maxLastLowCount;
                    } else {
                        if (datas.get(v).maxLastLowCount > datas.get(v).maxLow) {
                            datas.get(v).maxLow = datas.get(v).maxLastLowCount;
                        }
                    }
                }
            } else {
                for (int v = 0; v < datas.size(); v++) {
                    if (datas.get(v).lastIsLow) {
                        datas.get(v).lastIsLow = false;
                        if (datas.get(v).maxLastLowCount > datas.get(v).maxLow) {
                            datas.get(v).maxLow = datas.get(v).maxLastLowCount;
                        }
                        datas.get(v).maxLastLowCount = 0;
                    }
                }
            }
            Log.e("fps", String.valueOf(fps));
            WindowTool.getInstance().setFPS(fps, FPS);
        }
    };

    public void init(int fps) {
        if (fps != 0) {
            FPS = fps;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            frameCallback = new Choreographer.FrameCallback() {
                long mLastFrameTime = 0;
                int mFrameCount = 0;

                @Override
                public void doFrame(long frameTimeNanos) {

                    if (mLastFrameTime == 0) {
                        mLastFrameTime = frameTimeNanos;
                    }
                    float diff = (frameTimeNanos - mLastFrameTime) / 1000000.0f;//得到毫秒，正常是 16.66 ms
                    if (diff > 500) {
                        int fps = (int) ((((double) (mFrameCount * 1000L)) / diff));
                        mFrameCount = 0;
                        mLastFrameTime = 0;
                        Message msg = new Message();
                        msg.obj = fps;
                        handler.dispatchMessage(msg);
                    } else {
                        ++mFrameCount;
                    }
                    mChoreographer.postFrameCallback(frameCallback);
                }
            };
            mChoreographer = Choreographer.getInstance();
            mChoreographer.postFrameCallback(frameCallback);
        }
    }

    public void startRecord(String pageId) {
        FpsData data = new FpsData();
        synchronized (datas) {
            data.tag = pageId;
            data.time = System.currentTimeMillis();
            datas.add(data);
        }
    }

    public void stopRecord(String pageId) {
        FpsData data = new FpsData();
        synchronized (datas) {
            for (int r = 0; r < datas.size(); r++) {
                if (datas.get(r).tag.equals(pageId)) {
                    data = datas.get(r);
                    datas.remove(r);
                }
            }
        }
        data.time = (System.currentTimeMillis() - data.time) / 1000;
        if (PerformInstance.isIsSave()) {
            DBTCollect.insertFpsData(data);
        }
    }

    public void stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (mChoreographer != null) {
                mChoreographer.removeFrameCallback(frameCallback);
            }
        }
    }

}
