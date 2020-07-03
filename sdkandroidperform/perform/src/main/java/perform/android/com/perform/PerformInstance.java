package perform.android.com.perform;

import android.content.Context;

import perform.android.com.perform.util.DBTCollect;

public class PerformInstance {

    private static boolean isUse = false;

    private static boolean useStart = false;

    private static boolean isSave = false;

    private static boolean isWindow = false;

    public static boolean isIsWindow() {
        return isWindow;
    }

    public static void setIsWindow(boolean isWindow) {
        PerformInstance.isWindow = isWindow;
    }

    public static boolean isIsSave() {
        return isSave;
    }

    public static void setIsSave(boolean isSave) {
        PerformInstance.isSave = isSave;
    }

    public static boolean isIsUse() {
        return isUse;
    }

    public static void setIsUse(boolean isUse) {
        PerformInstance.isUse = isUse;
    }

    public static boolean isUseStart() {
        return useStart;
    }

    public static void setUseStart(boolean useStart) {
        PerformInstance.useStart = useStart;
    }

    public void init(Context con) {
        context = con;
        if (isSave) {
            DBTCollect.createTableSQL();
        }
       /* if (isWindow) {
            WindowTool.getInstance().init(context);
        }*/
    }

    private static Context context;

    public static Context getContext() {
        return context;
    }

    private static volatile PerformInstance instance;

    public static PerformInstance getInstance() {

        if (instance == null) {
            synchronized (PerformInstance.class) {
                if (instance == null) {
                    instance = new PerformInstance();
                }
            }
        }
        return instance;
    }

}
