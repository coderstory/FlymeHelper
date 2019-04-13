package com.coderstory.purify.module;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import java.io.File;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.purify.utils.ConfigPreferences.getInstance;


public class FlymeHome extends XposedHelper implements IModule {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        if (resparam.packageName.equals("com.meizu.flyme.launcher")) {
            resparam.res.setReplacement(resparam.packageName, "dimen", "iconsize", "20.0dip");
            resparam.res.setReplacement(resparam.packageName, "dimen", "app_icon_size", "20.0dip");
            resparam.res.setReplacement(resparam.packageName, "dimen", "iconsize_big", "20.0dip");
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.meizu.flyme.launcher")) {

            Class clazz = findClass("com.meizu.flyme.launcher.v", lpparam.classLoader);
            // 开启自定义布局
            // (String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
            if (getInstance().getBoolean("hide_icon_5", false)) {
                hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (param.args[0].getClass().equals(String.class)) {
                            // flyme5 359 518 5.0 4.0 55 13 4 55
                            param.args[3] = 5.0f; // y
                            param.args[4] = 5.0f; // x.
                            param.args[7] = 4.0f; // hotseat

                        }
                    }

                });
                // 不同布局使用不同的db
                hookAllConstructors(SQLiteOpenHelper.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam hookParam) {
                        if ("launcher.db".equals(hookParam.args[1])) {
                            Object arg = hookParam.args[0];
                            if (arg != null) {
                                String dbName = "launcher_coderStory.db";
                                XposedHelpers.setObjectField(hookParam.thisObject, "mName", dbName);

                                File file = ((Context) arg).getDatabasePath("launcher.db");
                                if (file != null && (file.exists())) {
                                    File databasePath = ((Context) arg).getDatabasePath(dbName);
                                    if (databasePath != null && (databasePath.exists())) {
                                        return;
                                    }
                                    writeFile(file, databasePath);
                                }
                            }
                        }
                    }
                });
            }


            if (getInstance().getBoolean("hide_icon_label", false)) {
                // 隐藏图标标签
                hookAllMethods(findClass("com.meizu.flyme.launcher.ShortcutIcon", lpparam.classLoader), "a", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        TextView textView = (TextView) XposedHelpers.getObjectField(param.thisObject, "c");
                        if (textView != null) {
                            textView.setVisibility(View.INVISIBLE);
                        }
                        ImageView imageView = (ImageView) XposedHelpers.getObjectField(param.thisObject, "a");
                        // 16th 默认宽高162 3.375比例

//                        int px = imageView.getLayoutParams().height;
//                        float set = Float.valueOf(getInstance().getString("Zoom", "1.0"));
//                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(px * set),
//                                (int)(px * set));//两个400分别为添加图片的大小
//                        imageView.setLayoutParams(params);
                    }
                });
                // 隐藏文件夹标签
                findAndHookMethod("com.meizu.flyme.launcher.FolderIcon", lpparam.classLoader, "setTextVisible", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = false;
                    }
                });
            }


            hookAllConstructors("com.meizu.flyme.launcher.u", lpparam.classLoader, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam hookParam) {
                    if (hookParam.args[0] instanceof String) {
                        hookParam.args[5] = 80;
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedHelpers.getFloatField(param.thisObject, "f");
                    XposedHelpers.setFloatField(param.thisObject, "f", 100f);
                }
            });

        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }
}
