package com.coderstory.Purify.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.coderstory.Purify.adapter.AppInfo;

import java.io.File;
import java.util.Vector;

import static com.coderstory.Purify.config.Misc.BackPath;


public class LoadApkInfo {
    public static Vector<String> apkAll = null;
    public static boolean needReload = false;

    // 获取当前目录下所有的apk文件
    public static Vector<String> GetApkFileName(String fileAbsolutePath) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        if (subFile != null) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    // 判断是否为apk结尾
                    if (filename.trim().toLowerCase().endsWith(".apk")) {
                        vecFile.add(filename);
                    }
                }
            }
        }
        return vecFile;
    }


    public static PackageInfo loadAppInfo(String appPath, Activity content) {
        AppInfo app = new AppInfo();
        app.setAppDir(BackPath + appPath);
        PackageManager pm = content.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(app.getAppDir(), PackageManager.GET_ACTIVITIES);
        return info;
    }


}
