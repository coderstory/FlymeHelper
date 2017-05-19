package com.coderstory.FTool.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.coderstory.FTool.R;

public class SplashActivity extends Activity {

    private static final int SHOW_TIME_MIN = 1200;
    protected  static Context mContext=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;

        //倒计时返回主界面
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPostExecute(Integer result) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(mContext, getString(R.string.not_support_android_target_version)+Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }.start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                long startTime = System.currentTimeMillis();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return 1;
            }
        }.execute();
    }
}
