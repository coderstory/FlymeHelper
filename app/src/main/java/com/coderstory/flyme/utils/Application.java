package com.coderstory.flyme.utils;

import com.coderstory.flyme.BuildConfig;
import com.topjohnwu.superuser.Shell;
import com.umeng.commonsdk.UMConfigure;


public class Application extends android.app.Application {
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(100));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(Application.this, Misc.token, Misc.channel, UMConfigure.DEVICE_TYPE_PHONE, "");
        mInstance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

}
