package com.coderstory.flyme.utils;


public class Misc {
    private static final String BasePath = "/storage/emulated/0/Flyme_Purify";
    public static final String BackPath = BasePath + "/Backup/";
    public static final String CrashFilePath = BasePath + "/CrashLog/";
    public static final String MyBlogUrl = "https://blog.coderstory.cn";
    public static final String ApplicationName = "com.coderstory.flyme";
    public static final String SharedPreferencesName = "UserSettings";
    public static final String HostFileTmpName = "/hosts";
    public static boolean isProcessing = false;
    public static final String endTime = "2020-10-1";
    public static final String token = "5ee5d80f978eea081640e210";
    public static final String channel = "coolapk";
    public static final boolean isTestVersion = true;
    public static final String searchApi = Utils.decode(Cpp.hello());
    // 家里的电脑1
    //public static String key= "1b0fa963126a28f8bceca65b3085f2e8";
    // release 00:1A:C5:ED:6E:B5:BD:55:2B:10:5E:7E:C2:92:2D:A2:70:A0:CE:E7
    public static String key = "f814a22ab70c24e6db39cbe505c633ec";

}
