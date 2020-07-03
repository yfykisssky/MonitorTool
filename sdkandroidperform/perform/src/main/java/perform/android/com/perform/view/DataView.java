package perform.android.com.perform.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import perform.android.com.perform.R;

public class DataView extends LinearLayout {

    public TextView textFps;

    public TextView textMem;

    public DataView(Context context) {
        super(context);
        View view = LinearLayout.inflate(context, R.layout.view_perform, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(400, 500));
        textFps = view.findViewById(R.id.fps);
        textMem = view.findViewById(R.id.mem);
        this.addView(view);
    }
}
