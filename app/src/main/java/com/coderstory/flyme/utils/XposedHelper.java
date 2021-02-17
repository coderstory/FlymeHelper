package com.coderstory.flyme.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coderstory.flyme.BuildConfig;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.flyme.utils.Misc.ApplicationName;

public class XposedHelper {
    public String SIGNATURE = "308203c6308202aea003020102021426d148b7c65944abcf3a683b4c3dd3b139c4ec85300d06092a864886f70d01010b05003074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f6964301e170d3139303130323138353233385a170d3439303130323138353233385a3074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696430820122300d06092a864886f70d01010105000382010f003082010a028201010087fcde48d9beaeba37b733a397ae586fb42b6c3f4ce758dc3ef1327754a049b58f738664ece587994f1c6362f98c9be5fe82c72177260c390781f74a10a8a6f05a6b5ca0c7c5826e15526d8d7f0e74f2170064896b0cf32634a388e1a975ed6bab10744d9b371cba85069834bf098f1de0205cdee8e715759d302a64d248067a15b9beea11b61305e367ac71b1a898bf2eec7342109c9c5813a579d8a1b3e6a3fe290ea82e27fdba748a663f73cca5807cff1e4ad6f3ccca7c02945926a47279d1159599d4ecf01c9d0b62e385c6320a7a1e4ddc9833f237e814b34024b9ad108a5b00786ea15593a50ca7987cbbdc203c096eed5ff4bf8a63d27d33ecc963990203010001a350304e300c0603551d13040530030101ff301d0603551d0e04160414a361efb002034d596c3a60ad7b0332012a16aee3301f0603551d23041830168014a361efb002034d596c3a60ad7b0332012a16aee3300d06092a864886f70d01010b0500038201010022ccb684a7a8706f3ee7c81d6750fd662bf39f84805862040b625ddf378eeefae5a4f1f283deea61a3c7f8e7963fd745415153a531912b82b596e7409287ba26fb80cedba18f22ae3d987466e1fdd88e440402b2ea2819db5392cadee501350e81b8791675ea1a2ed7ef7696dff273f13fb742bb9625fa12ce9c2cb0b7b3d94b21792f1252b1d9e4f7012cb341b62ff556e6864b40927e942065d8f0f51273fcda979b8832dd5562c79acf719de6be5aee2a85f89265b071bf38339e2d31041bc501d5e0c034ab1cd9c64353b10ee70b49274093d13f733eb9d3543140814c72f8e003f301c7a00b1872cc008ad55e26df2e8f07441002c4bcb7dc746745f0db";

    protected XSharedPreferences prefs;
    public static JSONObject json = new JSONObject();

    {
        prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, Misc.SharedPreferencesName);
        if (prefs.getAll().keySet().size() == 0) {
            prefs = new XSharedPreferences(new File("/data/user_de/0/" + ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml"));
        }
        XposedBridge.log("当前助手配置 -> " + JSON.toJSON(prefs));
    }

    public static Class<?> findClass(String classpatch, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(classpatch, classLoader);
        } catch (XposedHelpers.ClassNotFoundError error) {
            XposedBridge.log(error);
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
            Class<?> packageParser = XposedHelpers.findClass(p1, classLoader);
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
            int count = XposedBridge.hookAllMethods(packageParser, methodName, parameterTypesAndCallback).size();
            if (count == 0) {
                XposedBridge.log("类" + p1 + "中的方法" + methodName + "没有被hook到");
            }
            return count;
        } catch (Throwable error) {
            XposedBridge.log(error);
            return 0;
        }
    }

    public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        try {
            Set<XC_MethodHook.Unhook> result = XposedBridge.hookAllMethods(hookClass, methodName, callback);
            if (result.size() == 0) {
                XposedBridge.log("类" + hookClass.getName() + "中的方法" + methodName + "没有被hook到");
            }
            return result;
        } catch (Throwable error) {
            XposedBridge.log(error);
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
            Set<XC_MethodHook.Unhook> result = XposedBridge.hookAllConstructors(hookClass, callback);
            if (result.size() == 0) {
                XposedBridge.log("类" + hookClass.getName() + "中的构造方法没有被hook到");
            }
            return result;
        } catch (Throwable error) {
            XposedBridge.log(error);
            return new HashSet<>();
        }
    }

    protected Class findClassWithoutLog(String classpatch, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(classpatch, classLoader);
        } catch (XposedHelpers.ClassNotFoundError error) {
            //XposedBridge.log(error);
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
            json = new Gson().fromJson(sb.toString(), JSONObject.class);

            br.close();
            in.close();
            inputStream.close();
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }

    public void initJson(XC_LoadPackage.LoadPackageParam loadPackageParam) throws IllegalAccessException, InvocationTargetException, InstantiationException {
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
    }
}
