package com.coderstory.flyme.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.plugins.IModule;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.flyme.utils.Misc.ApplicationName;

public class XposedHelper implements IModule {


    protected XSharedPreferences prefs = new XSharedPreferences(new File("/data/user_de/0/" + ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml"));
    public static JSONObject json = new JSONObject();

    {
        prefs.makeWorldReadable();
        prefs.reload();
    }

    public static Class findClass(String classpatch, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(classpatch, classLoader);
        } catch (XposedHelpers.ClassNotFoundError error) {
            XposedBridge.log(error.getMessage());
        }
        return null;
    }


    public static void findAndHookMethod(String p1, ClassLoader lpparam, String p2, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(p1, lpparam, p2, parameterTypesAndCallback);

        } catch (Throwable error) {
            XposedBridge.log(error);
        }
    }

    public static void findAndHookMethod(Class<?> p1, String p2, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(p1, p2, parameterTypesAndCallback);
        } catch (Throwable error) {
            XposedBridge.log(error);
        }
    }

    public static void hookAllConstructors(String p1, ClassLoader classLoader, XC_MethodHook parameterTypesAndCallback) {
        try {
            Class packageParser = XposedHelpers.findClass(p1, classLoader);
            XposedBridge.hookAllConstructors(packageParser, parameterTypesAndCallback);

        } catch (Throwable error) {
            XposedBridge.log(error);
        }
    }

    protected static void findAndHookMethod(String p1, String p2, Object[] p3) {
        try {
            XposedHelpers.findAndHookMethod(Class.forName(p1), p2, p3);
        } catch (Throwable error) {
            XposedBridge.log(error);
        }
    }

    public static int hookAllMethods(String p1, ClassLoader lpparam, String methodName, XC_MethodHook parameterTypesAndCallback) {
        try {
            Class packageParser = XposedHelpers.findClass(p1, lpparam);
            return XposedBridge.hookAllMethods(packageParser, methodName, parameterTypesAndCallback).size();
        } catch (Throwable error) {
            XposedBridge.log(error);
            return 0;
        }
    }

    public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        try {
            return XposedBridge.hookAllMethods(hookClass, methodName, callback);
        } catch (Throwable error) {
            XposedBridge.log(error.getMessage());
        }
        return null;
    }

    public static void writeFile(File file, File file1) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream;
        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(file1);
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte[] bytes = new byte[5120];
            while (true) {
                int read = bufferedInputStream.read(bytes);
                if (read == -1) {
                    break;
                }

                bufferedOutputStream.write(bytes, 0, read);
            }
            bufferedOutputStream.flush();
            bufferedInputStream.close();
            bufferedOutputStream.close();
            fileOutputStream.close();
            fileInputStream.close();

        } catch (IOException error) {
            XposedBridge.log(error);
        }
    }

    protected Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
        try {
            return XposedBridge.hookAllConstructors(hookClass, callback);
        } catch (Throwable error) {
            XposedBridge.log(error.getMessage());
            return new HashSet<>();
        }
    }

    protected Class findClassWithoutLog(String classpatch, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(classpatch, classLoader);
        } catch (XposedHelpers.ClassNotFoundError error) {
            error.printStackTrace();
        }
        return null;
    }

    /**
     * Find apk file file. 获取需要Hook Apk文件地址
     *
     * @param context           the context
     * @param modulePackageName the module package name
     * @return the file
     */
    private File findApkFile(Context context, String modulePackageName) {
        if (context == null) {
            throw new RuntimeException("Can't Get Context");
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            String apkPath = moudleContext.getPackageCodePath();
            return new File(apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.loge(String.format("Find File Error，Package:%s", modulePackageName));
            return null;
        }
    }

    /**
     * Sets need hook package.
     *
     * @param context the context
     */
    private void getConfig(Context context) {
        try {
            String path = findApkFile(context, BuildConfig.APPLICATION_ID).toString();
            ZipFile zipFile = new ZipFile(path);
            ZipEntry zipEntry = zipFile.getEntry("assets/config");
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            InputStreamReader in = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(in);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            json = JSON.parseObject(sb.toString());

        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        try {
            // 获取context对象
            Context context = (Context) XposedHelpers.callMethod(
                    XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass(
                                    "android.app.ActivityThread",
                                    loadPackageParam.classLoader
                            ),
                            "currentActivityThread"
                    ),
                    "getSystemContext"
            );
            getConfig(context);
        } catch (Exception e) {
            Logger.loge(String.format("Set NeedHookPackage Accounding:%s Error", BuildConfig.APPLICATION_ID));
        }

//            if ((hookPackages != null) && (hookPackages.contains(loadPackageParam.processName)) && (!loadPackageParam.processName.equals(modulePackage))) {
//                try {
//
//                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                            Logger.logi(String.format("Get Needed Hook Package:%s", loadPackageParam.packageName));
//                            Context context = (Context) param.args[0];
//                            loadPackageParam.classLoader = context.getClassLoader();
//                            invokeHandleHookMethod(context, modulePackage, handleHookClass, handleHookMethod, loadPackageParam);
//                        }
//                    });
//                } catch (Exception e) {
//                    Logger.loge(String.format("Invoke Hook Method Error, Package:%s", loadPackageParam.packageName));
//                }
//            }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }
}
