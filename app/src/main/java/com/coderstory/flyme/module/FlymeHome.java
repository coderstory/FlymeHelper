package com.coderstory.flyme.module;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.XposedHelper;

import java.io.File;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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
        super.handleLoadPackage(lpparam);
        if (lpparam.packageName.equals("com.meizu.flyme.launcher")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (prefs.getBoolean("hide_icon_label", false)) {
                    // android 10
                    hookAllMethods("com.android.launcher3.BubbleTextView", lpparam.classLoader, "setText", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if (XposedHelpers.getIntField(param.thisObject, "mIconSize") > 100) {
                                param.args[0] = "";
                            }
                        }
                    });

                    hookAllMethods("com.meizu.launcher3.view.MzFolderBubbleTextView", lpparam.classLoader, "setText", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[0] = "";
                        }
                    });
                }
                meizu17(lpparam);
            } else {
                hook55(findClass("com.meizu.flyme.launcher.u", lpparam.classLoader), lpparam.classLoader);
                hook55(findClass("com.meizu.flyme.launcher.v", lpparam.classLoader), lpparam.classLoader);
                hook55(findClass("com.meizu.flyme.launcher.w", lpparam.classLoader), lpparam.classLoader);
                if (prefs.getBoolean("hide_icon_label", false)) {
                    //XposedBridge.log("开启隐藏标签");
                    // 隐藏图标标签
                    hookAllMethods(findClass("com.meizu.flyme.launcher.ShortcutIcon", lpparam.classLoader), "a", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            TextView textView = (TextView) XposedHelpers.getObjectField(param.thisObject, "c");
                            if (textView != null) {
                                textView.setVisibility(View.INVISIBLE);
                            }
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
            }


            if (prefs.getBoolean("disableSearch", false)) {
                /**
                 *     private void startSearchActivity() {
                 *         Intent actUp = new Intent("com.meizu.net.search.main");
                 *         actUp.setFlags(337707008);
                 *         actUp.putExtra("from_app", "homeshell");
                 *         this.mLauncher.startActivity(actUp);
                 *     }
                 */
                if (findClassWithoutLog("com.meizu.flyme.g.a", lpparam.classLoader) != null) {
                    findAndHookMethod("com.meizu.flyme.g.a", lpparam.classLoader, "a", XC_MethodReplacement.returnConstant(null));
                } else {
                    findAndHookMethod("com.meizu.launcher3.controller.CommonTouchController", lpparam.classLoader, "startSearchActivity", XC_MethodReplacement.returnConstant(null));
                }
            }

        }
    }


    private void meizu17(XC_LoadPackage.LoadPackageParam lpparam) {
        JSONObject config = json.getJSONObject("custom_launcher_icon_number");
        int numRows = prefs.getInt("home_icon_num_rows", 0);
        int numColumns = prefs.getInt("home_icon_num_column", 0);
        int numHotseatIcons = prefs.getInt("home_icon_num_hot_seat_icons", 0);
        if (numColumns + numRows + numHotseatIcons != 0) {
            hookAllConstructors(findClass(config.getString("class1"), lpparam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (numRows != 0)
                        XposedHelpers.setIntField(param.thisObject, "numRows", numRows);
                    if (numColumns != 0)
                        XposedHelpers.setIntField(param.thisObject, "numColumns", numColumns);
                    if (numHotseatIcons != 0)
                        XposedHelpers.setIntField(param.thisObject, "numHotseatIcons", numHotseatIcons);
                }
            });
            hookAllConstructors(findClass(config.getString("class2"), lpparam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (numRows != 0)
                        XposedHelpers.setIntField(param.thisObject, "numRows", numRows);
                    if (numColumns != 0)
                        XposedHelpers.setIntField(param.thisObject, "numColumns", numColumns);
                    if (numHotseatIcons != 0)
                        XposedHelpers.setIntField(param.thisObject, "numHotseatIcons", numHotseatIcons);
                }
            });

            if (findClass(config.getString("class3"), lpparam.classLoader) != null) {
                hookAllConstructors(SQLiteOpenHelper.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam hookParam) {
                        if ("launcher.db".equals(hookParam.args[1])) {
                            Object arg = hookParam.args[0];
                            if (arg != null) {
                                String dbName = "launcher_coderStory_" + (numColumns + numRows + numHotseatIcons) + ".db";
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
        }
    }

    private void hook55(Class clazz, ClassLoader classLoader) {
        // 开启自定义布局
        // deviceProfiles.add(new DeviceProfile("Flyme5", 359f, 518f, ((float)FlymeDeviceConfig.row), ((float)FlymeDeviceConfig.column), 55f, 13f, 4f, 55f));
        // (String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        String type = "";
        if (prefs.getBoolean("hide_icon_5", false)) {
            type = "hide_icon_5";
        } else if (prefs.getBoolean("hide_icon_6", false)) {
            type = "hide_icon_6";
        } else if (prefs.getBoolean("hide_icon_4", false)) {
            type = "hide_icon_4";
        }
        if (!"".equals(type)) {
            String finalType = type;
            hookAllConstructors(clazz, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param.args[0].getClass().equals(String.class)) {
                        // flyme5 359 518 5.0 4.0 55 13 4 55
                        switch (finalType) {
                            case "hide_icon_5":
                                param.args[3] = 5.0f; // y
                                param.args[4] = 5.0f; // x.
                                break;
                            case "hide_icon_6":
                                param.args[3] = 6.0f; // y
                                param.args[4] = 5.0f; // x.
                                break;
                            case "hide_icon_4":
                                param.args[3] = 5.0f; // y
                                param.args[4] = 4.0f; // x.
                        }
                        param.args[7] = 4.0f; // hotseat
                    }
                }

            });
            if (findClass("com.android.launcher3.InvariantDeviceProfile$GridOption", classLoader) == null) {
                // 不同布局使用不同的db
                hookAllConstructors(SQLiteOpenHelper.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam hookParam) {
                        if ("launcher.db".equals(hookParam.args[1])) {
                            Object arg = hookParam.args[0];
                            if (arg != null) {
                                String dbName = finalType + "launcher_coderStory.db";
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
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }
}
