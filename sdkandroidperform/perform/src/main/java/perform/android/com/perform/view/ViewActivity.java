package perform.android.com.perform.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import perform.android.com.perform.R;
import perform.android.com.perform.tool.CpuMemTool;
import perform.android.com.perform.tool.FPSTool;
import perform.android.com.perform.tool.TimeRecordTool;
import perform.android.com.perform.util.DBTCollect;
import perform.android.com.perform.util.SystemUtil;

public class ViewActivity extends Activity implements View.OnClickListener {

    private ListView listView;

    private ArrayList<FPSTool.FpsData> fpsDataArrayList = new ArrayList<>();

    private ArrayList<TimeRecordTool.TimeData> timeDataArrayList = new ArrayList<>();

    private ArrayList<CpuMemTool.MemSaveData> memSaveDataArrayList = new ArrayList<>();

    private MyAdapter myAdapter;

    private int select = 0;

    private EditText tagEdit;

    private EditText conditionEdit;

    private boolean isCondition = false;

    private Button judgeBnt;

    private String tagStr;

    private String conditionStr;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        listView = findViewById(R.id.list);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        ((TextView) findViewById(R.id.msg)).setText(
                "厂商:" + SystemUtil.getDeviceBrand() +
                        " 型号:" + SystemUtil.getSystemModel() +
                        " 版本:" + SystemUtil.getSystemVersion() + "\n"
                        + "APP版本:" + SystemUtil.getVersionName(this));

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

        findViewById(R.id.timecount).setOnClickListener(this);
        findViewById(R.id.fpscount).setOnClickListener(this);
        findViewById(R.id.cpu).setOnClickListener(this);
        findViewById(R.id.cvs).setOnClickListener(this);

        judgeBnt = findViewById(R.id.judge);
        judgeBnt.setOnClickListener(this);
        tagEdit = findViewById(R.id.tag);

        conditionEdit = findViewById(R.id.condition);

        findViewById(R.id.clear).setOnClickListener(this);

        findViewById(R.id.tagbnt).setOnClickListener(this);

        findViewById(R.id.reset).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        isCondition = false;
        if (i == R.id.timecount) {
            select = 0;
            findViewById(R.id.control).setVisibility(View.VISIBLE);
            timeDataArrayList = DBTCollect.getAllTimeData();
            conditionEdit.setVisibility(View.VISIBLE);
            judgeBnt.setVisibility(View.VISIBLE);
            tagEdit.setVisibility(View.VISIBLE);
            myAdapter.notifyDataSetChanged();
        } else if (i == R.id.fpscount) {
            select = 1;
            findViewById(R.id.control).setVisibility(View.VISIBLE);
            fpsDataArrayList = DBTCollect.getAllFpsData();
            myAdapter.notifyDataSetChanged();
            conditionEdit.setVisibility(View.GONE);
            judgeBnt.setVisibility(View.GONE);
            tagEdit.setVisibility(View.VISIBLE);
        } else if (i == R.id.cpu) {
            select = 2;
            findViewById(R.id.control).setVisibility(View.GONE);
            memSaveDataArrayList = DBTCollect.getAllMemData();
            conditionEdit.setVisibility(View.GONE);
            judgeBnt.setVisibility(View.GONE);
            tagEdit.setVisibility(View.GONE);
            myAdapter.notifyDataSetChanged();
        } else if (i == R.id.clear) {
            switch (select) {
                case 0:
                    DBTCollect.clearTimeData();
                    timeDataArrayList.clear();
                    break;
                case 1:
                    DBTCollect.clearFpsData();
                    fpsDataArrayList.clear();
                    break;
                case 2:
                    DBTCollect.clearMemData();
                    memSaveDataArrayList.clear();
                    break;

            }
            myAdapter.notifyDataSetChanged();
        } else if (i == R.id.tagbnt) {
            tagStr = tagEdit.getText().toString();

            if (TextUtils.isEmpty(tagStr)) {
                return;
            }
            switch (select) {
                case 0:
                    for (int g = 0; g < timeDataArrayList.size(); g++) {
                        if (!TextUtils.equals(tagStr, timeDataArrayList.get(g).tag)) {
                            timeDataArrayList.remove(g);
                            g--;
                        }
                    }
                    break;
                case 1:
                    for (int g = 0; g < fpsDataArrayList.size(); g++) {
                        if (!TextUtils.equals(tagStr, fpsDataArrayList.get(g).tag)) {
                            fpsDataArrayList.remove(g);
                            g--;
                        }
                    }
                    break;
            }

            myAdapter.notifyDataSetChanged();

        } else if (i == R.id.reset) {
            switch (select) {
                case 0:
                    timeDataArrayList = DBTCollect.getAllTimeData();
                    break;
                case 1:
                    fpsDataArrayList = DBTCollect.getAllFpsData();
                    break;
                case 2:
                    memSaveDataArrayList = DBTCollect.getAllMemData();
                    break;
            }
            myAdapter.notifyDataSetChanged();
        } else if (i == R.id.judge) {
            conditionStr = conditionEdit.getText().toString();
            if (TextUtils.isEmpty(conditionStr)) {
                return;
            }
            isCondition = true;
            myAdapter.notifyDataSetChanged();
        } else if (i == R.id.cvs) {
            String path = null;
            switch (select) {
                case 0:
                    path = DBTCollect.cvsTimeData();
                    break;
                case 1:
                    path = DBTCollect.cvsFpsData();
                    break;
                case 2:
                    //path = DBTCollect.cvsMemData();
                    break;
            }

            String title = "";

            if (TextUtils.isEmpty(path)) {
                title = "保存失败";
            } else {
                title = "保存成功";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage("路径:" + path);
            builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (select == 1) {
                return fpsDataArrayList.size();
            } else if (select == 0) {
                return timeDataArrayList.size();
            } else if (select == 2) {
                return memSaveDataArrayList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (select == 1) {
                @SuppressLint("ViewHolder")
                View view = LayoutInflater.from(ViewActivity.this).inflate(R.layout.item_fps_perform, null);
                ((TextView) view.findViewById(R.id.id)).setText(String.valueOf(fpsDataArrayList.get(position).id));
                ((TextView) view.findViewById(R.id.tag)).setText(fpsDataArrayList.get(position).tag);
                ((TextView) view.findViewById(R.id.maxLow)).setText(String.valueOf(fpsDataArrayList.get(position).maxLow));
                ((TextView) view.findViewById(R.id.minLow)).setText(String.valueOf(fpsDataArrayList.get(position).minLow));
                ((TextView) view.findViewById(R.id.time)).setText(String.valueOf(fpsDataArrayList.get(position).time));
                ((TextView) view.findViewById(R.id.countLow)).setText(String.valueOf(fpsDataArrayList.get(position).countLow));
                return view;
            } else if (select == 0) {
                @SuppressLint("ViewHolder")
                View view = LayoutInflater.from(ViewActivity.this).inflate(R.layout.item_time_perform, null);
                ((TextView) view.findViewById(R.id.id)).setText(String.valueOf(timeDataArrayList.get(position).id));
                ((TextView) view.findViewById(R.id.tag)).setText(timeDataArrayList.get(position).tag);
                ((TextView) view.findViewById(R.id.data)).setText(timeDataArrayList.get(position).dataStr);

                if (isCondition) {
                    try {
                        JSONObject jsonObject = new JSONObject(timeDataArrayList.get(position).dataStr);
                        JSONArray array = jsonObject.getJSONArray(timeDataArrayList.get(position).tag);
                        long start = 0;
                        long end = 0;
                        for (int r = 0; r < array.length(); r++) {
                            JSONObject object = array.getJSONObject(r);
                            if (object.has("start")) {
                                start = Long.valueOf(object.getString("start"));
                            }
                            if (object.has("end")) {
                                end = Long.valueOf(object.getString("end"));
                            }
                        }

                        if ((end - start) >= Long.valueOf(conditionStr)) {
                            view.findViewById(R.id.judge).setBackgroundColor(Color.RED);
                        } else {
                            view.findViewById(R.id.judge).setBackgroundColor(Color.GREEN);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    view.findViewById(R.id.judge).setBackgroundColor(Color.WHITE);
                }

                return view;
            } else if (select == 2) {
                @SuppressLint("ViewHolder")
                View view = LayoutInflater.from(ViewActivity.this).inflate(R.layout.item_mem_perform, null);
                ((TextView) view.findViewById(R.id.id)).setText(String.valueOf(memSaveDataArrayList.get(position).id));
                ((TextView) view.findViewById(R.id.data)).setText(String.valueOf(memSaveDataArrayList.get(position).data));
                return view;
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
