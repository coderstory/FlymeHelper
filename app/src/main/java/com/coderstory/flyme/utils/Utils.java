package com.coderstory.flyme.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.topjohnwu.superuser.Shell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Utils {
    private static final String TAG = "Utils";

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private static long compareDay(String day1, String day2) {
        try {
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(day1);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(day2);
            return (d2.getTime() - d1.getTime()) / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean vi() {
        return vp() >= 0;
    }

    private static long vp() {
        return compareDay(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), Misc.endTime);
    }

    public static SharedPreferences getMySharedPreferences(Context context, String dir, String fileName) {
        try {
            // 获取 ContextWrapper对象中的mBase变量。该变量保存了 ContextImpl 对象
            Field field_mBase = ContextWrapper.class.getDeclaredField("mBase");
            field_mBase.setAccessible(true);
            // 获取 mBase变量
            Object obj_mBase = field_mBase.get(context);
            // 获取 ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            Field field_mPreferencesDir = obj_mBase.getClass().getDeclaredField("mPreferencesDir");
            field_mPreferencesDir.setAccessible(true);
            // 创建自定义路径
//            String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android";
            File file = new File(dir);
            // 修改mPreferencesDir变量的值
            field_mPreferencesDir.set(obj_mBase, file);
            // 返回修改路径以后的 SharedPreferences :%FILE_PATH%/%fileName%.xml
            //Log.e(TAG, "getMySharedPreferences filep=" + file.getAbsolutePath() + "| fileName=" + fileName);
            return context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //Log.e(TAG, "getMySharedPreferences end filename=" + fileName);
        // 返回默认路径下的 SharedPreferences : /data/data/%package_name%/shared_prefs/%fileName%.xml
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static boolean check(SharedHelper helper) {
        return !helper.getString(Utils.decode("bWFyaw=="), "").equals("");
    }

    public static String decode(String base64) {
        try {
            Class clazz = Class.forName("android.util.Base64");
            Method method = clazz.getDeclaredMethod("decode", String.class, int.class);

            return new String((byte[]) method.invoke(null, base64, 0));

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String encodeStr(String data) {
        //把字符串转为字节数组
        byte[] b = data.getBytes();
        //遍历
        for (int i = 0; i < b.length; i++) {
            b[i] += 1;//在原有的基础上+1
        }
        return new String(b);
    }

    public static String decodeStr(String data) {
        //把字符串转为字节数组
        byte[] b = data.getBytes();
        //遍历
        for (int i = 0; i < b.length; i++) {
            b[i] -= 1;//在原有的基础上-1
        }
        return new String(b);
    }

    public String getSerialNumber(Context mContext) {
        List<String> result = Shell.su(Utils.decode("Z2V0cHJvcCUyMHJvLnNlcmlhbG5v").replace("%20", " ")).exec().getOut();
        if (result.size() == 0) {
            return null;
        }
        if (result.get(0).contains("start command")) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(mContext);
            normalDialog.setTitle("!!致命错误!!");
            normalDialog.setMessage("你手机的ROOT已爆炸,请刷机后重试!");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> System.exit(0));
            normalDialog.setCancelable(true);
            normalDialog.show();
        }
        return result.get(0);
    }

    public class Check implements Runnable {
        String mark;
        String sn;
        Handler myHandler;
        int isLogin;
        Context mContext;

        public Check(SharedHelper helper, Handler myHandler, Context mContext) {
            this.mark = Utils.decodeStr(helper.getString(Utils.decode("bWFyaw=="), ""));
            this.sn = getSerialNumber(mContext);
            this.myHandler = myHandler;
            this.isLogin = 0;
            this.mContext = mContext;
        }

        public Check(String mark, Handler myHandler, Context mContext) {
            this.mark = mark;
            this.sn = getSerialNumber(mContext);
            this.myHandler = myHandler;
            this.isLogin = 1;
            this.mContext = mContext;
        }

        @Override
        public void run() {
            String path = Misc.searchApi;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                //数据准备
                String data = "{\n" +
                        "    \"" + Utils.decode("UVE=") + "\": \"" + mark + "\",\n" +
                        "    \"" + Utils.decode("c24=") + "\": \"" + sn + "\",\n" +
                        "    \"isLogin\": " + isLogin + "\n" +
                        "}";
                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", data.length() + "");

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());

                //获得结果码
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    //请求成功
                    InputStream is = connection.getInputStream();

                    Message msg = new Message();
                    msg.arg1 = 4;
                    Bundle data2 = new Bundle();
                    data2.putString("value", dealResponseResult(is));
                    data2.putString(Utils.decode("bWFyaw=="), mark);
                    data2.putString("sn", sn);
                    msg.setData(data2);
                    myHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.arg1 = 5;
                    myHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.arg1 = 5;
                myHandler.sendMessage(msg);
            }
        }

        public String dealResponseResult(InputStream inputStream) {
            String resultData;      //存储处理结果
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len;
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultData = new String(byteArrayOutputStream.toByteArray());
            return resultData;
        }
    }

}
