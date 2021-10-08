package com.coderstory.flyme.tools

object Misc {
    const val MyBlogUrl = "https://blog.coderstory.cn"
    const val ApplicationName = "com.coderstory.flyme"
    const val SharedPreferencesName = "UserSettings"
    const val HostFileTmpName = "/hosts"
    const val endTime = "2021-11-29"
    const val isTestVersion = true
    val searchApi: String = Utils.decode(Cpp.helloWorld())
    private const val BasePath = "/storage/emulated/0/Flyme_Purify"
    const val BackPath = BasePath + "/Backup/"
    const val CrashFilePath = BasePath + "/CrashLog/"
    var isProcessing = false

    // 家里的电脑1
    //public static String key= "1b0fa963126a28f8bceca65b3085f2e8";
    // release 00:1A:C5:ED:6E:B5:BD:55:2B:10:5E:7E:C2:92:2D:A2:70:A0:CE:E7
    var key = "f814a22ab70c24e6db39cbe505c633ec"
}
