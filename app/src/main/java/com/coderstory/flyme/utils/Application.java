package com.coderstory.flyme.utils;

import android.os.Debug;

import com.umeng.commonsdk.UMConfigure;


public class Application extends android.app.Application {
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.setLogEnabled(false);
        UMConfigure.init(Application.this, Misc.token, Misc.channel, UMConfigure.DEVICE_TYPE_PHONE, "");
        mInstance = this;
        //if (!Debug.isDebuggerConnected()) {
            System.loadLibrary("Utils");
        //}
        CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(getApplicationContext());
        Cpp.initCpp(mInstance);
    }
}
