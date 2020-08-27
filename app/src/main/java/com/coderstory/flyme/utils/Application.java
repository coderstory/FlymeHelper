package com.coderstory.flyme.utils;

import com.coderstory.flyme.BuildConfig;
import com.topjohnwu.superuser.BusyBoxInstaller;
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
                .setTimeout(10).setInitializers(BusyBoxInstaller.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(Application.this, Misc.token, Misc.channel, UMConfigure.DEVICE_TYPE_PHONE, "");
        mInstance = this;
        System.loadLibrary("Utils");
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        Cpp.initCpp(mInstance);
    }
}
