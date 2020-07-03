package android.bestpay.xposedperform;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileWriter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TimeHook implements IXposedHookLoadPackage {

    //用于加密包和混淆包记录初始启动时间
    //加密和混淆包无法hook,需要代码插桩进行记录
    private String TEST_PKG_NAME = "com.xxx.xxx";
    private String RECORD_PATH = "/storage/emulated/0/Android/recoedfile";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!TextUtils.isEmpty(TEST_PKG_NAME)) {
            if (TEST_PKG_NAME.equals(lpparam.packageName)) {
                final long nowTime = System.currentTimeMillis();
                File file = new File(RECORD_PATH);
                if (!file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                FileWriter writer = new FileWriter(file);
                // 向文件写入内容
                writer.write(String.valueOf(nowTime));
                writer.flush();
                writer.close();
            }
        }else{

            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.hasThrowable()) return;
                    Class<?> cls = (Class<?>) param.getResult();
                    String name = cls.getName();
                    String classname = (String) param.args[0]; //步骤1
                    XposedBridge.log("classname " + name);
                    if (classname.startsWith(TEST_PKG_NAME)) {

                        //监听Activity onCreate -> onStart -> onResume方法
                        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", null, new XC_MethodHook() {//步骤4
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {//步骤5
                                super.afterHookedMethod(param);
                                long startTime = System.currentTimeMillis();
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                long endTime = System.currentTimeMillis();
                            }
                        });

                        //监听View onMeasure->View onLayout -> View onDraw方法
                        XposedHelpers.findAndHookMethod(View.class, "onMeasure", null, new XC_MethodHook() {//步骤4
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {//步骤5
                                super.afterHookedMethod(param);
                                long startTime = System.currentTimeMillis();
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                long endTime = System.currentTimeMillis();
                            }
                        });
                    }
                }
            });
        }

    }
}
