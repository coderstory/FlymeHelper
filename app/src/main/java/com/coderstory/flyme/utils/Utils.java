package com.coderstory.flyme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import com.coderstory.flyme.config.Misc;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final String TAG = "Utils";

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private static long compareDay(String day1, String day2) {
        try {
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(day1);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(day2);
            return (d2.getTime() - d1.getTime()) / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean vi() {
        return vp() >= 0;
    }

    public static long vp() {
        return compareDay(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), Misc.endTime);
    }

    public static SharedPreferences getMySharedPreferences(Context context, String dir, String fileName) {
        try {
            // 获取 ContextWrapper对象中的mBase变量。该变量保存了 ContextImpl 对象
            Field field_mBase = ContextWrapper.class.getDeclaredField("mBase");
            field_mBase.setAccessible(true);
            // 获取 mBase变量
            Object obj_mBase = field_mBase.get(context);
            // 获取 ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            Field field_mPreferencesDir = obj_mBase.getClass().getDeclaredField("mPreferencesDir");
            field_mPreferencesDir.setAccessible(true);
            // 创建自定义路径
//            String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android";
            File file = new File(dir);
            // 修改mPreferencesDir变量的值
            field_mPreferencesDir.set(obj_mBase, file);
            // 返回修改路径以后的 SharedPreferences :%FILE_PATH%/%fileName%.xml
            Log.e(TAG, "getMySharedPreferences filep=" + file.getAbsolutePath() + "| fileName=" + fileName);
            return context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "getMySharedPreferences end filename=" + fileName);
        // 返回默认路径下的 SharedPreferences : /data/data/%package_name%/shared_prefs/%fileName%.xml
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
}
