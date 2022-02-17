package com.coderstory.flyme.patchModule

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class FlymeHome : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {}
    override fun handleLoadPackage(param: LoadPackageParam) {
        if (param.packageName == "com.meizu.flyme.launcher") {
            XposedBridge.log("开始hook桌面")
            XposedBridge.log("获取到的参数个数" + prefs.all.size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (prefs.getBoolean("hide_icon_label", false)) {
                    // android 10
                    hookAllMethods(
                        "com.android.launcher3.BubbleTextView",
                        param.classLoader,
                        "setText",
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                super.beforeHookedMethod(param)

                                // 魅族17 shortcut 80  普通应用 146  魅族18 116 普通app 208
                                if (XposedHelpers.getIntField(param.thisObject, "mDisplay") != 4) {
                                    param.args[0] = ""
                                }
                            }
                        })
                }
                meizu17(param)
            } else {
                hook55(
                    findClass("com.meizu.flyme.launcher.u", param.classLoader),
                    param.classLoader
                )
                hook55(
                    findClass("com.meizu.flyme.launcher.v", param.classLoader),
                    param.classLoader
                )
                hook55(
                    findClass("com.meizu.flyme.launcher.w", param.classLoader),
                    param.classLoader
                )
                if (prefs.getBoolean("hide_icon_label", false)) {
                    //XposedBridge.log("开启隐藏标签");
                    // 隐藏图标标签
                    hookAllMethods(
                        findClass(
                            "com.meizu.flyme.launcher.ShortcutIcon",
                            param.classLoader
                        ), "a", object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam) {
                                super.beforeHookedMethod(param)
                                val textView =
                                    XposedHelpers.getObjectField(param.thisObject, "c") as TextView
                                textView.visibility = View.INVISIBLE
                            }
                        })
                    // 隐藏文件夹标签
                    findAndHookMethod(
                        "com.meizu.flyme.launcher.FolderIcon",
                        param.classLoader,
                        "setTextVisible",
                        Boolean::class.javaPrimitiveType,
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                super.beforeHookedMethod(param)
                                param.args[0] = false
                            }
                        })
                }
            }
            if (prefs.getBoolean("disableSearch", false)) {
                /**
                 * private void startSearchActivity() {
                 * Intent actUp = new Intent("com.meizu.net.search.main");
                 * actUp.setFlags(337707008);
                 * actUp.putExtra("from_app", "homeshell");
                 * this.mLauncher.startActivity(actUp);
                 * }
                 */
                if (findClassWithoutLog("com.meizu.flyme.g.a", param.classLoader) != null) {
                    findAndHookMethod(
                        "com.meizu.flyme.g.a",
                        param.classLoader,
                        "a",
                        XC_MethodReplacement.returnConstant(null)
                    )
                } else if (findClassWithoutLog(
                        "com.meizu.launcher3.controller.CommonTouchController",
                        param.classLoader
                    ) != null
                ) {
                    findAndHookMethod(
                        "com.meizu.launcher3.controller.CommonTouchController",
                        param.classLoader,
                        "startSearchActivity",
                        XC_MethodReplacement.returnConstant(null)
                    )
                }
            }
        }
    }

    private fun meizu17(lpparam: LoadPackageParam) {
        val config: JSONObject = json.getJSONObject("custom_launcher_icon_number")
        var numRows = prefs.getInt("home_icon_num_rows", 0)
        var numColumns = prefs.getInt("home_icon_num_column", 0)
        var numHotseatIcons = prefs.getInt("home_icon_num_hot_seat_icons", 0)
        if (numColumns + numRows + numHotseatIcons != 0) {
            numRows = prefs.getInt("home_icon_num_rows", 6)
            numColumns = prefs.getInt("home_icon_num_column", 4)
            numHotseatIcons = prefs.getInt("home_icon_num_hot_seat_icons", 4)
            // 解决桌面widget长度问题
            XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass(
                    "android.appwidget.AppWidgetHostView",
                    lpparam.classLoader
                ),
                "getAppWidgetInfo", object : XC_MethodHook() {
                    override fun beforeHookedMethod(arg5: MethodHookParam) {
                        val v0 = XposedHelpers.getObjectField(arg5.thisObject, "mInfo")
                        if (v0 != null) {
                            XposedHelpers.setIntField(v0, "resizeMode", 3)
                            XposedHelpers.setIntField(v0, "minResizeWidth", 40)
                            XposedHelpers.setIntField(v0, "minResizeHeight", 40)
                        }
                    }
                })
            hookAllConstructors(
                findClass(config.getString("class1"), lpparam.classLoader),
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        if (numRows != 0) XposedHelpers.setIntField(
                            param.thisObject,
                            "numRows",
                            numRows
                        )
                        if (numColumns != 0) XposedHelpers.setIntField(
                            param.thisObject,
                            "numColumns",
                            numColumns
                        )
                        if (numHotseatIcons != 0) XposedHelpers.setIntField(
                            param.thisObject,
                            "numHotseatIcons",
                            numHotseatIcons
                        )
                    }
                })
            hookAllConstructors(
                findClass(config.getString("class2"), lpparam.classLoader),
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        if (numRows != 0) XposedHelpers.setIntField(
                            param.thisObject,
                            "numRows",
                            numRows
                        )
                        if (numColumns != 0) XposedHelpers.setIntField(
                            param.thisObject,
                            "numColumns",
                            numColumns
                        )
                        if (numHotseatIcons != 0) XposedHelpers.setIntField(
                            param.thisObject,
                            "numHotseatIcons",
                            numHotseatIcons
                        )
                    }
                })

            if (findClass(config.getString("class3"), lpparam.classLoader) != null) {
                hookAllConstructors(SQLiteOpenHelper::class.java, object : XC_MethodHook() {
                    override fun afterHookedMethod(hookParam: MethodHookParam) {
                        if ("launcher.db" == hookParam.args[1]) {
                            val arg = hookParam.args[0]
                            if (arg != null) {
                                val dbName =
                                    "launcher_coderStory_" + (numColumns + numRows + numHotseatIcons) + ".db"
                                XposedHelpers.setObjectField(hookParam.thisObject, "mName", dbName)
                                val file = (arg as Context).getDatabasePath("launcher.db")
                                if (file != null && file.exists()) {
                                    val databasePath = arg.getDatabasePath(dbName)
                                    if (databasePath != null && databasePath.exists()) {
                                        return
                                    }
                                    with(XposedHelper) {
                                        if (databasePath != null && databasePath.exists()) {
                                            return
                                        }
                                        writeFile(file, databasePath)
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    private fun hook55(clazz: Class<*>, classLoader: ClassLoader) {
        // 开启自定义布局
        // deviceProfiles.add(new DeviceProfile("Flyme5", 359f, 518f, ((float)FlymeDeviceConfig.row), ((float)FlymeDeviceConfig.column), 55f, 13f, 4f, 55f));
        // (String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        var type = ""
        when {
            prefs.getBoolean("hide_icon_5", false) -> {
                type = "hide_icon_5"
            }
            prefs.getBoolean("hide_icon_6", false) -> {
                type = "hide_icon_6"
            }
            prefs.getBoolean("hide_icon_4", false) -> {
                type = "hide_icon_4"
            }
        }
        if ("" != type) {
            val finalType = type
            hookAllConstructors(clazz, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    if (param.args[0].javaClass == String::class.java) {
                        // flyme5 359 518 5.0 4.0 55 13 4 55
                        when (finalType) {
                            "hide_icon_5" -> {
                                param.args[3] = 5.0f // y
                                param.args[4] = 5.0f // x.
                            }
                            "hide_icon_6" -> {
                                param.args[3] = 6.0f // y
                                param.args[4] = 5.0f // x.
                            }
                            "hide_icon_4" -> {
                                param.args[3] = 5.0f // y
                                param.args[4] = 4.0f // x.
                            }
                        }
                        param.args[7] = 4.0f // hotseat
                    }
                }
            })
            if (findClass(
                    "com.android.launcher3.InvariantDeviceProfile\$GridOption",
                    classLoader
                ) == null
            ) {
                // 不同布局使用不同的db
                hookAllConstructors(SQLiteOpenHelper::class.java, object : XC_MethodHook() {
                    override fun afterHookedMethod(hookParam: MethodHookParam) {
                        if ("launcher.db" == hookParam.args[1]) {
                            val arg = hookParam.args[0]
                            if (arg != null) {
                                val dbName = finalType + "launcher_coderStory.db"
                                XposedHelpers.setObjectField(hookParam.thisObject, "mName", dbName)
                                val file = (arg as Context).getDatabasePath("launcher.db")
                                if (file != null && file.exists()) {
                                    val databasePath = arg.getDatabasePath(dbName)
                                    if (databasePath != null && databasePath.exists()) {
                                        return
                                    }
                                    with(XposedHelper) {
                                        if (databasePath != null && databasePath.exists()) {
                                            return
                                        }
                                        writeFile(file, databasePath)
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}
}
