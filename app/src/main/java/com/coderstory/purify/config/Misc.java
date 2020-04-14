package com.coderstory.purify.config;


public class Misc {
    private static final String BasePath = "/storage/emulated/0/Flyme_Purify";
    public static final String SystemPckagePath = "/storage/emulated/0/SystemPackage";
    public static final String BackPath = BasePath + "/Backup/";
    public static final String CrashFilePath = BasePath + "/CrashLog/";
    public static final String MyBlogUrl = "https://blog.coderstory.cn";
    public static final String ApplicationName = "com.coderstory.flyme";
    public static final String SharedPreferencesName = "UserSettings";
    public static final String HostFileTmpName = "/hosts";
    public static boolean isProcessing = false;

    public static boolean isEnable() {
        return true;
    }
}
