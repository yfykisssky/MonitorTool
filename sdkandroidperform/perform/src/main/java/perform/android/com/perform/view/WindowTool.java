package perform.android.com.perform.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import perform.android.com.perform.PerformInstance;
import perform.android.com.perform.R;

public class WindowTool {

    private DataView dataView;
    private WindowManager windowManager;
    private float mStartX, mStartY;
    private WindowManager.LayoutParams layoutParams;
    private Context context;

    private static volatile WindowTool instance;

    public static WindowTool getInstance() {

        if (instance == null) {
            synchronized (WindowTool.class) {
                if (instance == null) {
                    instance = new WindowTool();
                }
            }
        }
        return instance;
    }

    public void show(final Context con) {

        this.context = con.getApplicationContext();

        if (!PerformInstance.isIsWindow()) {
            return;
        }

        if (windowManager != null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "没有悬浮窗权限,跳转申请", Toast.LENGTH_LONG).show();
            }

            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 400;
            layoutParams.height = 500;
            dataView = new DataView(context);
            windowManager.addView(dataView, layoutParams);

            dataView.findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ViewActivity.class));
                }
            });

            dataView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onClick(View v) {
                    if (windowManager != null) {
                        windowManager.removeViewImmediate(dataView);
                        final TextView textView = new TextView(context);
                        textView.setText("打开面板");
                        textView.setGravity(Gravity.CENTER);
                        layoutParams.width = 200;
                        layoutParams.height = 50;
                        textView.setBackgroundColor(Color.WHITE);
                        textView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                // 当前值以屏幕左上角为原点
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        mStartX = event.getRawX();
                                        mStartY = event.getRawY();
                                        break;

                                    case MotionEvent.ACTION_MOVE:
                                        layoutParams.x += event.getRawX() - mStartX;
                                        layoutParams.y += event.getRawY() - mStartY;
                                        windowManager.updateViewLayout(textView, layoutParams);
                                        mStartX = event.getRawX();
                                        mStartY = event.getRawY();
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        break;
                                }

                                // 消耗触摸事件
                                return false;

                            }
                        });
                        windowManager.addView(textView, layoutParams);

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutParams.width = 400;
                                layoutParams.height = 500;
                                windowManager.removeViewImmediate(textView);
                                windowManager.addView(dataView, layoutParams);
                            }
                        });
                    }
                }
            });

            dataView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // 当前值以屏幕左上角为原点
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mStartX = event.getRawX();
                            mStartY = event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            layoutParams.x += event.getRawX() - mStartX;
                            layoutParams.y += event.getRawY() - mStartY;
                            windowManager.updateViewLayout(dataView, layoutParams);
                            mStartX = event.getRawX();
                            mStartY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }

                    // 消耗触摸事件
                    return true;

                }
            });

        }
    }


    @SuppressLint("SetTextI18n")
    public void setFPS(final int fps, int lowFps) {
        if (!PerformInstance.isIsWindow()) {
            return;
        }
        if (dataView != null) {
            if (fps < lowFps) {
                dataView.textFps.setTextColor(Color.RED);
            } else {
                dataView.textFps.setTextColor(Color.BLACK);
            }
            dataView.textFps.setText("fps:" + String.valueOf(fps));
        }
    }

    @SuppressLint("SetTextI18n")
    public void setMem(final int mem) {
        if (!PerformInstance.isIsWindow()) {
            return;
        }
        Message message = new Message();
        message.arg1 = mem;
        viewHandler.sendMessage(message);
    }

    private ViewHandler viewHandler = new ViewHandler(this);

    private static class ViewHandler extends Handler {
        private WeakReference<WindowTool> object;

        public ViewHandler(WindowTool object) {
            this.object = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            WindowTool obj = object.get();
            if (obj != null) {
                if (obj.dataView != null) {
                    obj.dataView.textMem.setText("mem:" + String.valueOf(msg.arg1) + "MB");
                }
            }

        }
    }

}
