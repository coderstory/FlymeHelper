package com.coderstory.flyme.utils;

import com.umeng.commonsdk.UMConfigure;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(Application.this, "5ee5d80f978eea081640e210", "qq_group", UMConfigure.DEVICE_TYPE_PHONE, "");

    }

}
