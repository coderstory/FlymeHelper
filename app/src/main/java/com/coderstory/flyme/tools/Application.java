package com.coderstory.flyme.tools;


public class Application extends android.app.Application {
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        System.loadLibrary("Utils");
        Cpp.firstCpp(mInstance);
    }
}
