package com.coderstory.flyme.utils;

import com.umeng.commonsdk.UMConfigure;

public class Application extends android.app.Application {
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(Application.this, "5ee5d80f978eea081640e210", "release", UMConfigure.DEVICE_TYPE_PHONE, "");
        mInstance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

}
