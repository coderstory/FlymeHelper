package com.coderstory.flyme.utils;

import android.util.Log;

import com.coderstory.flyme.config.Misc;
import com.umeng.commonsdk.UMConfigure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Application extends android.app.Application {
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        if (!getCurrSOLoaded("libnc.so")) {
            try {
                System.loadLibrary("nc");
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }
            super.onCreate();
            UMConfigure.setLogEnabled(true);
            UMConfigure.init(Application.this, Misc.token, Misc.channel, UMConfigure.DEVICE_TYPE_PHONE, "");
            mInstance = this;
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
    }

    private static List<String> allSOLists = new ArrayList<String>();


    /**
     * 获取当前应用已加载的SO库
     */
    private boolean getCurrSOLoaded(String soName) {
        allSOLists.clear();
        // 当前应用的进程ID
        int pid = android.os.Process.myPid();
        String path = "/proc/" + pid + "/maps";
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            readFileByLines(file.getAbsolutePath());
        } else {
            Log.e("CLOUDWISE", "不存在[" + path + "]文件.");
        }
        return allSOLists.stream().allMatch(name -> name.contains(soName));
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if (tempString.endsWith(".so")) {
                    int index = tempString.indexOf("/");
                    if (index != -1) {
                        String str = tempString.substring(index);
                        // 所有so库（包括系统的，即包含/system/目录下的）
                        allSOLists.add(str);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

}
