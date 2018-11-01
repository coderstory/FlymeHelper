package com.coderstory.purify.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.coderstory.purify.config.Misc.ApplicationName;
import static com.coderstory.purify.config.Misc.SharedPreferencesName;

public class XposedHelper {
    protected XSharedPreferences prefs = new XSharedPreferences(ApplicationName, SharedPreferencesName);

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

        } catch (Throwable localString3) {
            XposedBridge.log(localString3);
        }
    }

    public static void findAndHookMethod(Class<?> p1, String p2, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(p1, p2, parameterTypesAndCallback);
        } catch (Throwable localString3) {
            XposedBridge.log(localString3);
        }
    }

    public static void hookAllConstructors(String p1, XC_MethodHook parameterTypesAndCallback) {
        try {
            Class packageParser = XposedHelpers.findClass(p1, null);
            XposedBridge.hookAllConstructors(packageParser, parameterTypesAndCallback);

        } catch (Throwable localString3) {
            XposedBridge.log(localString3);
        }
    }

    protected static void findAndHookMethod(String p1, String p2, Object[] p3) {
        try {
            XposedHelpers.findAndHookMethod(Class.forName(p1), p2, p3);
        } catch (Throwable localString3) {
            XposedBridge.log(localString3);
        }
    }

    public static void hookAllMethods(String p1, ClassLoader lpparam, String methodName, XC_MethodHook parameterTypesAndCallback) {
        try {
            Class packageParser = XposedHelpers.findClass(p1, lpparam);
            XposedBridge.hookAllMethods(packageParser, methodName, parameterTypesAndCallback);

        } catch (Throwable localString3) {
            XposedBridge.log(localString3);
        }
    }

    public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        try {
            return XposedBridge.hookAllMethods(hookClass, methodName, callback);
        } catch (Throwable localString3) {
            XposedBridge.log(localString3.getMessage());
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

    public Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
        try {
            return XposedBridge.hookAllConstructors(hookClass, callback);
        } catch (Throwable localString3) {
            XposedBridge.log(localString3.getMessage());
        }
        return null;
    }

    public Class findClassWithoutLog(String classpatch, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(classpatch, classLoader);
        } catch (XposedHelpers.ClassNotFoundError error) {
        }
        return null;
    }

}
